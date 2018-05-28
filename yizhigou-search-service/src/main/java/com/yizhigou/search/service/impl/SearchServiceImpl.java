package com.yizhigou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yizhigou.pojo.TbBrand;
import com.yizhigou.pojo.TbItem;
import com.yizhigou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service(timeout = 3000)//连接超时等待 3000毫秒
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap){
        Map<String,Object> map=new HashMap<>();
        String str=(String)searchMap.get("keywords");
        searchMap.put("keywords",str.replace(" ",""));
        System.out.println(str);
        //1 条件查询 （高亮显示）查询
        map.putAll(searchList(searchMap));
        // 2 查询商品分类
        List categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);
        //3 根据分类名称取出品牌列表和规格项列表
        if(categoryList.size()>0){
            map.putAll(searchBrandAndSpecList((String)categoryList.get(0)));
        }
        //根据分类名称查询品牌
        String categoryName=(String)searchMap.get("category");
        if(!"".equals(categoryName)){
            map.putAll(searchBrandAndSpecList(categoryName));
        }else{
            //没有的情况就是按照第一个分类名称作为查询条件
            map.putAll(searchBrandAndSpecList((String)categoryList.get(0)));
        }
        return map;
    }



    //搜索
    //抽离代码
    private Map  searchList(Map searchMap) {
        Map map=new HashMap<>();
      //定义查询条件
        //按照分类筛选
  /*      Query query=new SimpleQuery();
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);//执行搜索
        map.put("rows",page.getContent());//取出数据*/
        HighlightQuery query=new SimpleHighlightQuery();
        //1.1 按照分类筛选
        if(!"".equals(searchMap.get("category"))){
            Criteria criteria=new Criteria("item_category").is(searchMap.get("category"));
            //添加过滤条件
            FilterQuery filterQuery=new SimpleFilterQuery(criteria);
            query.addFilterQuery(filterQuery);
        }
        //1.2 按照品牌筛选
        if(!"".equals(searchMap.get("brand"))){
            Criteria criteria=new Criteria("item_brand").is(searchMap.get("brand"));
            //添加过滤条件
            FilterQuery filterQuery=new SimpleFilterQuery(criteria);
            query.addFilterQuery(filterQuery);
        }
        //1.3 规格参数过滤
        if(searchMap.get("spec")!=null){
            Map<String,String> specMap=(Map<String,String>) searchMap.get("spec");
            for (String key:specMap.keySet()){
                Criteria filterCriteria=new Criteria("item_spec_"+key).is(specMap.get(key));
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //1.4 按照价格进行过滤
        if(!"".equals(searchMap.get("price"))){
            String[] price=((String)searchMap.get("price")).split("-");//0-100
            if(!price[0].equals("0")){
                Criteria criteria=new Criteria("item_price").greaterThanEqual(price[0]);
                FilterQuery filterQuery=new SimpleFilterQuery(criteria);
                query.addFilterQuery(filterQuery);
            }
            //3000-*处理
            if(!price[1].equals("*")){
                Criteria criteria=new Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery filterQuery=new SimpleFilterQuery(criteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //1.5 分页
    Integer pageNo=(Integer)searchMap.get("pageNo");//获取当前页数
        if(pageNo==null){
            pageNo=1;
        }
        //获取每页条数
        Integer pageSize=(Integer)searchMap.get("pageSize");
        if(pageSize==null){
            pageSize=40;
        }
        query.setOffset((pageNo-1)*pageSize);//计算出从第几条开始显示
        query.setRows(pageSize);
        //1.6  排序
            //1 获取排序的方式
        String sortValue=(String)searchMap.get("sort");
            //2 获取排序的字段
        String sortFiled=(String)searchMap.get("sortFiled");
        if(sortValue.equals("ASC")){
            Sort sort=new Sort(Sort.Direction.ASC,"item_"+sortFiled);
            query.addSort(sort);
        }
        if(sortValue.equals("DESC")){
            Sort sort=new Sort(Sort.Direction.DESC,"item_"+sortFiled);
            query.addSort(sort);
        }
        //2 设置高亮显示字段
        HighlightOptions highlightOptions=new HighlightOptions().addField("item_title");
        //3 设置高亮显示的前缀和后缀 <em></em>
        highlightOptions.setSimplePrefix("<em style='color:red'>");//前缀
        highlightOptions.setSimplePostfix("</em>");
        //4 设置高亮显示的选项
        query.setHighlightOptions(highlightOptions);
        //按照关键字查询
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);//执行搜索
//5 设置高亮显示字段中的内容
        for(HighlightEntry<TbItem> h:page.getHighlighted()){//循环高亮入口
            List<HighlightEntry.Highlight> highlights = h.getHighlights();
        /*    for (HighlightEntry.Highlight maps:highlights){
                System.out.println(maps.getSnipplets().get(0)+"===============");
            }*/
        TbItem item=h.getEntity();//获取到实体对象
            if(h.getHighlights().size()>0&&h.getHighlights().get(0).getSnipplets().size()>0){
                //设置高亮的结果
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
            }
        }
        map.put("rows",page.getContent());
        map.put("totalPages",page.getTotalPages());//总页数
        map.put("total",page.getTotalElements());//总条数
        return map;
    }

     //查询商品分类
    private List searchCategoryList(Map searchMap){
        List list=new ArrayList();
        //1 设置查询条件
        Query query=new SimpleQuery();
        //2 设置搜索关键字
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //3 设置分组选项
        GroupOptions groupOptions=new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //4 得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //5 根据分组列得到分组结果
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //6 根据分组结果集找到入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        //7 取出分组结果放到list中
        for(GroupEntry<TbItem> entry:content){
            list.add(entry.getGroupValue());//获取到分组结果放到list中
        }
        return list;
    }

    @Autowired
    private RedisTemplate redisTemplate;
    //根据分类名称，查询分类列表和规格项列表
    private Map searchBrandAndSpecList(String categoryName){
        Map map=new HashMap();
        //取出分类模板id
        Long typeId=(Long) redisTemplate.boundHashOps("itemCat").get(categoryName);
        if(typeId!=null){
            //取出品牌列表
            List<TbBrand> brandList =(List<TbBrand>) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList",brandList);
            //取出规格列表
            List specList=(List)redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList",specList);
        }
        return map;
    }

    //更新索引库
    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }
//删除索引库
    @Override
    public void deleteByGoodsIds(List goodsIdsList) {
        Query query=new SimpleQuery();
        Criteria criteria=new Criteria("item_goodsid").in(goodsIdsList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

}
