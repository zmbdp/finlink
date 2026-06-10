package com.finlink.common.config;

import com.finlink.common.filter.JwtFilter;
import com.finlink.common.security.BCryptCredentialsMatcher;
import com.finlink.common.security.UserRealm;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro 安全配置类
 * <p>配置 Shiro 的认证、授权、JWT过滤器链等核心组件</p>
 *
 * @author 稚名不带撇
 */
@Configuration
public class ShiroConfig {

    /**
     * 构建 Shiro 过滤链定义规则
     * <p>
     * 配置系统中各 URL 路径对应的访问控制策略，包括：
     * <ul>
     *     <li>Swagger/Knife4j 文档相关路径：匿名访问（anon）</li>
     *     <li>登录相关接口：匿名访问（anon）</li>
     *     <li>其他所有路径：JWT 认证（jwt）</li>
     * </ul>
     * <p>
     * 使用 {@link LinkedHashMap} 保证规则按添加顺序匹配，确保 "/**" 通配规则最后生效
     *
     * @return 过滤链定义映射，key 为 URL 路径模式，value 为对应的过滤器名称
     */
    private Map<String, String> buildFilterChainDefinitions() {
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // Swagger/Knife4j 相关路径放行
        filterChainDefinitionMap.put("/doc.html", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        filterChainDefinitionMap.put("/v2/api-docs", "anon");
        filterChainDefinitionMap.put("/v2/api-docs-ext", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        // 登录相关路径放行
        filterChainDefinitionMap.put("/api/login", "anon");
        filterChainDefinitionMap.put("/api/unauth", "anon");
        // 其他所有接口使用 JWT 过滤器
        filterChainDefinitionMap.put("/**", "jwt");
        return filterChainDefinitionMap;
    }

    /**
     * BCrypt 密码匹配器
     *
     * @return BCryptCredentialsMatcher 实例
     */
    @Bean
    public BCryptCredentialsMatcher bcryptCredentialsMatcher() {
        return new BCryptCredentialsMatcher();
    }

    /**
     * 自定义 Realm
     * <p>注入 BCrypt 密码匹配器</p>
     *
     * @param matcher BCrypt 密码匹配器
     * @return UserRealm 实例
     */
    @Bean
    public UserRealm userRealm(BCryptCredentialsMatcher matcher) {
        UserRealm userRealm = new UserRealm();
        userRealm.setCredentialsMatcher(matcher);
        return userRealm;
    }

    /**
     * SecurityManager 安全管理器
     * <p>关闭Session管理，使用JWT无状态认证</p>
     *
     * @param userRealm 自定义 Realm
     * @return DefaultWebSecurityManager 实例
     */
    @Bean
    public DefaultWebSecurityManager securityManager(UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm);

        // 关闭 Session 管理，使用无状态 JWT
        DefaultSessionManager sessionManager = new DefaultSessionManager();
        sessionManager.setSessionValidationSchedulerEnabled(false);
        securityManager.setSessionManager(sessionManager);

        return securityManager;
    }

    /**
     * Shiro 过滤器工厂
     * <p>配置 URL 拦截规则：
     * <ul>
     *     <li>Swagger/Knife4j 文档路径：匿名访问</li>
     *     <li>登录相关接口（/api/login、/api/unauth）：匿名访问</li>
     *     <li>其他所有路径：JWT 认证</li>
     * </ul>
     *
     * @param securityManager 安全管理器
     * @return ShiroFilterFactoryBean 实例
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 注册 JWT 过滤器
        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("jwt", new JwtFilter());
        shiroFilterFactoryBean.setFilters(filters);

        shiroFilterFactoryBean.setFilterChainDefinitionMap(buildFilterChainDefinitions());
        return shiroFilterFactoryBean;
    }
}