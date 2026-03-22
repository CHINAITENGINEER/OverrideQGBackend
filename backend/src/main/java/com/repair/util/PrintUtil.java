package com.repair.util;

import com.repair.entity.RepairOrder;
import com.repair.entity.User;
import com.repair.enums.Priority;
import com.repair.enums.RepairStatus;
import com.repair.enums.Role;

import java.util.List;

/**
 * 控制台打印工具类
 * <p>
 * 统一管理所有表格/详情的输出格式，保持 UI 层代码整洁。
 */
public class PrintUtil {

    private PrintUtil() {}

    private static final String SEP = "+--------+--------------------+----------+--------+----------+-------------------+------+";

    /**
     * 打印报修单列表（表格形式）
     *
     * @param orders 报修单集合
     */
    public static void printOrderList(List<RepairOrder> orders) {
        if (orders == null || orders.isEmpty()) {
            System.out.println("  [ 暂无报修记录 ]");
            return;
        }
        System.out.println(SEP);
        System.out.printf("| %-6s | %-18s | %-8s | %-6s | %-8s | %-17s | %-4s |%n",
                "ID", "设备类型", "楼栋/房间", "优先级", "状态", "创建时间", "评分");
        System.out.println(SEP);
        for (RepairOrder o : orders) {
            String loc      = o.getBuilding() + "-" + o.getRoomNo();
            String priority = Priority.fromCode(o.getPriority()) != null
                    ? Priority.fromCode(o.getPriority()).getDesc() : "?";
            String status   = RepairStatus.fromCode(o.getStatus()) != null
                    ? RepairStatus.fromCode(o.getStatus()).getDesc() : "?";
            String rating   = o.getRating() != null ? o.getRating() + "星" : "-";
            String time     = o.getCreatedAt() != null
                    ? o.getCreatedAt().toString().replace("T", " ").substring(0, 16) : "-";
            System.out.printf("| %-6d | %-18s | %-8s | %-6s | %-8s | %-17s | %-4s |%n",
                    o.getId(), o.getDeviceType(), loc, priority, status, time, rating);
        }
        System.out.println(SEP);
    }

    /**
     * 打印报修单详情
     *
     * @param o 报修单
     */
    public static void printOrderDetail(RepairOrder o) {
        System.out.println("\n====== 报修单详情 ======");
        System.out.println("  报修单ID  : " + o.getId());
        System.out.println("  宿舍位置  : " + o.getBuilding() + " 楼 " + o.getRoomNo() + " 室");
        System.out.println("  设备类型  : " + o.getDeviceType());
        System.out.println("  问题描述  : " + o.getDescription());
        String priority = Priority.fromCode(o.getPriority()) != null
                ? Priority.fromCode(o.getPriority()).getDesc() : "?";
        System.out.println("  优先级    : " + priority);
        String status = RepairStatus.fromCode(o.getStatus()) != null
                ? RepairStatus.fromCode(o.getStatus()).getDesc() : "?";
        System.out.println("  当前状态  : " + status);
        System.out.println("  管理员备注: " + (o.getRemark() != null ? o.getRemark() : "暂无"));
        System.out.println("  评分      : " + (o.getRating() != null ? o.getRating() + " 星" : "暂未评分"));
        System.out.println("  创建时间  : " + (o.getCreatedAt() != null ? o.getCreatedAt().toString().replace("T", " ") : "-"));
        System.out.println("  最后修改  : " + (o.getUpdatedAt() != null ? o.getUpdatedAt().toString().replace("T", " ") : "-"));
        System.out.println("=======================");
    }

    /**
     * 打印用户基本信息
     *
     * @param user 用户对象
     */
    public static void printUserInfo(User user) {
        System.out.println("\n====== 我的信息 ======");
        System.out.println("  账号  : " + user.getAccount());
        String role = Role.fromCode(user.getRole()) != null
                ? Role.fromCode(user.getRole()).getDesc() : "?";
        System.out.println("  角色  : " + role);
        if (user.getBuilding() != null) {
            System.out.println("  宿舍  : " + user.getBuilding() + " 楼 " + user.getRoomNo() + " 室");
        }
        System.out.println("  注册时间: " + (user.getCreatedAt() != null
                ? user.getCreatedAt().toString().replace("T", " ") : "-"));
        System.out.println("=====================");
    }
}
