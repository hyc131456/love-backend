# 心迹·情侣时光 后端服务

## 技术栈

- Java 17
- Spring Boot 3.2.0
- MyBatis-Plus 3.5.5
- MySQL 8.0
- JWT (jjwt 0.12.3)

## 快速开始

### 1. 创建数据库

```sql
-- 连接MySQL后执行
source d:/AI_WORK_SPACE/LOVE_APP/love-backend/src/main/resources/db/init.sql
```

或手动执行 `src/main/resources/db/init.sql` 中的SQL语句。

### 2. 修改配置

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/love_app?...
    username: your_username
    password: your_password

wechat:
  appid: your_appid
  secret: your_secret
```

### 3. 运行项目

```bash
cd love-backend
mvn spring-boot:run
```

或使用IDE直接运行 `LoveAppApplication.java`

### 4. 测试接口

```bash
# 健康检查
curl http://localhost:8080/api/health

# 模拟登录
curl -X POST "http://localhost:8080/api/user/login?openid=test123"
```

## API 接口列表

| 模块 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 用户 | /user/login | POST | 模拟登录 |
| 用户 | /user/wxLogin | POST | 微信登录 |
| 用户 | /user/profile | GET | 获取用户信息 |
| 配对 | /couple/create | POST | 创建空间 |
| 配对 | /couple/join | POST | 加入空间 |
| 配对 | /couple/info | GET | 获取空间信息 |
| 日历 | /calendar/events | GET | 获取月事件 |
| 日历 | /calendar/event | POST | 添加事件 |
| 日记 | /diary/list | GET | 日记列表 |
| 日记 | /diary | POST | 创建日记 |

## 项目结构

```
src/main/java/com/loveapp/
├── LoveAppApplication.java     # 启动类
├── common/                     # 通用类
│   ├── Result.java            # 统一响应
│   ├── ResultCode.java        # 状态码
│   └── exception/             # 异常处理
├── config/                     # 配置类
├── controller/                 # 控制器
├── dto/                        # 数据传输对象
├── entity/                     # 实体类
├── interceptor/                # 拦截器
├── mapper/                     # Mapper接口
├── service/                    # 服务接口
│   └── impl/                  # 服务实现
└── utils/                      # 工具类
```
