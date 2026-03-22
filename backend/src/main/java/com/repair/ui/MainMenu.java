package com.repair.ui;

import com.repair.constant.MsgConst;
import com.repair.entity.User;
import com.repair.enums.Role;
import com.repair.service.RepairService;
import com.repair.service.UserService;
import com.repair.util.ValidateUtil;

import java.util.Scanner;

/**
 * 主菜单处理类（登录/注册入口）
 * <p>
 * 程序启动后先进入此菜单，完成登录或注册后，
 * 再根据角色跳转到 StudentMenu 或 AdminMenu。
 */
public class MainMenu {

    private final Scanner scanner;
    private final UserService userService;
    private final RepairService repairService;

    public MainMenu(Scanner scanner, UserService userService, RepairService repairService) {
        this.scanner = scanner;
        this.userService = userService;
        this.repairService = repairService;
    }

    /** 主菜单循环入口 */
    public void start() {
        boolean running = true;
        while (running) {
            printWelcome();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> handleLogin();
                case "2" -> handleRegister();
                case "3" -> {
                    System.out.println("\n再见！欢迎下次使用宿舍报修管理系统。");
                    running = false;
                }
                default -> System.out.println(MsgConst.INPUT_ERROR);
            }
        }
    }

    // ─────────────────── 欢迎界面 ───────────────────

    private void printWelcome() {
        System.out.println("\n============================");
        System.out.println("   宿舍报修管理系统 v1.0");
        System.out.println("============================");
        System.out.println("  1. 登录");
        System.out.println("  2. 注册");
        System.out.println("  3. 退出");
        System.out.println("----------------------------");
        System.out.print("请选择操作（1-3）：");
    }

    // ─────────────────── 登录 ───────────────────

    private void handleLogin() {
        System.out.println("\n===== 用户登录 =====");
        System.out.print("请输入账号：");
        String account = scanner.nextLine().trim();
        System.out.print("请输入密码：");
        String password = scanner.nextLine().trim();

        User user = userService.login(account, password);
        if (user == null) {
            System.out.println("[ 登录失败 ] 账号不存在或密码错误，请重新尝试。");
            return;
        }

        Role role = Role.fromCode(user.getRole());
        System.out.println("[ 登录成功 ] 欢迎您，" + account
                + "！角色：" + (role != null ? role.getDesc() : "未知"));

        // 根据角色进入对应菜单
        if (role == Role.STUDENT) {
            new StudentMenu(scanner, user, userService, repairService).show();
        } else if (role == Role.ADMIN) {
            new AdminMenu(scanner, user, userService, repairService).show();
        }
    }

    // ─────────────────── 注册 ───────────────────

    private void handleRegister() {
        System.out.println("\n===== 用户注册 =====");

        // 1. 选择角色
        System.out.println("请选择角色：");
        System.out.println("  1. 学生（学号前缀：3125 或 3225）");
        System.out.println("  2. 维修人员/管理员（工号前缀：0025）");
        System.out.print("请输入编号（1 或 2）：");
        String roleInput = scanner.nextLine().trim();

        Role role;
        if ("1".equals(roleInput)) {
            role = Role.STUDENT;
        } else if ("2".equals(roleInput)) {
            role = Role.ADMIN;
        } else {
            System.out.println(MsgConst.INPUT_ERROR);
            return;
        }

        // 2. 输入账号
        String prompt = role == Role.STUDENT
                ? "请输入学号（前缀 3125 或 3225，共10位）："
                : "请输入工号（前缀 0025，共10位）：";
        System.out.print(prompt);
        String account = scanner.nextLine().trim();

        if (!ValidateUtil.isAccountValid(account, role)) {
            System.out.println(MsgConst.ACCOUNT_FORMAT_ERROR);
            return;
        }

        // 3. 输入密码
        System.out.print("请输入密码（6-20位，含字母和数字）：");
        String password = scanner.nextLine().trim();
        if (!ValidateUtil.isPasswordValid(password)) {
            System.out.println(MsgConst.PASSWORD_FORMAT_ERROR);
            return;
        }

        System.out.print("请确认密码：");
        String confirm = scanner.nextLine().trim();
        if (!password.equals(confirm)) {
            System.out.println(MsgConst.PASSWORD_NOT_MATCH);
            return;
        }

        // 4. 调用 Service 注册
        boolean success = userService.register(account, password, role.getCode());
        if (success) {
            System.out.println("[ 注册成功 ] 请返回主界面登录。");
        } else {
            System.out.println(MsgConst.ACCOUNT_EXISTS);
        }
    }
}
