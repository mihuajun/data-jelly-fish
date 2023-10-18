package com.github.alenfive.datajellyfish.security;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"org.pac4j.springframework.web","org.pac4j.springframework.component"})
public class Pac4jMvcConfig {
}
