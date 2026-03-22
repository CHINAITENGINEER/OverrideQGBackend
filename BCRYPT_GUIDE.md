# BCrypt 密码哈希生成指南

## 问题
假数据中的密码哈希可能不正确，导致登录失败。

## 解决方案

### 方法一：使用在线工具（最简单）

访问 https://bcrypt-generator.com/ 或类似工具：
1. 输入密码：`admin123`
2. 复制生成的哈希值
3. 替换 `fake_data.sql` 中所有的密码字段

### 方法二：使用 Java 生成（推荐）

在 IDE 中运行以下代码片段：

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String hash = encoder.encode(password);
        System.out.println("密码: " + password);
        System.out.println("哈希: " + hash);
    }
}
```

### 方法三：使用 MySQL 函数（不推荐）

如果你的 MySQL 支持 UDF，可以使用 `mysql_native_password` 函数，但 BCrypt 需要特殊支持。

## 更新假数据

1. 生成正确的 BCrypt 哈希
2. 编辑 `backend/sql/fake_data.sql`
3. 将所有密码字段替换为新的哈希值
4. 重新导入数据

## 验证

导入数据后，使用以下命令验证：

```bash
mysql -u root -p dormitory_repair
> SELECT account, password FROM user LIMIT 1;
```

密码字段应该以 `$2a$` 或 `$2b$` 开头。

## 快速修复

如果你已经有了正确的哈希值，直接替换 `fake_data.sql` 中的所有密码：

```sql
-- 查找并替换
-- 旧值: '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh8y'
-- 新值: [你生成的正确哈希]
```
