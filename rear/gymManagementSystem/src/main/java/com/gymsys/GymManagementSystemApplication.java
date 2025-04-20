package com.gymsys;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gymsys.repository")
public class GymManagementSystemApplication {

    /**
     * 应用程序入口点
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(GymManagementSystemApplication.class, args);
    }
}