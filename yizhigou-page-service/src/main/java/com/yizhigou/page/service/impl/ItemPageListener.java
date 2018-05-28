package com.yizhigou.page.service.impl;

import com.yizhigou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class ItemPageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage= (TextMessage) message;//接受消息
        try {
          Long goodsId=  Long.parseLong(textMessage.getText());//取出消息内容
            itemPageService.genItemHtml(goodsId);//生产静态页面
            System.out.println("静态页面生成成功");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
