package com.finlink.common.filter;

import com.finlink.common.domain.JwtToken;
import com.finlink.common.utils.JwtUtil;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT 认证过滤器
 * <p>从请求头获取Token并校验，兼容带Bearer前缀和不带前缀两种格式</p>
 *
 * @author 稚名不带撇
 */
public class JwtFilter extends BasicHttpAuthenticationFilter {

    /**
     * 判断是否允许访问
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = getToken(httpRequest);

        if (token == null || JwtUtil.isExpired(token)) {
            return false;
        }

        // Token 有效，创建认证令牌
        JwtToken jwtToken = new JwtToken(token);
        try {
            getSubject(request, response).login(jwtToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 访问被拒绝时的处理
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setContentType("application/json;charset=UTF-8");
        httpResponse.setStatus(401);
        httpResponse.getWriter().write("{\"code\":401001,\"errMsg\":\"请先登录\"}");
        return false;
    }

    /**
     * 从请求头获取 Token
     * <p>兼容两种格式：
     * Authorization: Bearer xxx
     * Authorization: xxx
     * </p>
     */
    private String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isEmpty()) {
            return null;
        }
        // 兼容带 "Bearer " 前缀和不带的情况
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }

    /**
     * 跨域处理
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 允许跨域
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");

        // OPTIONS 预检请求直接放行
        if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpResponse.setStatus(HttpStatus.OK.value());
            return false;
        }

        return super.preHandle(request, response);
    }
}