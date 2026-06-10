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
 * <p>继承自 Shiro 的 {@link BasicHttpAuthenticationFilter}，用于拦截需要认证的请求，
 * 从请求头获取 JWT Token 并进行校验，校验通过后将用户身份绑定到当前线程上下文中。
 * 支持两种 Token 格式：
 * <ul>
 *     <li>Authorization: Bearer {token}</li>
 *     <li>Authorization: {token}</li>
 * </ul>
 * 同时还处理了跨域请求（CORS）和 OPTIONS 预检请求。</p>
 *
 * @author 稚名不带撇
 */
public class JwtFilter extends BasicHttpAuthenticationFilter {

    /**
     * 判断是否允许访问
     * <p>从请求头获取 Token，检查其有效性，若有效则尝试登录认证。
     * 认证通过返回 true 允许访问，否则返回 false 拒绝访问。</p>
     *
     * @param request     Servlet 请求对象
     * @param response    Servlet 响应对象
     * @param mappedValue 拦截路径上配置的值
     * @return true-允许访问，false-拒绝访问
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = getToken(httpRequest);

        // Token 为空或已过期，拒绝访问
        if (token == null || JwtUtil.isExpired(token)) {
            return false;
        }

        // Token 有效，创建 JwtToken 对象并尝试登录认证
        JwtToken jwtToken = new JwtToken(token);
        try {
            getSubject(request, response).login(jwtToken);
            return true;
        } catch (Exception e) {
            // 登录失败（如用户被禁用、Token 解析异常等），拒绝访问
            return false;
        }
    }

    /**
     * 访问被拒绝时的处理
     * <p>当 isAccessAllowed 返回 false 时执行，返回 401 未授权的 JSON 格式响应。</p>
     *
     * @param request  Servlet 请求对象
     * @param response Servlet 响应对象
     * @return false-表示已处理完成，不再继续执行后续过滤器链
     * @throws Exception 可能抛出的异常
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
     * <p>从 Authorization 请求头中提取 JWT Token，支持两种格式：
     * <ul>
     *     <li>Authorization: Bearer {token}</li>
     *     <li>Authorization: {token}</li>
     * </ul>
     * 若请求头不存在或为空，则返回 null。</p>
     *
     * @param request HTTP 请求对象
     * @return JWT Token 字符串，若不存在则返回 null
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
     * 前置处理
     * <p>在请求到达拦截器逻辑之前执行，用于处理跨域（CORS）和 OPTIONS 预检请求。
     * 对于 OPTIONS 预检请求，直接返回 200 OK 状态并阻止后续处理。</p>
     *
     * @param request  Servlet 请求对象
     * @param response Servlet 响应对象
     * @return true-继续执行后续过滤器链，false-中止执行
     * @throws Exception 可能抛出的异常
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 设置跨域响应头，允许所有来源、指定方法和请求头
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");

        // OPTIONS 预检请求直接返回 200 OK，不继续执行
        if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpResponse.setStatus(HttpStatus.OK.value());
            return false;
        }

        // 非 OPTIONS 请求，继续执行父类的前置处理
        return super.preHandle(request, response);
    }
}