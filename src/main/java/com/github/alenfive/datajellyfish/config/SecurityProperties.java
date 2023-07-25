package com.github.alenfive.datajellyfish.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "spring.security")
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityProperties {

    /**
     * 是否启用鉴权
     */
    private boolean enabled = true;

    /**
     * 鉴权类型
     */
    private SecurityType type = SecurityType.BASIC;

    /**
     * 基础鉴权方式：内存账号鉴权
     */
    private BasicProperties basic = BasicProperties.builder().username("root").password("root").build();

    /**
     * Ldap鉴权方式
     */
    private LdapProperties ldap;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LdapProperties {
        private String url;
        private String searchFilter;
        private String username;
        private String password;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BasicProperties {
        private String username;
        private String password;
    }
}
