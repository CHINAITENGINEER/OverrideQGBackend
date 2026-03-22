package com.repair.ui;

import com.repair.constant.MsgConst;
import com.repair.entity.RepairOrder;
import com.repair.entity.User;
import com.repair.enums.RepairStatus;
import com.repair.service.RepairService;
import com.repair.service.UserService;
import com.repair.util.PrintUtil;
import com.repair.util.ValidateUtil;

import java.util.List;
import java.util.Scanner;

/**
 * 管理员端菜单处理类
 * <p>
 * 负责管理员的全部终端交互：
 *   查看报修单、更新状态、删除报修单、统计、修改密码。
 */
public class AdminMenu {

    private final Scanner scanner;
    private final UserService userService;
    private final RepairService repairService;
    private final User currentUser;

    public AdminMenu(Scanner scanner, User currentUser,
                     UserService userService, RepairService repairService) {
        this.scanner = scanner;
        this.currentUser = currentUser;
        this.userService = userService;
        this.repairService = repairService;
    }

    /** 进入管理员菜单主循环 */
    public void show() {
        boolean running = true;
        while (running) {
            printMenu();
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> viewAllOrders();
                case "2" -> viewOrderDetail();
                case "3" -> updateOrderStatus();
                case "4" -> deleteOrder();
                case "5" -> viewStats();
                case "6" -> changePassword();
                case "7" -> viewMyInfo();
                case "0" -> running = false;
                default  -> System.out.println(MsgConst.INPUT_ERROR);
            }
        }
    }

    // ─────────────────── 菜单打印 ───────────────────

    private void printMenu() {
        System.out.println("\n" + MsgConst.LINE);
        System.out.println("  管理员菜单  |  工号: " + currentUser.getAccount());
        System.out.println(MsgConst.LINE);
        System.out.println("  1. 查看所有报修单（可按状态筛选）");
        System.out.println("  2. 查看报修单详情");
        System.out.println("  3. 更新报修单状态");
        System.out.println("  4. 删除报修单");
        System.out.println("  5. 报修统计");
        System.out.println("  6. 修改密码");
        System.out.println("  7. 查看我的信息");
        System.out.println("  0. 退出登录");
        System.out.println(MsgConst.LINE);
        System.out.print("请选择操作：");
    }

    // ─────────────────── 1. 查看所有报修单 ───────────────────

    private void viewAllOrders() {
        System.out.println("\n===== 报修单列表 =====");
        System.out.println("状态筛选（直接回车=全部）：");
        for (RepairStatus s : RepairStatus.values()) {
            System.out.println("  " + s.getCode() + ". " + s.getDesc());
        }
        System.out.print("请输入状态编号：");
        String input = scanner.nextLine().trim();

        Integer statusFilter = null;
        if (!input.isBlank()) {
            try {
                int code = Integer.parseInt(input);
                if (RepairStatus.fromCode(code) != null) {
                    statusFilter = code;
                } else {
                    System.out.println("[ 状态无效，展示全部 ]");
                }
            } catch (NumberFormatException e) {
                System.out.println("[ 输入非数字，展示全部 ]");
            }
        }

        List<RepairOrder> orders = repairService.getAllOrders(statusFilter);
        PrintUtil.printOrderList(orders);
    }

    // ─────────────────── 2. 查看报修单详情 ───────────────────

    private void viewOrderDetail() {
        System.out.println("\n===== 报修单详情 =====");
        System.out.print("请输入报修单ID：");
        try {
            Long orderId = Long.parseLong(scanner.nextLine().trim());
            RepairOrder order = repairService.getOrderDetail(orderId);
            if (order == null) {
                System.out.println(MsgConst.ORDER_NOT_FOUND);
            } else {
                PrintUtil.printOrderDetail(order);
            }
        } catch (NumberFormatException e) {
            System.out.println(MsgConst.INPUT_ERROR);
        }
    }

    // ─────────────────── 3. 更新报修单状态 ───────────────────

    private void updateOrderStatus() {
        System.out.println("\n===== 更新报修单状态 =====");
        System.out.print("请输入报修单ID：");
        try {
            Long orderId = Long.parseLong(scanner.nextLine().trim());
            RepairOrder order = repairService.getOrderDetail(orderId);
            if (order == null) {
                System.out.println(MsgConst.ORDER_NOT_FOUND);
                return;
            }
            PrintUtil.printOrderDetail(order);

            System.out.println("\n请选择新状态：");
            for (RepairStatus s : RepairStatus.values()) {
                System.out.println("  " + s.getCode() + ". " + s.getDesc());
            }
            System.out.print("请输入状态编号：");
            int newStatus = Integer.parseInt(scanner.nextLine().trim());
            if (RepairStatus.fromCode(newStatus) == null) {
                System.out.println(MsgConst.INPUT_ERROR);
                return;
            }

            System.out.print("请输入备注说明（可直接回车跳过）：");
            String remark = scanner.nextLine().trim();

            boolean success = repairService.updateStatus(
                    orderId, currentUser.getId(), newStatus,
                    remark.isBlank() ? null : remark);
            System.out.println(success ? MsgConst.OPERATION_SUCCESS : MsgConst.OPERATION_FAIL);
        } catch (NumberFormatException e) {
            System.out.println(MsgConst.INPUT_ERROR);
        }
    }

    // ─────────────────── 4. 删除报修单 ───────────────────

    private void deleteOrder() {
        System.out.println("\n===== 删除报修单 =====");
        System.out.print("请输入要删除的报修单ID：");
        try {
            Long orderId = Long.parseLong(scanner.nextLine().trim());
            RepairOrder order = repairService.getOrderDetail(orderId);
            if (order == null) {
                System.out.println(MsgConst.ORDER_NOT_FOUND);
                return;
            }
            PrintUtil.printOrderDetail(order);
            System.out.print("确认删除？此操作不可撤销（输入 yes 确认）：");
            String confirm = scanner.nextLine().trim();
            if (!"yes".equalsIgnoreCase(confirm)) {
                System.out.println("[ 已取消删除 ]");
                return;
            }
            boolean success = repairService.deleteOrder(orderId);
            System.out.println(success ? MsgConst.OPERATION_SUCCESS : MsgConst.OPERATION_FAIL);
        } catch (NumberFormatException e) {
            System.out.println(MsgConst.INPUT_ERROR);
        }
    }

    // ─────────────────── 5. 报修统计 ───────────────────

    private void viewStats() {
        System.out.println("\n===== 报修单统计 =====");
        int[] counts = repairService.countByStatus();
        System.out.println("  待处理: " + counts[0] + " 条");
        System.out.println("  处理中: " + counts[1] + " 条");
        System.out.println("  已完成: " + counts[2] + " 条");
        System.out.println("  已取消: " + counts[3] + " 条");
        int total = counts[0] + counts[1] + counts[2] + counts[3];
        System.out.println("  合  计: " + total + " 条");
        System.out.println("=====================");
    }

    // ─────────────────── 6. 修改密码 ───────────────────

    private void changePassword() {
        System.out.println("\n===== 修改密码 =====");
        System.out.print("请输入旧密码：");
        String oldPwd = scanner.nextLine().trim();
        System.out.print("请输入新密码（6-20位，含字母和数字）：");
        String newPwd = scanner.nextLine().trim();
        System.out.print("请确认新密码：");
        String confirmPwd = scanner.nextLine().trim();

        if (!newPwd.equals(confirmPwd)) {
            System.out.println(MsgConst.PASSWORD_NOT_MATCH);
            return;
        }
        if (!ValidateUtil.isPasswordValid(newPwd)) {
            System.out.println(MsgConst.PASSWORD_FORMAT_ERROR);
            return;
        }
        boolean success = userService.changePassword(currentUser.getId(), oldPwd, newPwd);
        System.out.println(success ? MsgConst.OPERATION_SUCCESS : MsgConst.PASSWORD_WRONG);
    }

    // ─────────────────── 7. 查看我的信息 ───────────────────

    private void viewMyInfo() {
        User freshUser = userService.getById(currentUser.getId());
        if (freshUser != null) {
            PrintUtil.printUserInfo(freshUser);
        }
    }
}
