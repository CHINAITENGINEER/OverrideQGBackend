package com.repair.ui;

import com.repair.constant.MsgConst;
import com.repair.entity.RepairOrder;
import com.repair.entity.User;
import com.repair.enums.DeviceType;
import com.repair.enums.Priority;
import com.repair.enums.RepairStatus;
import com.repair.service.RepairService;
import com.repair.service.UserService;
import com.repair.util.PrintUtil;
import com.repair.util.ValidateUtil;

import java.util.List;
import java.util.Scanner;

/**
 * 学生端菜单处理类
 * <p>
 * 负责：
 *   1. 展示学生菜单
 *   2. 读取用户输入，调用对应 Service 方法
 *   3. 将结果反馈给用户
 * <p>
 * 注意：UI 层只负责「交互」，不写业务逻辑，业务判断全在 Service 层。
 */
public class StudentMenu {

    private final Scanner scanner;
    private final UserService userService;
    private final RepairService repairService;

    /** 当前登录的学生对象 */
    private User currentUser;

    public StudentMenu(Scanner scanner, User currentUser,
                       UserService userService, RepairService repairService) {
        this.scanner = scanner;
        this.currentUser = currentUser;
        this.userService = userService;
        this.repairService = repairService;
    }

    /**
     * 进入学生菜单主循环
     * <p>
     * 首次登录且未绑定宿舍时，强制提示绑定。
     */
    public void show() {
        // 首次登录提示绑定宿舍
        if (currentUser.getBuilding() == null || currentUser.getBuilding().isBlank()) {
            System.out.println("\n[ 提示 ] 您尚未绑定宿舍，请先绑定宿舍后再使用报修功能！");
            bindDorm();
        }

        boolean running = true;
        while (running) {
            printMenu();
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> bindDorm();
                case "2" -> createOrder();
                case "3" -> viewMyOrders();
                case "4" -> cancelOrder();
                case "5" -> rateOrder();
                case "6" -> changePassword();
                case "7" -> viewMyInfo();
                case "0" -> running = false;
                default  -> System.out.println(MsgConst.INPUT_ERROR);
            }
        }
    }

    // ─────────────────────── 菜单打印 ───────────────────────

    private void printMenu() {
        System.out.println("\n" + MsgConst.LINE);
        System.out.println("  学生菜单  |  账号: " + currentUser.getAccount());
        System.out.println(MsgConst.LINE);
        System.out.println("  1. 绑定/修改宿舍");
        System.out.println("  2. 创建报修单");
        System.out.println("  3. 查看我的报修记录");
        System.out.println("  4. 取消报修单");
        System.out.println("  5. 对已完成的报修评分");
        System.out.println("  6. 修改密码");
        System.out.println("  7. 查看我的信息");
        System.out.println("  0. 退出登录");
        System.out.println(MsgConst.LINE);
        System.out.print("请选择操作：");
    }

    // ─────────────────────── 各功能实现 ─────────────────────────

    /** 1. 绑定/修改宿舍 */
    private void bindDorm() {
        System.out.println("\n===== 绑定/修改宿舍 =====");
        System.out.print("请输入楼栋（如：北3）：");
        String building = scanner.nextLine().trim();
        System.out.print("请输入房间号（如：306）：");
        String roomNo = scanner.nextLine().trim();

        if (building.isBlank() || roomNo.isBlank()) {
            System.out.println(MsgConst.INPUT_ERROR);
            return;
        }

        userService.bindDorm(currentUser.getId(), building, roomNo);
        // 更新内存中的用户对象，避免下次还提示绑定
        currentUser.setBuilding(building);
        currentUser.setRoomNo(roomNo);
        System.out.println("[ 绑定成功 ] 宿舍：" + building + " 楼 " + roomNo + " 室");
    }

    /** 2. 创建报修单 */
    private void createOrder() {
        // 如果未绑定宿舍，先绑定
        if (currentUser.getBuilding() == null || currentUser.getBuilding().isBlank()) {
            System.out.println("[ 提示 ] 请先绑定宿舍！");
            bindDorm();
            return;
        }

        System.out.println("\n===== 创建报修单 =====");

        // 选择设备类型
        System.out.println("请选择设备类型：");
        for (DeviceType dt : DeviceType.values()) {
            System.out.println("  " + dt.getCode() + ". " + dt.getDesc());
        }
        System.out.print("请输入编号：");
        int deviceCode;
        try {
            deviceCode = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println(MsgConst.INPUT_ERROR);
            return;
        }
        DeviceType deviceType = DeviceType.fromCode(deviceCode);
        if (deviceType == null) {
            System.out.println(MsgConst.INPUT_ERROR);
            return;
        }

        // 填写问题描述
        System.out.print("请描述问题（详细说明有助于快速维修）：");
        String description = scanner.nextLine().trim();
        if (description.isBlank()) {
            System.out.println("[ 问题描述不能为空 ]");
            return;
        }

        // 选择优先级
        System.out.println("请选择优先级：");
        for (Priority p : Priority.values()) {
            System.out.println("  " + p.getCode() + ". " + p.getDesc());
        }
        System.out.print("请输入编号（默认1-低）：");
        String prioInput = scanner.nextLine().trim();
        int priority = 1;
        if (!prioInput.isBlank()) {
            try {
                priority = Integer.parseInt(prioInput);
                if (Priority.fromCode(priority) == null) {
                    System.out.println("[ 优先级无效，已使用默认值：低 ]");
                    priority = 1;
                }
            } catch (NumberFormatException e) {
                System.out.println("[ 输入非数字，已使用默认值：低 ]");
            }
        }

        Long orderId = repairService.createOrder(
                currentUser.getId(),
                currentUser.getBuilding(),
                currentUser.getRoomNo(),
                deviceType.getDesc(),
                description,
                priority
        );
        System.out.println("[ 报修成功 ] 报修单ID：" + orderId + "，我们会尽快安排维修！");
    }

    /** 3. 查看我的报修记录 */
    private void viewMyOrders() {
        System.out.println("\n===== 我的报修记录 =====");
        List<RepairOrder> orders = repairService.getMyOrders(currentUser.getId());
        PrintUtil.printOrderList(orders);

        if (orders != null && !orders.isEmpty()) {
            System.out.print("\n输入报修单ID查看详情（直接回车跳过）：");
            String idInput = scanner.nextLine().trim();
            if (!idInput.isBlank()) {
                try {
                    Long orderId = Long.parseLong(idInput);
                    RepairOrder detail = repairService.getOrderDetail(orderId);
                    if (detail != null && detail.getStudentId().equals(currentUser.getId())) {
                        PrintUtil.printOrderDetail(detail);
                    } else {
                        System.out.println(MsgConst.ORDER_NOT_FOUND);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(MsgConst.INPUT_ERROR);
                }
            }
        }
    }

    /** 4. 取消报修单 */
    private void cancelOrder() {
        System.out.println("\n===== 取消报修单 =====");
        System.out.print("请输入要取消的报修单ID：");
        try {
            Long orderId = Long.parseLong(scanner.nextLine().trim());
            boolean success = repairService.cancelOrder(orderId, currentUser.getId());
            if (success) {
                System.out.println("[ 取消成功 ]");
            } else {
                System.out.println("[ 取消失败 ] 报修单不存在、不属于您，或状态非【待处理】");
            }
        } catch (NumberFormatException e) {
            System.out.println(MsgConst.INPUT_ERROR);
        }
    }

    /** 5. 对已完成的报修评分 */
    private void rateOrder() {
        System.out.println("\n===== 维修评分 =====");
        System.out.print("请输入要评分的报修单ID：");
        try {
            Long orderId = Long.parseLong(scanner.nextLine().trim());
            System.out.print("请输入评分（1-5星）：");
            int rating = Integer.parseInt(scanner.nextLine().trim());
            if (rating < 1 || rating > 5) {
                System.out.println("[ 评分范围为 1-5 ]");
                return;
            }
            boolean success = repairService.rateOrder(orderId, currentUser.getId(), rating);
            if (success) {
                System.out.println("[ 评分成功 ] 感谢您的反馈！");
            } else {
                System.out.println("[ 评分失败 ] 报修单不存在、不属于您、未完成或已评过分");
            }
        } catch (NumberFormatException e) {
            System.out.println(MsgConst.INPUT_ERROR);
        }
    }

    /** 6. 修改密码 */
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

    /** 7. 查看我的信息 */
    private void viewMyInfo() {
        User freshUser = userService.getById(currentUser.getId());
        if (freshUser != null) {
            PrintUtil.printUserInfo(freshUser);
        }
    }
}
