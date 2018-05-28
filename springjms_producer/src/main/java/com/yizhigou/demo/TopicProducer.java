package com.yizhigou.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@Component
public class TopicProducer {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired//注入消息的类型
    private Destination topicTextDestination;

    public void sendTestMessage(String text){
        jmsTemplate.send(topicTextDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(text);
            }
        });
    }
}
