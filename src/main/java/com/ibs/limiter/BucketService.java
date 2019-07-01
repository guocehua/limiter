package com.ibs.limiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BucketService {
    @Autowired
    private RedisTemplate redisTemplate;
    public Bucket getBucket(String key){
        return (Bucket)redisTemplate.opsForValue().get(key);
    }
    public void setBucket(String key, Bucket bucket){
       redisTemplate.opsForValue().set(key,bucket);
    }
}
