-- =============================================
-- 宿舍报修管理系统 数据库初始化脚本
-- =============================================

CREATE DATABASE IF NOT EXISTS dormitory_repair
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE dormitory_repair;

-- ---------------------------------------------
-- 用户表（学生 + 管理员共用）
-- ---------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `account`    VARCHAR(20)  NOT NULL                COMMENT '账号（学号/工号）',
    `password`   VARCHAR(100) NOT NULL                COMMENT '密码（BCrypt加密）',
    `role`       TINYINT      NOT NULL                COMMENT '角色：0=学生，1=管理员',
    `building`   VARCHAR(20)  DEFAULT NULL            COMMENT '宿舍楼栋（学生专属）',
    `room_no`    VARCHAR(10)  DEFAULT NULL            COMMENT '宿舍房间号（学生专属）',
    `created_at` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ---------------------------------------------
-- 报修单表
-- ---------------------------------------------
DROP TABLE IF EXISTS `repair_order`;
CREATE TABLE `repair_order` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `student_id`  BIGINT       NOT NULL                COMMENT '报修学生ID',
    `building`    VARCHAR(20)  NOT NULL                COMMENT '宿舍楼栋',
    `room_no`     VARCHAR(10)  NOT NULL                COMMENT '宿舍房间号',
    `device_type` VARCHAR(30)  NOT NULL                COMMENT '设备类型',
    `description` TEXT         NOT NULL                COMMENT '问题描述',
    `priority`    TINYINT      DEFAULT 1               COMMENT '优先级：1=低，2=中，3=高',
    `status`      TINYINT      DEFAULT 0               COMMENT '状态：0=待处理，1=处理中，2=已完成，3=已取消',
    `admin_id`    BIGINT       DEFAULT NULL            COMMENT '处理管理员ID',
    `remark`      VARCHAR(255) DEFAULT NULL            COMMENT '管理员备注',
    `rating`      TINYINT      DEFAULT NULL            COMMENT '学生评分：1-5星',
    `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP     COMMENT '创建时间',
    `updated_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP   COMMENT '最后修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_status`     (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修单表';

-- ---------------------------------------------
-- 测试数据（密码均为 admin123 的BCrypt结果）
-- ---------------------------------------------
INSERT INTO `user` (`account`, `password`, `role`) VALUES
('0025000001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh8y', 1);
