package com.repair.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.repair.entity.RepairOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 报修单数据访问接口（DAO 层）
 * <p>
 * 继承 BaseMapper<RepairOrder>，获得基础 CRUD 能力。
 * 复杂查询（多条件筛选、排序）在 XML 中编写 SQL。
 */
public interface RepairOrderMapper extends BaseMapper<RepairOrder> {

    /**
     * 查询指定学生的所有报修单，按创建时间倒序
     *
     * @param studentId 学生ID
     * @return 该学生的报修单列表
     */
    List<RepairOrder> selectByStudentId(@Param("studentId") Long studentId);

    /**
     * 管理员：查询所有报修单，支持按状态筛选，按优先级降序+创建时间倒序排列
     *
     * @param status 状态值，null 表示查全部
     * @return 报修单列表
     */
    List<RepairOrder> selectAllWithFilter(@Param("status") Integer status);
}
