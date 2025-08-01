from typing import Any

from fastapi import Request
from jinja2 import pass_context


# force HTTPS in jinja templates
@pass_context
def urlx_for(
    context: dict,
    name: str,
    **path_params: Any,
) -> str:
    request: Request = context["request"]
    http_url = request.url_for(name, **path_params)
    if scheme := request.headers.get("x-forwarded-proto"):
        return http_url.replace(scheme=scheme)
    return http_url

from typing import List, Optional
from llama_index.core.embeddings import BaseEmbedding
from transformers import AutoTokenizer, AutoModel
import torch
import torch.nn.functional as F


class BgeEmbedding(BaseEmbedding):
    def __init__(
        self,
        model_path: str,
        device: str = "cuda" if torch.cuda.is_available() else "cpu",
        normalize: bool = True,
        **kwargs
    ):
        super().__init__(**kwargs)
        
        # 直接赋值给实例属性而不是通过Pydantic
        self.device = device
        self.normalize = normalize
        self.tokenizer = AutoTokenizer.from_pretrained(model_path)
        self.model = AutoModel.from_pretrained(model_path).to(self.device)
        self._model_dim = self.model.config.hidden_size  # 从模型配置中获取维度

    @classmethod
    def class_name(cls):
        return "BgeEmbedding"

    def _mean_pooling(self, token_embeddings, attention_mask):
        input_mask_expanded = (
            attention_mask.unsqueeze(-1).expand(token_embeddings.size()).float()
        )
        embeddings = torch.sum(token_embeddings * input_mask_expanded, 1) / torch.clamp(
            input_mask_expanded.sum(1), min=1e-9
        )
        return embeddings

    def _get_query_embedding(self, query: str):
        inputs = self.tokenizer(
            query,
            padding=True,
            truncation=True,
            max_length=512,
            return_tensors="pt",
        ).to(self.device)
        with torch.no_grad():
            outputs = self.model(**inputs)
        embeddings = self._mean_pooling(
            outputs.last_hidden_state, inputs["attention_mask"]
        )
        if self.normalize:
            embeddings = F.normalize(embeddings, p=2, dim=1)
        return embeddings[0].cpu().tolist()

    def _get_text_embedding(self, text: str):
        return self._get_query_embedding(text)

    def _get_text_embeddings(self, texts: List[str]):
        return [self._get_text_embedding(text) for text in texts]

    async def _aget_query_embedding(self, query: str) -> List[float]:
        return self._get_query_embedding(query)

    async def _aget_text_embedding(self, text: str) -> List[float]:
        return self._get_text_embedding(text)

    async def _aget_text_embeddings(self, texts: List[str]) -> List[List[float]]:
        return self._get_text_embeddings(texts)

    @property
    def dimensions(self) -> int:
        """返回嵌入向量的维度"""
        return self._model_dim