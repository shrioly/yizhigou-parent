package com.yizhigou.search.service;

import java.util.List;
import java.util.Map;

public interface SearchService {
    //搜索
    public Map<String,Object> search(Map searchMap);

    //更新索引库
    public void importList(List list);
    //删除索引库
    public void deleteByGoodsIds(List goodsIdsList);
}
