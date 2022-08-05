package org.tfoweaver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description:
 * @title: SpringBootTest
 * @Author Star_Chen
 * @Date: 2022/8/4 17:54
 * @Version 1.0
 */
@SpringBootApplication
@MapperScan("org.tfoweaver")
public class SpringBootTest {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootTest.class);
    }
}
