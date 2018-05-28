package com.yizhigou;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/applicationContext-redis.xml")
public class TestSet {
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void setValue(){
        redisTemplate.boundSetOps("hkey1").add("曹操");
        redisTemplate.boundSetOps("hkey1").add("张飞");
        redisTemplate.boundSetOps("hkey1").add("关羽");
        redisTemplate.boundSetOps("hkey1").add("张飞");
    }
    @Test
    public void getValue(){
        Set hkey1 = redisTemplate.boundSetOps("hkey1").members();
        System.out.println(hkey1);
    }
    @Test
    public void deleteValue(){
        redisTemplate.boundSetOps("hkey1").remove("张飞");
    }
    @Test
    public void deleteAll(){
        redisTemplate.delete("hkey1");
    }
}
