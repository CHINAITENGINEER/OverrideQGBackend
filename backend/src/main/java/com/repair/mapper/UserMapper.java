package com.repair.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.repair.entity.User;
import org.apache.ibatis.annotations.Param;

/**
 * 用户数据访问接口（DAO 层）
 * <p>
 * 继承 MyBatis-Plus 的 BaseMapper<User>，自动拥有以下常用方法：
 *   insert(user)            — 插入一条记录
 *   deleteById(id)          — 按主键删除
 *   updateById(user)        — 按主键更新（只更新非null字段）
 *   selectById(id)          — 按主键查询
 *   selectList(queryWrapper)— 条件查询列表
 *   selectOne(queryWrapper) — 条件查询单条
 * <p>
 * 如果 BaseMapper 提供的方法不够用，可以在此接口中声明自定义方法，
 * 并在 resources/mapper/UserMapper.xml 中编写对应的 SQL。
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据账号查询用户
     * <p>
     * 虽然可以用 BaseMapper + QueryWrapper 实现，但登录/注册时频繁使用，
     * 单独定义方法让调用处代码更简洁，SQL 写在 UserMapper.xml 中。
     *
     * @param account 账号（学号/工号）
     * @return 找到则返回 User 对象，否则返回 null
     */
    User selectByAccount(@Param("account") String account);
}
