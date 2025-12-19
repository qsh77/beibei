# Docker 部署指南 (Windows/Mac/Linux)

本项目已支持 Docker 容器化部署，你可以轻松地在 Windows、Mac 或 Linux 环境下运行整个系统。

## 前置要求

确保你的电脑上安装了：

1. **Docker Desktop** (包含 Docker Engine 和 Docker Compose)
    - 下载地址: [https://www.docker.com/products/docker-desktop/](https://www.docker.com/products/docker-desktop/)

## 快速开始

### 1. 启动项目

在项目根目录下（即 `docker-compose.yml` 所在的目录），打开终端（Windows 下可用 PowerShell 或 CMD），运行以下命令：

```bash
docker-compose up --build -d
```

- `--build`: 强制重新构建镜像（确保代码更新生效）
- `-d`: 后台运行

### 2. 访问服务

等待几分钟让服务启动和数据库初始化完成后，你可以通过浏览器访问：

- **前台网站 (Web)**: [http://localhost:3000](http://localhost:3000)
- **后台管理 (Admin)**: [http://localhost:3001](http://localhost:3001)
- **后端 API**: [http://localhost:8080/doc.html](http://localhost:8080/doc.html) (Swagger 文档)

### 3. 查看日志

如果遇到问题，可以查看日志：

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看后端日志
docker-compose logs -f beibei-backend
```

### 4. 停止项目

```bash
docker-compose down
```

## 注意事项

- **数据库数据**: 数据库数据会持久化保存在 Docker Volume `mysqldata` 中，重启容器数据不会丢失。
- **图片上传**: 图片会保存在项目根目录下的 `uploads` 文件夹中。
- **端口占用**:
  - MySQL 映射到了宿主机 `3307` 端口
  - Redis 映射到了宿主机 `6380` 端口
  - Web 映射到了 `3000`
  - Admin 映射到了 `3001`
  - Backend 映射到了 `8080`
  - 请确保这些端口未被占用。

## 故障排查

1. **数据库连接失败**:
    - 刚启动时，MySQL 初始化需要时间，后端可能会报错连接不上。Docker Compose 会自动让后端重启尝试连接，请耐心等待 1-2 分钟。
2. **Windows换行符问题**:
    - 如果遇到 `mvnw: no such file or directory` 或 `\r` 错误，可能是 Git 自动将换行符转换为了 CRLF。请确保 `mvnw` 文件是 LF 格式。
    - 解决方法：在 `beibei-java` 目录下运行 `dos2unix mvnw` (如果安装了 Git Bash) 或在编辑器中将行尾序列切换为 `LF`。
