package com.github.alenfive.datajellyfish.controller;

import com.github.alenfive.datajellyfish.entity.IgnoreWrapper;
import com.github.alenfive.datajellyfish.entity.test.TestRequest;
import com.github.alenfive.datajellyfish.entity.test.TestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据请求实例
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    /**
     * 数据提供方
     * 对对返回数据进行排序，以便实现增量查询
     * @param testRequest
     * @return
     */
    @IgnoreWrapper
    @PostMapping("/producer")
    public TestResult producer(@RequestBody TestRequest testRequest){
        log.info("test producer params:{}",testRequest);
        TestResult result = new TestResult();
        List<Map<String,Object>> data = new ArrayList<>();
        Map<String,Object> item1 = new HashMap<>();
        item1.put("id",1);
        item1.put("name","张1");
        item1.put("time",new Date());

        Map<String,Object> item2 = new HashMap<>();
        item2.put("id",2);
        item2.put("name","张2");
        item2.put("time",new Date());

        Map<String,Object> item3 = new HashMap<>();
        item3.put("id",3);
        item3.put("name","张3");
        item3.put("time",new Date());

        data.add(item1);data.add(item2);data.add(item3);

        List<Map<String,Object>> incData = data.stream().filter(item->Integer.valueOf(item.get("id").toString())>Integer.valueOf(testRequest.getOffset())).limit(1).collect(Collectors.toList());

        result.setCode("0");
        result.setData(incData);
        return result;
    }

    /**
     * 数据消费方
     * 数据处理正确要求返回code=0,code!=0时 会触发重新推送
     * @return
     */
    @IgnoreWrapper
    @PostMapping("/consumer")
    public TestResult consumer(@RequestBody Map<String,Object> record){
        log.info("test consumer params:{}",record);
        TestResult result = new TestResult();
        result.setCode("0");
        return result;
    }
}
