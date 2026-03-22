package com.repair.constant;

/**
 * 系统提示信息常量类
 * 所有输出到控制台的固定文本统一在此管理，避免硬编码散落各处。
 */
public class MsgConst {

    private MsgConst() {}

    // ===== 通用 =====
    public static final String LINE             = "==============================";
    public static final String INPUT_ERROR      = "[ 输入有误，请重新输入 ]";
    public static final String OPERATION_SUCCESS = "[ 操作成功 ]";
    public static final String OPERATION_FAIL    = "[ 操作失败，请稍后重试 ]";

    // ===== 账号相关 =====
    public static final String ACCOUNT_EXISTS        = "[ 该账号已被注册，请换一个 ]";
    public static final String ACCOUNT_NOT_FOUND     = "[ 账号不存在，请先注册 ]";
    public static final String PASSWORD_WRONG        = "[ 密码错误，请重新输入 ]";
    public static final String PASSWORD_NOT_MATCH    = "[ 两次密码不一致，请重新输入 ]";
    public static final String ACCOUNT_FORMAT_ERROR  = "[ 账号格式不正确，学生请使用 3125/3225 前缀，管理员请使用 0025 前缀 ]";
    public static final String PASSWORD_FORMAT_ERROR = "[ 密码格式不正确，需 6-20 位，且包含字母和数字 ]";

    // ===== 报修单相关 =====
    public static final String ORDER_NOT_FOUND   = "[ 报修单不存在 ]";
    public static final String ORDER_CANCEL_FAIL = "[ 仅可取消【待处理】状态的报修单 ]";
}
