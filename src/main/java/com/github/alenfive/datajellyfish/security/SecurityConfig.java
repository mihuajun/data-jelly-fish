package com.github.alenfive.datajellyfish.security;

import org.pac4j.core.config.Config;
import org.pac4j.springframework.security.web.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

import java.net.URI;

@EnableWebSecurity
public class SecurityConfig {

    @Configuration
    public static class ProtectedWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        private Config config;

        protected void configure(final HttpSecurity http) throws Exception {
            final SecurityFilter filter = new SecurityFilter(config,null,"isAuthenticated");
            http
                    .addFilterAfter(filter, BasicAuthenticationFilter.class)
                    .csrf().disable()
                    .antMatcher("/**")
                    .headers().frameOptions().disable()
                    .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
                    ;
        }


        @Override
        public void configure(WebSecurity web) {
            web.ignoring().antMatchers("/test/**","/callback","/error*","/logout","/login","/data-jelly-fish/**");
        }
    }

}