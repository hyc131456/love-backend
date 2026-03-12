package com.loveapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 心迹·情侣时光 后端启动类
 */
@SpringBootApplication
@MapperScan("com.loveapp.mapper")
public class LoveAppApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(LoveAppApplication.class, args);
        System.out.println("=========================================");
        System.out.println("  心迹·情侣时光 后端服务启动成功!");
        System.out.println("  访问地址: http://localhost:18888/api");
        System.out.println("=========================================");
    }
}
