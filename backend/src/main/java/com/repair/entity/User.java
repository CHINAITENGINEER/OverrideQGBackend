package com.repair.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类（学生和管理员共用一张表）
 * <p>
 * 对应数据库表：user
 * 注解说明：
 *   @TableName  — 指定对应的数据库表名
 *   @TableId    — 指定主键字段，IdType.AUTO 表示使用数据库自增
 *   @Data       — Lombok 注解，自动生成 getter/setter/toString/equals/hashCode
 */
@Data
@TableName("user")
public class User {

    /** 主键，数据库自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 账号：学生为学号（3125/3225开头），管理员为工号（0025开头） */
    private String account;

    /** 密码：BCrypt 加密后存储，绝不明文保存 */
    private String password;

    /**
     * 角色：0=学生，1=管理员
     * 使用 int 与数据库交互，业务层通过 Role.fromCode() 转换为枚举
     */
    private Integer role;

    /** 宿舍楼栋，仅学生有值，管理员为 null */
    private String building;

    /** 宿舍房间号，仅学生有值，管理员为 null */
    private String roomNo;

    /** 注册时间，数据库自动填充 */
    private LocalDateTime createdAt;
}
