package com.repair.enums;

/**
 * 报修单状态枚举
 * 状态流转：待处理 -> 处理中 -> 已完成
 *         待处理 -> 已取消（学生主动取消）
 */
public enum RepairStatus {

    PENDING(0, "待处理"),
    PROCESSING(1, "处理中"),
    COMPLETED(2, "已完成"),
    CANCELLED(3, "已取消");

    private final int code;
    private final String desc;

    RepairStatus(int code, String desc) {
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
    public static RepairStatus fromCode(int code) {
        for (RepairStatus s : RepairStatus.values()) {
            if (s.code == code) {
                return s;
            }
        }
        return null;
    }
}
