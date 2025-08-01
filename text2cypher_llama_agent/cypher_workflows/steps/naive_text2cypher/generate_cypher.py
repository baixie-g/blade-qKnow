# from nt import system
import sys
from llama_index.core import ChatPromptTemplate
# from cypher_workflows.shared.utils import get_neo4j_schema_str
# import os

GENERATE_SYSTEM_TEMPLATE = """Given an input question, convert it to a Cypher query. No pre-amble.
Do not wrap the response in any backticks or anything else. Respond with a Cypher statement only!"""

# GENERATE_USER_TEMPLATE = """You are a Neo4j expert. Given an input question, create a syntactically correct Cypher query to run.
# Do not wrap the response in any backticks or anything else. Respond with a Cypher statement only!
# Here is the schema information
# {schema}

# Below are a number of examples of questions and their corresponding Cypher queries.

# {fewshot_examples}

# User input: {question}
# Cypher query:"""

GENERATE_USER_TEMPLATE = """You are a Neo4j expert. Given an input question, create a syntactically correct Cypher query to run.
Do not wrap the response in any backticks or anything else. Respond with a Cypher statement only!
Here is the schema information
{schema}

Note: When querying nested properties such as d.attributes.cause, you should use the syntax d.`attributes.cause`.

Below are a number of examples of questions and their corresponding Cypher queries.

{fewshot_examples}

User input: {question}
Cypher query:"""


async def generate_cypher_step(llm, graph_store, subquery, fewshot_examples):
    # 直接用环境变量获取数据库连接参数
    # uri = os.getenv("NEO4J_URI", "bolt://localhost:7687")
    # username = os.getenv("NEO4J_USERNAME", "neo4j")
    # password = os.getenv("NEO4J_PASSWORD", "12345678")
    # database = os.getenv("NEO4J_DATABASE", "neo4j")
    # schema = get_neo4j_schema_str(uri, username, password, database, exclude_types=["Actor", "Director"])
    
    # schema = graph_store.get_schema_str(exclude_types=["Actor", "Director"])
    # print(f"-> 成功获取 schema 为: {schema}")
    # sys.exit(0)
    schema = {
        'metadata': {
            'constraint': [],
            'index': []
        },
        'node_props': {
            'Check': ['type', 'aliases', 'name', 'definition'],
            'Department': ['type', 'aliases', 'name', 'definition'],
            'Disease': [
                'attributes.cured_prob',
                'definition',
                'attributes.cure_department',
                'type',
                'attributes.cure_lasttime',
                'attributes.cure_way',
                'attributes.easy_get',
                'attributes.cause',
                'name',
                'aliases',
                'attributes.prevent'
            ],
            'Drug': ['definition', 'type', 'aliases', 'name'],
            'Food': ['definition', 'type', 'aliases', 'name'],
            'Producer': ['aliases', 'name', 'definition', 'type'],
            'Symptom': ['type', 'aliases', 'name', 'definition']
        },
        'rel_props': {
            'acompany_with': ['name'],
            'belongs_to': ['name'],
            'common_drug': ['name'],
            'do_eat': ['name'],
            'drugs_of': ['name'],
            'has_symptom': ['name'],
            'need_check': ['name'],
            'no_eat': ['name'],
            'recommand_drug': ['name'],
            'recommand_eat': ['name']
        },
        'relationships': [
            '(:Disease)-[:recommand_eat]->(:Food)',
            '(:Disease)-[:no_eat]->(:Food)',
            '(:Disease)-[:do_eat]->(:Food)',
            '(:Department)-[:belongs_to]->(:Department)',
            '(:Disease)-[:common_drug]->(:Drug)',
            '(:Producer)-[:drugs_of]->(:Drug)',
            '(:Disease)-[:recommand_drug]->(:Drug)',
            '(:Disease)-[:need_check]->(:Check)',
            '(:Disease)-[:has_symptom]->(:Symptom)',
            '(:Disease)-[:acompany_with]->(:Disease)',
            '(:Disease)-[:belongs_to]->(:Department)'
        ]
    }
    generate_cypher_msgs = [
        ("system", GENERATE_SYSTEM_TEMPLATE),
        ("user", GENERATE_USER_TEMPLATE),
    ]
    text2cypher_prompt = ChatPromptTemplate.from_messages(generate_cypher_msgs)

    response = await llm.achat(
        text2cypher_prompt.format_messages(
            question=subquery, schema=schema, fewshot_examples=fewshot_examples
        )
    )

    return response.message.content
    
    # prompt_content = text2cypher_prompt.format_messages(
    #     question=subquery, schema=schema, fewshot_examples=fewshot_examples
    # )
    # print("\n===== 最终发送给LLM的Cypher提示词1 =====\n")
    # print(prompt_content)
    # sys.exit(0)

    # return ""