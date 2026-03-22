package com.repair.service;

import com.repair.entity.RepairOrder;

import java.util.List;

/**
 * 报修单服务接口
 * <p>
 * 覆盖学生端和管理员端的全部业务操作。
 */
public interface RepairService {

    // ==================== 学生功能 ====================

    /**
     * 创建报修单
     *
     * @param studentId   学生ID
     * @param building    楼栋
     * @param roomNo      房间号
     * @param deviceType  设备类型描述文字
     * @param description 问题描述
     * @param priority    优先级（1低/2中/3高）
     * @return 创建成功返回新报修单的ID
     */
    Long createOrder(Long studentId, String building, String roomNo,
                     String deviceType, String description, int priority);

    /**
     * 查询某学生的全部报修记录
     *
     * @param studentId 学生ID
     * @return 报修单列表，按创建时间倒序
     */
    List<RepairOrder> getMyOrders(Long studentId);

    /**
     * 取消报修单（仅限"待处理"状态）
     *
     * @param orderId   报修单ID
     * @param studentId 学生ID（用于校验是否是自己的单）
     * @return true=取消成功，false=单不存在/不属于该学生/状态不允许取消
     */
    boolean cancelOrder(Long orderId, Long studentId);

    /**
     * 学生对已完成的报修单进行评分
     *
     * @param orderId   报修单ID
     * @param studentId 学生ID
     * @param rating    评分（1-5）
     * @return true=评分成功
     */
    boolean rateOrder(Long orderId, Long studentId, int rating);

    // ==================== 管理员功能 ====================

    /**
     * 查询所有报修单，支持按状态筛选
     *
     * @param status 状态值，null 表示查全部
     * @return 报修单列表，按优先级降序+创建时间倒序
     */
    List<RepairOrder> getAllOrders(Integer status);

    /**
     * 查看单个报修单详情
     *
     * @param orderId 报修单ID
     * @return 报修单对象，找不到返回 null
     */
    RepairOrder getOrderDetail(Long orderId);

    /**
     * 管理员更新报修单状态
     *
     * @param orderId  报修单ID
     * @param adminId  操作的管理员ID
     * @param status   新状态
     * @param remark   备注说明（可为null）
     * @return true=更新成功，false=报修单不存在
     */
    boolean updateStatus(Long orderId, Long adminId, int status, String remark);

    /**
     * 管理员删除报修单
     *
     * @param orderId 报修单ID
     * @return true=删除成功，false=报修单不存在
     */
    boolean deleteOrder(Long orderId);

    /**
     * 统计各状态报修单数量（管理员专用）
     *
     * @return 长度为4的数组：[待处理数, 处理中数, 已完成数, 已取消数]
     */
    int[] countByStatus();
}
