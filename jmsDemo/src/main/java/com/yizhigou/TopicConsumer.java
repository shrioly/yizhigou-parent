package com.yizhigou;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.IOException;

public class TopicConsumer {
    public static void main(String[] args) throws JMSException, IOException {
        //1 创建连接工厂
        ConnectionFactory connectionFactory=new ActiveMQConnectionFactory("tcp://192.168.177.128:61616");
        //2 获取连接
        Connection connection = connectionFactory.createConnection();
        //3 启动连接
        connection.start();
        //4 获取session     1  事务是否开启    2 消息的确认模式
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //创建消息队列的对象  Queue topic
        Topic topic=session.createTopic("test_topic");//消息队列的名称
        //获取消息消费者
        MessageConsumer consumer = session.createConsumer(topic);
        //取出消息
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                TextMessage textMessage= (TextMessage) message;
                try {
                    String text = textMessage.getText();
                    System.out.println("获取到的消息==="+text);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
        System.in.read();
        //关闭流
        consumer.close();
        session.close();
        connection.close();
    }
}
