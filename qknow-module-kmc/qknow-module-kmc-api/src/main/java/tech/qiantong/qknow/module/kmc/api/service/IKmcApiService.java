package tech.qiantong.qknow.module.kmc.api.service;

import java.util.List;

public interface IKmcApiService {
    /**
     * 获得全部知识文件列表
     * @return
     */
    public List<Object> getKmcDocumentList();

    public List<Object> getKmcDocumentListByIds(List<Long> ids);

    public List<Object> getCategoryTreeList(Object kmcCategoryDO);
}
