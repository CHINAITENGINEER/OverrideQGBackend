package com.repair.service.impl;

import com.repair.entity.RepairOrder;
import com.repair.enums.RepairStatus;
import com.repair.mapper.RepairOrderMapper;
import com.repair.service.RepairService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 报修单服务实现类（Spring 管理，Mapper 注入）
 */
@Service
public class RepairServiceImpl implements RepairService {

    private final RepairOrderMapper repairOrderMapper;

    public RepairServiceImpl(RepairOrderMapper repairOrderMapper) {
        this.repairOrderMapper = repairOrderMapper;
    }

    @Override
    public Long createOrder(Long studentId, String building, String roomNo,
                            String deviceType, String description, int priority) {
        RepairOrder order = new RepairOrder();
        order.setStudentId(studentId);
        order.setBuilding(building);
        order.setRoomNo(roomNo);
        order.setDeviceType(deviceType);
        order.setDescription(description);
        order.setPriority(priority);
        order.setStatus(RepairStatus.PENDING.getCode());

        repairOrderMapper.insert(order);
        return order.getId();
    }

    @Override
    public List<RepairOrder> getMyOrders(Long studentId) {
        return repairOrderMapper.selectByStudentId(studentId);
    }

    @Override
    public boolean cancelOrder(Long orderId, Long studentId) {
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null
                || !order.getStudentId().equals(studentId)
                || order.getStatus() != RepairStatus.PENDING.getCode()) {
            return false;
        }
        order.setStatus(RepairStatus.CANCELLED.getCode());
        repairOrderMapper.updateById(order);
        return true;
    }

    @Override
    public boolean rateOrder(Long orderId, Long studentId, int rating) {
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null
                || !order.getStudentId().equals(studentId)
                || order.getStatus() != RepairStatus.COMPLETED.getCode()
                || order.getRating() != null) {
            return false;
        }
        order.setRating(rating);
        repairOrderMapper.updateById(order);
        return true;
    }

    @Override
    public List<RepairOrder> getAllOrders(Integer status) {
        return repairOrderMapper.selectAllWithFilter(status);
    }

    @Override
    public RepairOrder getOrderDetail(Long orderId) {
        return repairOrderMapper.selectById(orderId);
    }

    @Override
    public boolean updateStatus(Long orderId, Long adminId, int status, String remark) {
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null) {
            return false;
        }
        order.setStatus(status);
        order.setAdminId(adminId);
        if (remark != null && !remark.isBlank()) {
            order.setRemark(remark);
        }
        repairOrderMapper.updateById(order);
        return true;
    }

    @Override
    public boolean deleteOrder(Long orderId) {
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null) {
            return false;
        }
        repairOrderMapper.deleteById(orderId);
        return true;
    }

    @Override
    public int[] countByStatus() {
        int[] counts = new int[4];
        for (int i = 0; i < 4; i++) {
            counts[i] = (int) repairOrderMapper.selectAllWithFilter(i).size();
        }
        return counts;
    }
}
