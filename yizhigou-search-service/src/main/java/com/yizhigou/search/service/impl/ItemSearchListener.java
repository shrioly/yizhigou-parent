package com.yizhigou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.yizhigou.pojo.TbItem;
import com.yizhigou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

@Component
public class ItemSearchListener  implements MessageListener {
    @Autowired
    private SearchService searchService;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage= (TextMessage) message;//获取到的信息

        try {
            String jsonstr  = textMessage.getText();
        //类型转换  转换为list
            List<TbItem> itemList = JSON.parseArray(jsonstr, TbItem.class);
            for (TbItem item:itemList){
                System.out.println("使用Activemq实现索引库同步===");
                Map specMap=JSON.parseObject(item.getSpec());//转换规格项
                item.setSpecMap(specMap);
            }
            searchService.importList(itemList);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
