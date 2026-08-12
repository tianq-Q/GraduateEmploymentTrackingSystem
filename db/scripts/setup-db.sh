#!/bin/bash
# 就业跟踪系统 - 数据库初始化脚本 (Mac/Linux)
set -e

MYSQL_USER="root"
MYSQL_PASS="123456"
MYSQL_HOST="localhost"
MYSQL_PORT="3306"

# 解析命令行参数
while [[ $# -gt 0 ]]; do
  case $1 in
    -u) MYSQL_USER="$2"; shift 2 ;;
    -p) MYSQL_PASS="$2"; shift 2 ;;
    -h) MYSQL_HOST="$2"; shift 2 ;;
    -P) MYSQL_PORT="$2"; shift 2 ;;
    *) echo "用法: $0 [-u 用户名] [-p 密码] [-h 主机] [-P 端口]"; exit 1 ;;
  esac
done

echo "============================================"
echo "  就业跟踪系统 - 数据库初始化"
echo "============================================"
echo "数据库连接: $MYSQL_HOST:$MYSQL_PORT  用户: $MYSQL_USER"

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SQL_FILE="$SCRIPT_DIR/employment_tracking_data.sql"

if [ ! -f "$SQL_FILE" ]; then
  echo "[错误] 找不到数据文件: $SQL_FILE"
  exit 1
fi

echo "[1/2] 正在导入数据库结构和数据..."
mysql -u "$MYSQL_USER" -p"$MYSQL_PASS" -h "$MYSQL_HOST" -P "$MYSQL_PORT" --default-character-set=utf8mb4 < "$SQL_FILE"

echo "数据库结构和数据导入完成！"
echo "[2/2] 验证导入结果..."
mysql -u "$MYSQL_USER" -p"$MYSQL_PASS" -h "$MYSQL_HOST" -P "$MYSQL_PORT" employment_tracking -e "SELECT '用户数' as 项目, COUNT(*) as 值 FROM sys_user UNION SELECT '院系数', COUNT(*) FROM department UNION SELECT '专业数', COUNT(*) FROM major;" 2>/dev/null

echo ""
echo "============================================"
echo "  初始化完成！"
echo "============================================"
echo "测试账号:"
echo "  管理员: admin / admin123"
echo "  教师:   teacher01 / 123456"
echo "  学生:   student01 / 123456"
echo ""
echo "接下来:"
echo "  1. 配置 application-dev.yml 数据库密码"
echo "  2. 运行后端: cd backend && mvn spring-boot:run"
echo "  3. 运行前端: cd frontend && npm run dev"
