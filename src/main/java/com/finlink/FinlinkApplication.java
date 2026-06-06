package com.finlink;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class FinlinkApplication {
    public static void main(String[] args) {
        SpringApplication.run(FinlinkApplication.class, args);
        log.info("FinlinkApplication 启动成功.....");
    }
}