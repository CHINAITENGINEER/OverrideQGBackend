package com.repair.service;

import com.repair.entity.User;

/**
 * 用户服务接口
 * <p>
 * 定义用户相关的业务方法。接口与实现分离是三层架构的核心原则：
 *   - 调用方（UI层）只依赖接口，不关心具体实现
 *   - 方便后期替换实现（例如从控制台版迁移到 Web 版）
 *   - 方便单元测试时 Mock
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param account  账号（学号/工号），由调用方完成格式校验
     * @param password 明文密码，由调用方完成格式校验
     * @param role     角色代码（0=学生，1=管理员）
     * @return true=注册成功，false=账号已存在
     */
    boolean register(String account, String password, int role);

    /**
     * 用户登录
     *
     * @param account  账号
     * @param password 明文密码
     * @return 登录成功返回 User 对象，账号不存在或密码错误返回 null
     */
    User login(String account, String password);

    /**
     * 修改密码
     *
     * @param userId      当前登录用户的ID
     * @param oldPassword 旧密码（明文），需验证通过才能修改
     * @param newPassword 新密码（明文）
     * @return true=修改成功，false=旧密码错误
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 绑定/修改宿舍信息（仅学生）
     *
     * @param userId   学生用户ID
     * @param building 楼栋
     * @param roomNo   房间号
     */
    void bindDorm(Long userId, String building, String roomNo);

    /**
     * 根据ID查询用户
     *
     * @param userId 用户ID
     * @return User 对象，找不到返回 null
     */
    User getById(Long userId);
}
