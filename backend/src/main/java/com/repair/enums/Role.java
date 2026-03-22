package com.repair.enums;

/**
 * 用户角色枚举
 * 数据库中存储 code 值（0 或 1）
 */
public enum Role {

    /** 学生：学号前缀 3125 或 3225 */
    STUDENT(0, "学生"),

    /** 管理员/维修人员：工号前缀 0025 */
    ADMIN(1, "管理员");

    /** 存入数据库的值 */
    private final int code;

    /** 显示名称 */
    private final String desc;

    Role(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据 code 值获取对应枚举
     *
     * @param code 数据库中存储的值
     * @return 对应的 Role 枚举，找不到则返回 null
     */
    public static Role fromCode(int code) {
        for (Role role : Role.values()) {
            if (role.code == code) {
                return role;
            }
        }
        return null;
    }
}
