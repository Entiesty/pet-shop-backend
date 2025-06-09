package com.example.petshopbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@MapperScan("com.example.petshopbackend.mapper") // [不变] MyBatis-Plus的Mapper扫描路径
@EnableMongoRepositories(basePackages = "com.example.petshopbackend.repository.mongo") // [ADDED] 明确指定MongoDB的Repository扫描路径
public class PetShopBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetShopBackendApplication.class, args);
    }

}
