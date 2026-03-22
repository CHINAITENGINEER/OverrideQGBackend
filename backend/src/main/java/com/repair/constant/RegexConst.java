package com.repair.constant;

/**
 * 正则表达式常量类
 * 集中管理所有正则，避免硬编码散落在代码各处
 */
public class RegexConst {

    private RegexConst() {
        // 工具类，禁止实例化
    }

    /**
     * 学生学号正则：前缀必须为 3125 或 3225，后跟 6 位数字，共 10 位
     * 示例：3125004123、3225001001
     */
    public static final String STUDENT_ACCOUNT = "^(3125|3225)\\d{6}$";

    /**
     * 管理员工号正则：前缀必须为 0025，后跟 6 位数字，共 10 位
     * 示例：0025004123
     */
    public static final String ADMIN_ACCOUNT = "^0025\\d{6}$";

    /**
     * 密码正则：至少 6 位，字母和数字组合
     */
    public static final String PASSWORD = "^(?=.*[a-zA-Z])(?=.*\\d).{6,20}$";
}
