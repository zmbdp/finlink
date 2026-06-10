package com.finlink.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.finlink.user.domain.dto.LoginDTO;
import com.finlink.user.domain.dto.LoginReqDTO;
import com.finlink.user.domain.entity.User;
import com.finlink.user.mapper.UserMapper;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link UserServiceImpl} 集成测试类。
 * <p>
 * 基于 {@link SpringBootTest} 启动完整 Spring 上下文，对用户查询、登录及登出功能进行端到端验证。
 * 测试前后会自动绑定/解绑 Shiro {@link SecurityManager}，并清理测试用户数据，保证用例之间互不干扰。
 * </p>
 *
 * @author 稚名不带撇
 */
@SpringBootTest
@DisplayName("用户服务集成测试")
class UserServiceImplTest {

    /**
     * 测试用户 ID。
     */
    private static final Long TEST_USER_ID = 1L;

    /**
     * 测试用户名。
     */
    private static final String TEST_USERNAME = "testuser";

    /**
     * 测试用户密码（BCrypt 加密后的密文）。
     */
    private static final String TEST_PASSWORD = "$2a$10$ylLNJCDTo2LXW9JePLhMouTLMWSN8JKLDCAODUvmGV6to.kX.DtlW";

    /**
     * 测试用户昵称。
     */
    private static final String TEST_NICKNAME = "测试用户";

    /**
     * 用户数据访问层。
     */
    @Autowired
    private UserMapper userMapper;

    /**
     * 被测用户服务实现。
     */
    @Autowired
    private UserServiceImpl userService;

    /**
     * Shiro 安全管理器，用于在测试线程中绑定安全上下文。
     */
    @Autowired
    private SecurityManager securityManager;

    /**
     * 每个测试方法执行前的初始化操作。
     * <p>
     * 绑定 Shiro {@link SecurityManager} 到当前线程，清理历史测试用户后插入一条基础用户记录。
     * </p>
     */
    @BeforeEach
    void setUp() {
        // 绑定 Shiro 安全上下文，否则登录/登出等操作可能因缺少 SecurityManager 而失败
        ThreadContext.bind(securityManager);

        // 清理历史测试数据，防止主键或唯一索引冲突
        userMapper.deleteById(TEST_USER_ID);
        userMapper.delete(new LambdaQueryWrapper<User>().eq(User::getUsername, TEST_USERNAME));

        // 构造基础测试用户并入库
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setUsername(TEST_USERNAME);
        user.setPassword(TEST_PASSWORD);
        user.setNickname(TEST_NICKNAME);
        user.setStatus(1);
        userMapper.insert(user);
    }

    /**
     * 每个测试方法执行后的清理操作。
     * <p>
     * 解绑并移除线程中的 Shiro 安全上下文，删除由 {@code setUp()} 插入的测试用户数据。
     * </p>
     */
    @AfterEach
    void tearDown() {
        // 解绑 Shiro 安全上下文，避免影响后续测试线程
        ThreadContext.unbindSecurityManager();
        ThreadContext.remove();

        // 清理测试数据
        userMapper.deleteById(TEST_USER_ID);
        userMapper.delete(new LambdaQueryWrapper<User>().eq(User::getUsername, TEST_USERNAME));
    }

    /**
     * 测试根据用户名查询用户 —— 用户存在场景。
     * <p>
     * 期望：返回非空 {@link User} 对象，且用户名、ID、昵称均与预期一致。
     * </p>
     */
    @Test
    @DisplayName("测试根据用户名查询用户 - 用户存在")
    void testFindByUsername_UserExists() {
        // 执行查询
        User result = userService.findByUsername(TEST_USERNAME);

        // 校验返回结果的核心字段
        assertNotNull(result, "查询结果不应为空");
        assertEquals(TEST_USERNAME, result.getUsername(), "用户名应匹配");
        assertEquals(TEST_USER_ID, result.getId(), "用户 ID 应匹配");
        assertEquals(TEST_NICKNAME, result.getNickname(), "用户昵称应匹配");
    }

    /**
     * 测试根据用户名查询用户 —— 用户不存在场景。
     * <p>
     * 期望：返回 {@code null}。
     * </p>
     */
    @Test
    @DisplayName("测试根据用户名查询用户 - 用户不存在")
    void testFindByUsername_UserNotExists() {
        // 使用数据库中不可能存在的用户名进行查询
        User result = userService.findByUsername("nonexistent_user_99999");
        assertNull(result, "不存在的用户应返回 null");
    }

    /**
     * 测试用户登录 —— 成功场景。
     * <p>
     * 期望：返回非空 {@link LoginDTO}，包含匹配的用户名、昵称及有效的 JWT Token。
     * </p>
     */
    @Test
    @DisplayName("测试用户登录 - 成功")
    void testLogin_Success() {
        // 构造登录请求参数，密码需与数据库中 BCrypt 密文对应的明文一致
        LoginReqDTO loginReqDTO = new LoginReqDTO();
        loginReqDTO.setUsername(TEST_USERNAME);
        loginReqDTO.setPassword("password");
        loginReqDTO.setRememberMe(false);

        // 执行登录
        LoginDTO result = userService.login(loginReqDTO);

        // 校验登录结果及 Token 生成情况
        assertNotNull(result, "登录结果不应为空");
        assertEquals(TEST_USERNAME, result.getUsername(), "登录返回的用户名应匹配");
        assertEquals(TEST_NICKNAME, result.getNickname(), "登录返回的昵称应匹配");
        assertNotNull(result.getToken(), "登录应返回有效的 JWT Token");
    }

    /**
     * 测试用户登出。
     * <p>
     * 期望：登出操作正常执行，不抛出任何异常。
     * </p>
     */
    @Test
    @DisplayName("测试用户登出")
    void testLogout() {
        // 验证登出方法不会抛出异常即可
        assertDoesNotThrow(() -> userService.logout(), "登出操作不应抛出异常");
    }
}
