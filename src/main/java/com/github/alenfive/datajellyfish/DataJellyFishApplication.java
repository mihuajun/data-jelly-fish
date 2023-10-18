package com.github.alenfive.datajellyfish;

import com.github.alenfive.datajellyfish.service.DataJellyFishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@Slf4j
@SpringBootApplication (exclude = { HibernateJpaAutoConfiguration.class,DataSourceAutoConfiguration.class})
public class DataJellyFishApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DataJellyFishApplication.class, args);
    }

    @Autowired
    private DataJellyFishService dataJellyFishService;

    @Override
    public void run(String... args) {
        dataJellyFishService.taskReload();
        dataJellyFishService.run();
    }
}
