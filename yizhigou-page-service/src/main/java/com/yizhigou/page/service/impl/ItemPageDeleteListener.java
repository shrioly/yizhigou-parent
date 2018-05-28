package com.yizhigou.page.service.impl;

import com.yizhigou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class ItemPageDeleteListener implements MessageListener {
    @Autowired
    ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        //获取消息
        ObjectMessage objectMessage= (ObjectMessage) message;
        try {
            Long[] goodsId=(Long[])objectMessage.getObject();
            //执行删除
            itemPageService.deleteItemHtml(goodsId);
            System.out.println("删除静态页面成功");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
