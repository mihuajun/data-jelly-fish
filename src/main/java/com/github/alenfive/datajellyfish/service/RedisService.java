package com.github.alenfive.datajellyfish.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Copyright: Copyright (c) 2019  ALL RIGHTS RESERVED.
 * @Author: 米华军
 * @CreateDate: 2020/9/13 14:41
 * @UpdateDate: 2020/9/13 14:41
 * @UpdateRemark: init
 * @Version: 1.0
 * @menu
 */
@Service
@Slf4j
public class RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final String pushStatusKey = "rocket-ssc:push-status:";

    public void setErrMsg(String table, String msg){
        String key = pushStatusKey+table;
        stringRedisTemplate.opsForValue().set(key,msg,1, TimeUnit.DAYS);
    }

    public String getErrMsg(String table){
        String key = pushStatusKey+table;
        return stringRedisTemplate.opsForValue().get(key);
    }

    /***
     * @param key
     * @param value 当前时间+超时时间
     * @return 锁住返回true
     */
    public boolean lock(String key,String value){
        if(stringRedisTemplate.opsForValue().setIfAbsent(key,value)){//setNX 返回boolean
            return true;
        }
        //如果锁超时 ***
        String currentValue = stringRedisTemplate.opsForValue().get(key);
        if(!StringUtils.isEmpty(currentValue) && Long.parseLong(currentValue)<System.currentTimeMillis()){
            //获取上一个锁的时间
            String oldvalue  = stringRedisTemplate.opsForValue().getAndSet(key,value);
            if(!StringUtils.isEmpty(oldvalue)&&oldvalue.equals(currentValue)){
                return true;
            }
        }
        return false;
    }
    /***
     * 解锁
     * @param key
     * @param value
     * @return
     */
    public void unlock(String key,String value){
        try {
            String currentValue = stringRedisTemplate.opsForValue().get(key);
            if(!StringUtils.isEmpty(currentValue)&&currentValue.equals(value)){
                stringRedisTemplate.opsForValue().getOperations().delete(key);
            }
        } catch (Exception e) {
            log.error("解锁异常");
        }
    }
}
