package com.yizhigou.page.service.impl;

import com.yizhigou.mapper.TbGoodsDescMapper;
import com.yizhigou.mapper.TbGoodsMapper;
import com.yizhigou.mapper.TbItemCatMapper;
import com.yizhigou.mapper.TbItemMapper;
import com.yizhigou.page.service.ItemPageService;
import com.yizhigou.pojo.TbGoods;
import com.yizhigou.pojo.TbGoodsDesc;
import com.yizhigou.pojo.TbItem;
import com.yizhigou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;

import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService{
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private TbGoodsMapper tbGoodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean genItemHtml(Long goodsId) {
        //1读取模板文件
        Configuration configuration = freeMarkerConfig.getConfiguration();
        try {
            //找到模板
            Template template = configuration.getTemplate("item.ftl");
            Map dateModel=new HashMap();
            //查询tbgoods信息
            TbGoods goods = tbGoodsMapper.selectByPrimaryKey(goodsId); //2 查询数据库  查询出商品的spu信息
            //3 把信息添加到模板中
            dateModel.put("goods",goods);
            //生成面包屑 商品类别
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dateModel.put("goodsDesc",goodsDesc);
            String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();//一级分类名称
            String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();//二级分类名称
            String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();//三级分类名称
            //生成到页面中
            dateModel.put("itemCat1",itemCat1);
            dateModel.put("itemCat2",itemCat2);
            dateModel.put("itemCat3",itemCat3);
            //查询sku列表信息
            TbItemExample example=new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");//审核通过的商品
            criteria.andGoodsIdEqualTo(goodsId);//
            example.setOrderByClause("is_default desc");//排序 默认选项
            List<TbItem> itemList = itemMapper.selectByExample(example);
            dateModel.put("itemList",itemList);
            //4 生产模板
            Writer out=new FileWriter(pagedir+goodsId+".html");
            template.process(dateModel,out);
            out.close();
            return  true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteItemHtml(Long[] goodsId) {
        //使用File类的方法
        try {
            for(Long id:goodsId){
                new File(pagedir+".html").delete();
            }
            return  true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
