package com.repair.enums;

/**
 * 报修优先级枚举
 * 管理员查询列表时可按优先级排序，高优先级优先处理
 */
public enum Priority {

    LOW(1, "低"),
    MEDIUM(2, "中"),
    HIGH(3, "高");

    private final int code;
    private final String desc;

    Priority(int code, String desc) {
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
     * 根据 code 获取枚举
     */
    public static Priority fromCode(int code) {
        for (Priority p : Priority.values()) {
            if (p.code == code) {
                return p;
            }
        }
        return null;
    }
}
