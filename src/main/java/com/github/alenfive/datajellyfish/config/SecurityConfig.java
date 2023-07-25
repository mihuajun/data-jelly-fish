package com.github.alenfive.datajellyfish.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.headers().frameOptions().disable();
        http.csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        if (securityProperties.isEnabled()){
            web.ignoring().antMatchers("/test/**");
        }else {
            web.ignoring().antMatchers("/**");
        }
    }



    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        if (SecurityType.BASIC.equals(securityProperties.getType())){
            SecurityProperties.BasicProperties basicProperties = securityProperties.getBasic();
            auth.inMemoryAuthentication()
                    .passwordEncoder(new BCryptPasswordEncoder())
                    .withUser(basicProperties.getUsername())
                    .password(new BCryptPasswordEncoder().encode(basicProperties.getPassword()))
                    .roles("ADMIN");
        }else if (SecurityType.LDAP.equals(securityProperties.getType())){
            SecurityProperties.LdapProperties ldapProperties = securityProperties.getLdap();
            if (ldapProperties == null){
                throw new RuntimeException("ldap is not configured");
            }
            auth.ldapAuthentication()
                    .userSearchFilter(ldapProperties.getSearchFilter())
                    .contextSource()
                    .url(ldapProperties.getUrl())
                    .managerDn(ldapProperties.getUsername())
                    .managerPassword(ldapProperties.getPassword());
        }
    }
}
