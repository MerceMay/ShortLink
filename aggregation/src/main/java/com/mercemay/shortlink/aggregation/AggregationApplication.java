package com.mercemay.shortlink.aggregation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {
        "com.mercemay.shortlink.admin",
        "com.mercemay.shortlink.project",
        "com.mercemay.shortlink.aggregation"
})
@MapperScan(value = {
        "com.mercemay.shortlink.project.dao.mapper",
        "com.mercemay.shortlink.admin.dao.mapper"
})
public class AggregationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AggregationApplication.class, args);
    }
}
