package com.github.alenfive.datajellyfish.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alenfive.datajellyfish.entity.IgnoreWrapper;
import com.github.alenfive.datajellyfish.entity.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Date;

@RestControllerAdvice
public class ResultResponseBodyAdvice implements ResponseBodyAdvice {


    protected ObjectMapper objectMapper;


    public ResultResponseBodyAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        IgnoreWrapper ignoreWrapper = returnType.getMethodAnnotation(IgnoreWrapper.class);
        if (ignoreWrapper != null){
            return body;
        }

        Result result = new Result();
        result.setCode("0");
        result.setData(body);
        result.setAction(((ServletServerHttpRequest) request).getServletRequest().getRequestURI());
        result.setTimestamp(new Date());
        if (body instanceof String) {
            try {
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
                return this.objectMapper.writeValueAsString(result);
            } catch (JsonProcessingException e) {
                throw new HttpMessageNotWritableException("Could not write JSON: " + e.getOriginalMessage(), e);
            }
        }
        return result;
    }

}