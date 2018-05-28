package com.yizhigou.test;

import com.yizhigou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-solr.xml")
public class TestSolr {
    @Autowired
    private SolrTemplate solrTemplate;
    //添加单条数据
    @Test
    public void testAdd(){
        TbItem item=new TbItem();
        item.setId(1L);
        item.setGoodsId(20L);
        item.setTitle("华为p20");
        item.setPrice(new BigDecimal(20));
        item.setImage("aaa.jpg");
        solrTemplate.saveBean(item);
        solrTemplate.commit();
    }
    //查询单条
    @Test
    public void  queryById(){
        TbItem byId = solrTemplate.getById(1, TbItem.class);
        System.out.println(byId.getTitle());
    }
    //按主键删除
     @Test
    public void deleteById(){
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }
    //添加多条数据
    @Test
    public void testAndAll(){
        List<TbItem> list=new ArrayList<>();
        for(int i=0;i<100;i++){
            TbItem item=new TbItem();
            item.setId(1L+i);
            item.setGoodsId(20L);
            item.setTitle("华为p20"+i);
            item.setPrice(new BigDecimal(2000+i));
            item.setImage("aaa.jpg");
            list.add(item);
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }
    //分页查询
    @Test
    public void queryPage(){
        Query query=new SimpleQuery("*:*");
        query.setOffset(20);//起始条数 index从0开始的
        query.setRows(20);//每页显示的条数
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        System.out.println("总条数"+tbItems.getTotalElements());
        List<TbItem> content = tbItems.getContent();
        showTbitem(content);
    }
    //条件查询
    @Test
    public void queryTest(){
        Query query=new SimpleQuery("*:*");
        //查询时按含有1和含有0的查询
        Criteria criteria=new Criteria("item_title").contains("10");
        criteria= criteria.and("item_title").contains("5");
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        System.out.println("总条数"+tbItems.getTotalElements());
        List<TbItem> content = tbItems.getContent();
        showTbitem(content);
    }

    @Test
    public void deleteAll(){
        Query query=new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    private void showTbitem(List<TbItem> list){
        for(TbItem item:list){
            System.out.println("标题"+item.getTitle()+"价格"+item.getPrice());
        }
    }

    @Test
    public void deleAll(){
        Query query=new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }


}
