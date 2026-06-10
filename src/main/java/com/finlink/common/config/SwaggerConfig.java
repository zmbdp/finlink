package com.finlink.common.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger/Knife4j 配置类
 * <p>
 * 配置接口文档相关信息，包括：
 * <ul>
 *     <li>API 基本信息（标题、描述、版本等）</li>
 *     <li>接口扫描范围</li>
 *     <li>UI 配置（Knife4j 增强功能）</li>
 * </ul>
 * <p>
 * <b>访问地址：</b>
 * <ul>
 *     <li>Knife4j UI: <a href="http://localhost:10030/doc.html">http://localhost:10030/doc.html</a></li>
 *     <li>Swagger UI: <a href="http://localhost:10030/swagger-ui/index.html">http://localhost:10030/swagger-ui/index.html</a></li>
 * </ul>
 *
 * @author 稚名不带撇
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
public class SwaggerConfig {

    /**
     * 创建 API 文档
     * <p>
     * 配置 Docket Bean，用于生成接口文档。<br>
     * 扫描所有使用 @ApiOperation 注解的方法作为 API 接口。
     *
     * @return Docket 实例
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 扫描所有带 @ApiOperation 注解的方法
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                // 扫描所有的 controller
                .apis(RequestHandlerSelectors.basePackage("com.finlink"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 构建 API 基本信息
     * <p>
     * 配置 API 文档的基本信息，包括：
     * <ul>
     *     <li>标题</li>
     *     <li>描述</li>
     *     <li>版本号</li>
     *     <li>联系人信息</li>
     * </ul>
     *
     * @return ApiInfo 实例
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("FinLink 账户流水系统 API 文档")
                .description("FinLink 账户流水管理系统的接口文档，包含用户认证、账户管理、流水查询等功能")
                .contact(new Contact("稚名不带撇", "", ""))
                .version("1.0.0")
                .build();
    }
}
