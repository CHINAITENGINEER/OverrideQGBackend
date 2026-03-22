package com.repair.util;

import com.repair.constant.RegexConst;
import com.repair.enums.Role;

/**
 * 输入校验工具类
 * <p>
 * 封装正则匹配逻辑，让 Service 层调用更简洁。
 * 所有方法均为静态方法，不需要实例化。
 */
public class ValidateUtil {

    private ValidateUtil() {}

    /**
     * 校验账号格式是否符合指定角色的规则
     *
     * @param account 待校验的账号字符串
     * @param role    角色（STUDENT 或 ADMIN）
     * @return true=格式合法
     */
    public static boolean isAccountValid(String account, Role role) {
        if (account == null || account.isBlank()) {
            return false;
        }
        if (role == Role.STUDENT) {
            return account.matches(RegexConst.STUDENT_ACCOUNT);
        } else {
            return account.matches(RegexConst.ADMIN_ACCOUNT);
        }
    }

    /**
     * 校验密码格式：6-20位，必须包含字母和数字
     *
     * @param password 待校验密码
     * @return true=格式合法
     */
    public static boolean isPasswordValid(String password) {
        if (password == null || password.isBlank()) {
            return false;
        }
        return password.matches(RegexConst.PASSWORD);
    }

    /**
     * 根据账号前缀自动判断角色
     * <p>
     * 3125/3225 前缀 → 学生
     * 0025 前缀       → 管理员
     *
     * @param account 账号字符串
     * @return 对应角色，格式不符合任何规则则返回 null
     */
    public static Role detectRole(String account) {
        if (account == null) return null;
        if (account.matches(RegexConst.STUDENT_ACCOUNT)) return Role.STUDENT;
        if (account.matches(RegexConst.ADMIN_ACCOUNT))   return Role.ADMIN;
        return null;
    }
}
