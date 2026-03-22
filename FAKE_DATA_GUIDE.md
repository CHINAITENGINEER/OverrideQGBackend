# 假数据加载指南

## 概述

通过 SQL 脚本加载假数据，包含 3 个管理员 + 13 个学生 + 25 个报修单。

## 加载步骤

### 1. 创建数据库
```bash
mysql -u root -p < backend/sql/init.sql
```

### 2. 加载假数据
```bash
mysql -u root -p dormitory_repair < backend/sql/fake_data.sql
```

### 3. 验证数据
```bash
mysql -u root -p dormitory_repair
> SELECT COUNT(*) FROM user;      -- 应显示 16（3个管理员 + 13个学生）
> SELECT COUNT(*) FROM repair_order;  -- 应显示 25
```

## 测试账号

**管理员**（密码均为 `admin123`）
- `0025000001`
- `0025000002`
- `0025000003`

**学生**（密码均为 `admin123`）
- A栋：`3125001001` ~ `3125001005`
- B栋：`3225002001` ~ `3225002004`
- C栋：`3125003001` ~ `3125003003`

## 数据结构

### 用户数据
- **3 个管理员**：工号 `0025000001` ~ `0025000003`
- **13 个学生**：学号 `2024001001` ~ `2024003003`，分布在 A、B、C 三栋

### 报修单数据（25 条）

| 状态 | 数量 | 说明 |
|------|------|------|
| 待处理 | 6 | 新报修，等待处理 |
| 处理中 | 5 | 已分配给管理员 |
| 已完成 | 11 | 其中 8 条有评分，3 条无评分 |
| 已取消 | 3 | 学生主动取消 |

### 报修单优先级
- 低（1）：一般问题
- 中（2）：较重要问题
- 高（3）：紧急问题

## 重置数据

如需重新加载假数据：

```bash
# 清空现有数据
mysql -u root -p dormitory_repair < backend/sql/init.sql

# 重新加载假数据
mysql -u root -p dormitory_repair < backend/sql/fake_data.sql
```

## 常见问题

### Q: 密码是什么？
A: 所有测试账号密码均为 `admin123`

### Q: 如何修改假数据？
A: 编辑 `backend/sql/fake_data.sql` 文件中的 INSERT 语句

## 相关文件

- `backend/sql/init.sql` - 数据库初始化脚本
- `backend/sql/fake_data.sql` - 假数据 SQL 脚本
