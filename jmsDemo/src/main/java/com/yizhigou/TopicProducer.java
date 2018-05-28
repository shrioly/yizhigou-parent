package com.yizhigou;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class TopicProducer {
    public static void main(String[] args) throws JMSException {
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
        //创建消息生产者
        MessageProducer producer = session.createProducer(topic);
        //创建消息
        TextMessage textMessage = session.createTextMessage("欢迎来到易直购世界，想买啥就买啥---订阅式发送");
        producer.send(textMessage);
        //关闭流
        producer.close();
        session.close();
        connection.close();
    }
}
