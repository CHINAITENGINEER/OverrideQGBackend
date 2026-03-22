# Service 层设计讲解

## 接口与实现分离

Service 层由两部分组成：

```
service/
├── UserService.java         ← 接口（只有方法签名，无实现）
├── RepairService.java       ← 接口
└── impl/
    ├── UserServiceImpl.java  ← 实现类（真正的业务逻辑）
    └── RepairServiceImpl.java
```

**UI 层调用方式：**

```java
// 声明接口类型变量，赋值实现类对象
UserService userService = new UserServiceImpl();

// 调用接口方法（不知道也不关心内部实现细节）
User user = userService.login(account, password);
```

下周迁移到 Spring Boot 后，只需把 `new UserServiceImpl()` 改成 `@Autowired`，其余代码不变。

---

## UserService 方法说明

### register(account, password, role)

```
1. 查询账号是否已存在 → 存在则返回 false
2. 用 BCrypt 加密密码
3. 构造 User 对象，调用 mapper.insert()
4. 返回 true
```

### login(account, password)

```
1. 根据账号查询用户 → 不存在返回 null
2. BCrypt.verify(明文密码, 数据库密文) → 不匹配返回 null
3. 验证通过，返回 User 对象（含 id、role 等信息）
```

### changePassword(userId, oldPassword, newPassword)

```
1. 按 id 查出用户
2. 验证旧密码是否正确 → 不正确返回 false
3. 用 BCrypt 加密新密码
4. updateById() 只更新 password 字段
5. 返回 true
```

### bindDorm(userId, building, roomNo)

```java
// 只传需要更新的字段，MyBatis-Plus updateById 只更新非 null 字段
User user = new User();
user.setId(userId);
user.setBuilding(building);
user.setRoomNo(roomNo);
mapper.updateById(user);  // 只执行 UPDATE user SET building=?, room_no=? WHERE id=?
```

---

## RepairService 方法说明

### createOrder(...)

```
1. 构造 RepairOrder 对象
2. 设置 status = PENDING（0，待处理）
3. mapper.insert(order)
4. MP 自动将数据库生成的自增 ID 回填到 order.id
5. 返回 order.getId()
```

### cancelOrder(orderId, studentId)

业务校验（三个条件都要满足）：
```
① order != null         — 报修单存在
② order.studentId == studentId  — 是当前学生自己的单
③ order.status == PENDING       — 只能取消待处理的单
```
三个条件有任意一个不满足，返回 false。

### rateOrder(orderId, studentId, rating)

业务校验（四个条件）：
```
① order != null
② order.studentId == studentId
③ order.status == COMPLETED     — 只有已完成才能评分
④ order.rating == null          — 防止重复评分
```

### countByStatus()

返回 `int[4]`，下标对应状态：
```java
counts[0]  // 待处理数量
counts[1]  // 处理中数量
counts[2]  // 已完成数量
counts[3]  // 已取消数量
```

---

## SqlSession 使用规范

所有 Service 方法都使用 try-with-resources：

```java
try (SqlSession session = DBUtil.getSession()) {
    UserMapper mapper = session.getMapper(UserMapper.class);
    // ... 执行操作
} // 自动调用 session.close()，释放数据库连接
```

**为什么要 close？**  
SqlSession 持有数据库连接，不关闭会导致连接泄漏，最终耗尽连接池。  
try-with-resources 语法保证即使发生异常也一定会关闭。
