package com.finlink;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 财务流水管理系统启动类
 * <p>Spring Boot 应用程序入口，负责初始化 Spring 容器并启动应用</p>
 *
 * @author 稚名不带撇
 */
@Slf4j
@SpringBootApplication
public class FinlinkApplication {

    /**
     * 应用程序主方法
     * <p>启动 Spring Boot 应用，初始化 Spring 容器，加载配置和 Bean</p>
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(FinlinkApplication.class, args);
        log.info("FinlinkApplication 启动成功.....");
    }
}