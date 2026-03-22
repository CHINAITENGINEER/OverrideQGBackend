# MyBatis XML 映射文件讲解

## 文件结构

```
resources/mapper/
├── UserMapper.xml
└── RepairOrderMapper.xml
```

每个 XML 对应一个 Mapper 接口，通过 `namespace` 关联。

---

## UserMapper.xml 讲解

### namespace

```xml
<mapper namespace="com.repair.mapper.UserMapper">
```

`namespace` 必须和接口的全限定类名完全一致，MyBatis 靠这个找到对应的接口方法。

### ResultMap

```xml
<resultMap id="UserMap" type="User">
    <id     column="id"         property="id"/>
    <result column="room_no"    property="roomNo"/>
    <result column="created_at" property="createdAt"/>
</resultMap>
```

- `column`：数据库列名
- `property`：Java 对象字段名
- 虽然全局开启了驼峰映射，显式定义 ResultMap 更直观、便于维护

### 自定义查询

```xml
<select id="selectByAccount" resultMap="UserMap">
    SELECT * FROM `user` WHERE account = #{account} LIMIT 1
</select>
```

- `id` 必须和接口方法名一致
- `#{account}` 是参数占位符，防止 SQL 注入（等价于 PreparedStatement 的 `?`）
- `LIMIT 1` 是好习惯，虽然 account 有唯一索引，显式限制更安全

---

## RepairOrderMapper.xml 讲解

### 动态 SQL

```xml
<select id="selectAllWithFilter" resultMap="OrderMap">
    SELECT * FROM repair_order
    <where>
        <if test="status != null">
            status = #{status}
        </if>
    </where>
    ORDER BY priority DESC, created_at DESC
</select>
```

**`<where>` 标签**：内部有内容时自动加 WHERE，全为空时不输出任何内容。

**`<if test="...">` 标签**：条件为真才拼接该片段。

| 调用 | 生成 SQL |
|------|----------|
| `selectAllWithFilter(null)` | `SELECT * FROM repair_order ORDER BY ...` |
| `selectAllWithFilter(0)` | `SELECT * FROM repair_order WHERE status = 0 ORDER BY ...` |

---

## mybatis-config.xml 关键配置

```xml
<!-- 驼峰映射：数据库 room_no 自动映射到 Java roomNo -->
<setting name="mapUnderscoreToCamelCase" value="true"/>

<!-- 开启 SQL 日志，调试时可以在控制台看到实际执行的 SQL -->
<setting name="logImpl" value="STDOUT_LOGGING"/>
```

```xml
<!-- 实体类别名：配置后 XML 中可直接写 User 代替 com.repair.entity.User -->
<typeAliases>
    <package name="com.repair.entity"/>
</typeAliases>
```
