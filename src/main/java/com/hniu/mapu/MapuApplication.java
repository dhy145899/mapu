package com.hniu.mapu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.hniu.mapu.mapper")
@EnableTransactionManagement
public class MapuApplication {

    public static void main(String[] args) {
        SpringApplication.run(MapuApplication.class, args);
    }

}
