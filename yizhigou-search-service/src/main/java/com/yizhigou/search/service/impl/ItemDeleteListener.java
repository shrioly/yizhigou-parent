package com.yizhigou.search.service.impl;

import com.yizhigou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

@Component
public class ItemDeleteListener implements MessageListener {
    @Autowired
    private SearchService searchService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage= (ObjectMessage) message;
        try {
            Long [] ids=(Long[])objectMessage.getObject();
            searchService.deleteByGoodsIds(Arrays.asList(ids));
            //删除索引库数据
            System.out.println("删除索引库数据成功.........");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
