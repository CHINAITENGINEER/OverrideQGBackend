package com.repair.enums;

/**
 * 设备类型枚举
 * 学生创建报修单时，从此枚举中选择设备类型
 */
public enum DeviceType {

    WATER(1, "水龙头/水管"),
    ELECTRICITY(2, "电路/插座"),
    DOOR(3, "门窗/门锁"),
    FURNITURE(4, "桌椅/床铺"),
    NETWORK(5, "网络/网线"),
    AC(6, "空调"),
    LIGHT(7, "灯具"),
    OTHER(8, "其他");

    private final int code;
    private final String desc;

    DeviceType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static DeviceType fromCode(int code) {
        for (DeviceType d : DeviceType.values()) {
            if (d.code == code) {
                return d;
            }
        }
        return null;
    }
}
