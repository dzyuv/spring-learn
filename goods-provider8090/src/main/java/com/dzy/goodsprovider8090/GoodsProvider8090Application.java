package com.dzy.goodsprovider8090;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan
public class GoodsProvider8090Application {

    public static void main(String[] args) {
        SpringApplication.run(GoodsProvider8090Application.class, args);
    }

}
