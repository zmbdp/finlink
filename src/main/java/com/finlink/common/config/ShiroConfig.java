package com.finlink.common.config;

import com.finlink.common.shiro.BCryptCredentialsMatcher;
import com.finlink.common.filter.JwtFilter;
import com.finlink.common.shiro.UserRealm;
import org.apache.shiro.mgt.SecurityManager;
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
     * <p>配置 URL 拦截规则：/api/login 和 /api/unauth 匿名访问，其余走JWT认证</p>
     *
     * @param securityManager 安全管理器
     * @return ShiroFilterFactoryBean 实例
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 注册 JWT 过滤器
        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("jwt", new JwtFilter());
        shiroFilterFactoryBean.setFilters(filters);

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/api/login", "anon");
        filterChainDefinitionMap.put("/api/unauth", "anon");
        // 其他所有接口使用 JWT 过滤器
        filterChainDefinitionMap.put("/**", "jwt");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }
}