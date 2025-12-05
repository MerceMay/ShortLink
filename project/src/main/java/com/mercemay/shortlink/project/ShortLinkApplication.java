package com.mercemay.shortlink.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mercemay.shortlink.project.dao.mapper")
public class ShortLinkApplication {
    public static void main(String[] args){
        SpringApplication.run(ShortLinkApplication.class);
    }
}
