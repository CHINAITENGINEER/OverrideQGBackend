package com.repair.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报修单实体类
 * <p>
 * 对应数据库表：repair_order
 * 状态流转说明：
 *   0(待处理) → 1(处理中) → 2(已完成)
 *   0(待处理) → 3(已取消)  [仅学生可操作，且只能取消待处理的单]
 */
@Data
@TableName("repair_order")
public class RepairOrder {

    /** 主键，数据库自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 报修学生的用户ID，关联 user.id */
    private Long studentId;

    /** 报修时的宿舍楼栋（冗余存储，避免学生改宿舍后数据错乱） */
    private String building;

    /** 报修时的宿舍房间号 */
    private String roomNo;

    /**
     * 设备类型，存储枚举描述文字
     * 例如："水龙头/水管"、"电路/插座"
     */
    private String deviceType;

    /** 问题描述，学生自由填写 */
    private String description;

    /**
     * 优先级：1=低，2=中，3=高
     * 管理员查看列表时按优先级降序排列，高优先级先处理
     */
    private Integer priority;

    /**
     * 报修单状态：0=待处理，1=处理中，2=已完成，3=已取消
     * 详见 RepairStatus 枚举
     */
    private Integer status;

    /** 处理该报修单的管理员ID，未分配时为 null */
    private Long adminId;

    /** 管理员备注，更新状态时可填写处理说明 */
    private String remark;

    /** 学生对本次维修的评分（1-5星），仅状态为已完成时可评 */
    private Integer rating;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 最后修改时间，数据库 ON UPDATE CURRENT_TIMESTAMP 自动更新 */
    private LocalDateTime updatedAt;
}
