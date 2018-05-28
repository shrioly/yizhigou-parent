package com.yizhigou.demo;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;


public class TopicMessageListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage= (TextMessage) message;
        try {
            System.out.println(textMessage.getText()+"获取到的信息");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
