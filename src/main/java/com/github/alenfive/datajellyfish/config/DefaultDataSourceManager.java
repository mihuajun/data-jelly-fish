package com.github.alenfive.datajellyfish.config;

import com.github.alenfive.rocketapi.datasource.DataSourceDialect;
import com.github.alenfive.rocketapi.datasource.DataSourceManager;
import com.github.alenfive.rocketapi.datasource.MongoDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认数据源管理器
 */
@Component
public class DefaultDataSourceManager extends DataSourceManager {

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void init() {
        Map<String, DataSourceDialect> dialects = new HashMap<>();
        dialects.put("mongodb",new MongoDataSource(mongoTemplate,true));
        super.setDialectMap(dialects);
    }
}
