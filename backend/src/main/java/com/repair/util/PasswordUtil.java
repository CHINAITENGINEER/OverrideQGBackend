package com.repair.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * 密码工具类（BCrypt 加密）
 * <p>
 * 为什么选择 BCrypt 而不是 MD5？
 *   - MD5 是哈希算法，速度快，但已有彩虹表可以反查
 *   - BCrypt 内置随机盐（salt），每次加密同一个密码结果都不同
 *   - BCrypt 计算慢（可调工作因子），暴力破解成本极高
 *   - 工业界标准做法，Spring Security 默认也用 BCrypt
 */
public class PasswordUtil {

    private PasswordUtil() {}

    /**
     * 对明文密码进行 BCrypt 加密
     * <p>
     * BCrypt.gensalt() 会生成一个随机盐，自动嵌入加密结果中，
     * 所以同一密码每次调用结果都不同，但都能被 verify() 验证通过。
     *
     * @param rawPassword 用户输入的明文密码
     * @return 加密后的密码字符串（约60字符），直接存入数据库
     */
    public static String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    /**
     * 验证密码是否正确
     * <p>
     * BCrypt 会从 encodedPassword 中提取盐值，
     * 再对 rawPassword 做同样的加密，然后比较结果。
     *
     * @param rawPassword     用户输入的明文密码
     * @param encodedPassword 数据库中存储的加密密码
     * @return true=密码正确，false=密码错误
     */
    public static boolean verify(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
