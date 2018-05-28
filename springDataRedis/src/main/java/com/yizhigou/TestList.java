package com.yizhigou;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/applicationContext-redis.xml")
public class TestList {
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void setRightList(){
        redisTemplate.boundListOps("list1").rightPush("张荣");
        redisTemplate.boundListOps("list1").rightPush("张飞");
        redisTemplate.boundListOps("list1").rightPush("张飞");
    }
    @Test
    public void getList(){
        List list1 = redisTemplate.boundListOps("list1").range(0, 10);
        System.out.println(list1);
    }
    @Test
    public void setLeft(){
        redisTemplate.boundListOps("list1").leftPush("诸葛亮");
    }
    @Test
    public void searchList(){
        String str = (String)redisTemplate.boundListOps("list1").index(1);
        System.out.println(str);
    }
}
