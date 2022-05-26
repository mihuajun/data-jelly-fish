package com.github.alenfive.datajellyfish.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class JsonConfig implements InitializingBean {

    @Autowired
    private ObjectMapper objectMapper;

    public class ObjectIdDeserializer  extends JsonDeserializer<ObjectId> {
        @Override
        public ObjectId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new ObjectId(p.getText());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(ObjectId.class, ToStringSerializer.instance);
        simpleModule.addDeserializer(ObjectId.class, new ObjectIdDeserializer());
        objectMapper.registerModule(simpleModule);

    }
}
