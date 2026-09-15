# 部署指南：把项目放到公网上

跟着本文走完，你会得到一个形如 `http://123.45.67.89` 的地址，任何人打开都能看到你的系统。

---

## 0. 先记住四句话

1. **用 IP 访问不需要备案**。备案只针对域名，且只有绑定国内服务器才需要。答辩赶时间就直接用 IP。
2. **前端不用改一行代码**。前端所有请求都发往相对路径 `/api`，部署时用 Nginx 把 `/api` 转发到本机 8080，与本地开发完全一致。
3. **本项目不用 Redis**（`backend/pom.xml` 中无 Redis 依赖）。服务器上只需装 JDK、MySQL、Nginx 三样。
4. **推荐在 Windows 本地打包，再上传到服务器**。这样服务器连 Node 和 Maven 都不用装，2G 内存也扛得住。

---

## 1. 领取免费服务器

三个入口，挑一个能领的：

| 平台 | 入口 | 个人免费规格 |
|------|------|--------------|
| 腾讯云 | https://cloud.tencent.com/act/pro/free | 轻量应用服务器 **2核2G / 3M 带宽 / 40G SSD，免费 1 个月**（会员专享档可选 2核4G / 5M） |
| 阿里云 | https://free.aliyun.com | 云服务器 ECS 个人版，**300 元额度 / 3 个月内有效**，最高可选 4核8G |
| 华为云 | https://activity.huaweicloud.com | 云耀云服务器 2核1G / 1M，免费 1 个月 |

**领取注意**

- 需要**实名认证**，学生用身份证即可，大概几分钟。
- 仅限**产品新用户**（这个账号从没买过该产品）。企业认证的配置更高，但要先注册企业，学生没必要。
- 腾讯云企业版每天限量，个人版随时可领。

**推荐**：腾讯云轻量应用服务器 2核4G / 5M（会员档）> 2核2G / 3M。内存 2G 也能跑，但得按第 7 节调低 JVM 参数。

**下单配置**

- 镜像：**Ubuntu 22.04 LTS**（别选 Windows，白白吃掉 1G 内存）
- 地域：离你最近的（广州 / 上海 / 北京）
- 安全组 / 防火墙：放行 **22**（SSH）、**80**（HTTP）。**8080 不要对外放行**，让 Nginx 从内部转发。

买完后在控制台复制**公网 IP**，下文用 `你的公网IP` 代替。

---

## 2. 连接服务器

Windows 推荐装 [FinalShell](https://www.hostbuf.com/)（免费，自带文件上传，比命令行省事）。想用命令行就在 PowerShell 里：

```bash
ssh root@你的公网IP
```

首次连接会问 `Are you sure you want to continue`，输入 `yes` 回车。密码在云厂商控制台里，或者下单时让你设过。

---

## 3. 安装环境

一路复制粘贴执行：

```bash
apt update && apt upgrade -y
apt install -y openjdk-17-jre-headless mysql-server nginx
```

如果提示 `Unable to locate package`，先执行 `apt update` 再重试。

验证：

```bash
java -version     # 应显示 openjdk 17.x
mysql --version   # 应显示 8.0.x
nginx -v          # 应显示 nginx/1.18 或更高
```

### 3.1 内存不够怎么办（2G 机型必做）

MySQL 8 默认要吃 400–500MB，Spring Boot 再吃 700MB+，2G 机器容易 OOM。加个 2G 的 swap 兜底：

```bash
fallocate -l 2G /swapfile
chmod 600 /swapfile
mkswap /swapfile
swapon /swapfile
echo '/swapfile none swap sw 0 0' >> /etc/fstab
```

---

## 4. 建库并导入数据

```bash
mysql -u root -p
```

进入 MySQL 后执行（注意分号）：

```sql
CREATE DATABASE employment_tracking
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
CREATE USER 'emp'@'localhost' IDENTIFIED BY 'Emp@2026!Strong';
GRANT ALL PRIVILEGES ON employment_tracking.* TO 'emp'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

> 密码自己改一个，**别用 `123456`** 这种，公网机器分分钟被扫。

把 SQL 脚本传到服务器。在 **Windows 本地**另开一个终端（不要关掉 SSH 那个）：

```bash
scp "D:\内蒙古工业大学\青软项目\高校毕业生就业与跟踪分析系统\13组-系统源码+sql脚本\高校毕业生就业跟踪与分析系统源码+sql脚本\employment-tracking\db\employment_tracking.sql" root@你的公网IP:/root/
```

回到服务器，导入：

```bash
mysql -u root -p employment_tracking < /root/employment_tracking.sql
```

验证表建好了：

```bash
mysql -u root -p -e "USE employment_tracking; SHOW TABLES;"
```

---

## 5. 本地打包后端

在 **Windows 本地**执行（需要 JDK 17 + Maven，你本机已经跑通过 `mvn spring-boot:run`，说明都有）：

```bash
cd backend
mvn clean package -DskipTests
```

产物在 `backend/target/employment-tracking-1.0.0.jar`。上传到服务器：

```bash
scp target\employment-tracking-1.0.0.jar root@你的公网IP:/root/
```

---

## 6. 打包前端并上传

同样在**本地**执行（需要 Node 18+）：

```bash
cd frontend
npm install
npm run build
```

产物是 `frontend/dist/` 整个目录。打包上传：

```bash
tar -czf dist.tar.gz dist
scp dist.tar.gz root@你的公网IP:/root/
```

> 如果 `npm run build` 报 `vue-tsc` 类型错误，改成 `npx vite build` 跳过类型检查，能出包就行。

---

## 7. 在服务器上配置并启动后端

### 7.1 解压前端

```bash
mkdir -p /var/www/employment
tar -xzf /root/dist.tar.gz -C /var/www/employment --strip-components=1
ls /var/www/employment    # 应看到 index.html 和 assets/
```

### 7.2 放好后端 jar

```bash
mkdir -p /opt/employment/config /opt/employment/uploads /opt/employment/logs
mv /root/employment-tracking-1.0.0.jar /opt/employment/
```

### 7.3 创建外部配置文件

> **这一步不能省。** `application-dev.yml` 因为含数据库密码没有进 Git 仓库，服务器上必须自己建一个。Spring Boot 会自动读取 jar 同级 `config/` 目录下的配置，优先级高于 jar 内部，所以不用改 jar。

新建 `/opt/employment/config/application-dev.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/employment_tracking?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: ${MYSQL_USER:emp}
    password: ${MYSQL_PASS}
    driver-class-name: com.mysql.cj.jdbc.Driver

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000

app:
  upload:
    dir: ./uploads
```

嫌手敲麻烦，就把仓库里现成的模板传上去，内容一样：

```bash
scp deploy\application-dev.yml root@你的公网IP:/opt/employment/config/
```

### 7.4 创建 systemd 服务

新建 `/etc/systemd/system/employment.service`：

```ini
[Unit]
Description=Employment Tracking Backend
After=mysql.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/employment
Environment="MYSQL_USER=emp"
Environment="MYSQL_PASS=Emp@2026!Strong"
Environment="JWT_SECRET=替换成你自己的一段长随机字符串"
ExecStart=/usr/bin/java -Xms256m -Xmx768m -jar /opt/employment/employment-tracking-1.0.0.jar
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
```

> `MYSQL_PASS` 要跟第 4 节设的一致。
> `JWT_SECRET` 随便编一段，比如执行 `openssl rand -base64 32` 生成一个。
> **2核4G 机型**可把 `-Xmx768m` 调到 `-Xmx1200m`。

也可以用仓库里现成的模板：`deploy/employment-tracking.service`，改三个 `Environment` 的值即可。

### 7.5 启动

```bash
systemctl daemon-reload
systemctl enable employment
systemctl start employment
```

看启动日志：

```bash
journalctl -u employment -f
```

看到 `Started EmploymentTrackingApplication` 就成功了。按 `Ctrl+C` 退出日志。

验证接口：

```bash
curl http://127.0.0.1:8080/api/auth/login -X POST -H "Content-Type: application/json" -d '{}'
```

能返回 JSON（哪怕是报错的 JSON）说明后端活着。

---

## 8. 配置 Nginx

新建 `/etc/nginx/sites-available/employment`：

```nginx
server {
    listen 80;
    server_name _;

    root /var/www/employment;
    index index.html;

    # 前端静态资源
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 接口转发到后端
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 上传的附件
    location /uploads/ {
        alias /opt/employment/uploads/;
    }

    client_max_body_size 20m;
}
```

也可以直接用仓库里的 `deploy/nginx.conf`，内容一样。

启用：

```bash
rm -f /etc/nginx/sites-enabled/default
ln -s /etc/nginx/sites-available/employment /etc/nginx/sites-enabled/
nginx -t              # 必须显示 syntax is ok
systemctl restart nginx
```

---

## 9. 打开浏览器验证

访问：

```
http://你的公网IP
```

登录账号用 SQL 里初始化好的那批（`admin`、`teacher`、`student` 等），密码是你自己统一改的那个。

**打不开？按顺序排查**

| 现象 | 原因 | 处理 |
|------|------|------|
| 完全打不开，连接超时 | 安全组没放行 80 | 去云厂商控制台 → 防火墙 → 添加规则：TCP 80 |
| 显示 Nginx 欢迎页 | 默认站点没删 | 执行第 8 节的 `rm -f /etc/nginx/sites-enabled/default` 后重启 |
| 页面白屏，F12 报 404 | dist 路径不对 | 检查 `/var/www/employment/index.html` 是否存在 |
| 页面能开，登录报 502 | 后端没起来 | `systemctl status employment` 看状态，`journalctl -u employment -n 50` 看日志 |
| 502 且日志说数据库连接失败 | 密码写错了 | 检查 systemd 里的 `MYSQL_PASS` 与 MySQL 实际密码 |

---

## 10. 试用到期之后

免费试用一般 1 个月。到期前有三种选择：

1. **续费**：腾讯云试用期间续费 1 年 3.5 折，2核2G 约一百多元/年。
2. **首单特惠**：试用完还能享受新用户首单，轻量服务器常年有 38–99 元/年的活动。
3. **快照备份后释放**：在控制台给服务器打个快照，把数据留着，之后想用再恢复。

**答辩前记得做快照**。墨菲定律：演示当天服务器一定会出问题，有快照能救你一命。

---

## 附：常用运维命令

```bash
systemctl status employment       # 看后端运行状态
systemctl restart employment      # 重启后端
journalctl -u employment -f       # 实时看后端日志
systemctl reload nginx            # 重载 Nginx 配置

# 更新代码：重新走一遍本地打包 → scp 上传 → 下面两条
systemctl restart employment
rm -rf /var/www/employment/* && tar -xzf /root/dist.tar.gz -C /var/www/employment --strip-components=1
```
