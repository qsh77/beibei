package com.beibeijava;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.beibeijava.mapper")
public class BeibeiJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeibeiJavaApplication.class, args);
        System.out.println("=================================");
        System.out.println("贝贝家政服务平台启动成功！");
        System.out.println("接口文档地址：http://localhost:8080/swagger-ui.html");
        System.out.println("测试接口：http://localhost:8080/api/auth/test");
        System.out.println("=================================");
    }

}
