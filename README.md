# finlink

## 资金管理系统快速开始

### 安装 docker & docker-compose

1. 安装依赖工具
```bash
sudo apt-get install -y apt-transport-https ca-certificates curl software-properties-common
```
2. 添加 Docker 官方 GPG 密钥（阿里云镜像）
```bash
curl -fsSL https://mirrors.aliyun.com/docker-ce/linux/ubuntu/gpg | sudo apt-key add -
```
3. 添加阿里云 Docker 软件源
```bash
sudo add-apt-repository "deb [arch=amd64] https://mirrors.aliyun.com/docker-ce/linux/ubuntu $(lsb_release -cs) stable"
```
4. 更新软件包索引
```bash
sudo apt-get update
```
5. 安装 Docker 核心组件
```bash
sudo apt-get install -y docker-ce docker-ce-cli containerd.io
```
6. 启动 Docker 并设置开机自启
```bash
sudo systemctl enable --now docker
```
7. 配置镜像加速（推荐）
```bash
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
    "registry-mirrors": ["https://mirror.ccs.tencentyun.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```
8. 重新加载 systemd 配置
```bash
sudo systemctl daemon-reload
```
9. 启动 Docker 服务
```bash
sudo systemctl start docker
```
10. 设置 Docker 开机自启
```bash
sudo systemctl enable docker
```
11. 查看 Docker 服务运行状态
```bash
sudo systemctl status docker
```

### 开发环境搭建

1. 首先要准备云服务器或者虚拟机，并且这个云服务器/虚拟机上得有 docker & docker-compose，下载方式如上所述
2. 然把 deploy/dev 上传到云服务器上
3. 进入 deploy/dev/app/ 目录，执行 docker compose -p finlink -f docker-compose-mid.yml up -d
4. 使用云服务器的话得先开一个隧道连接到 mysql 容器，否则直接在本地连接也可以
5. 等拉取完 mysql 镜像，进入 mysql 执行 deploy/dev/res/sql/db.sql 文件里的所有 sql 语句

### 生产环境搭建
1. 首先还是准备云服务器，把 docker 和 docker-compose 安装好
2. 后端程序打包：
- 下载：gradle-8.14.5，👉[点击下载](https://services.gradle.org/distributions/gradle-8.14.5-bin.zip)
- 解压到任意目录下，如：D:\gradle-8.14.5
- {你gradle解压的路径}\bin\gradle.bat bootJar
3. 前端程序打包：
- 下载：nodejs-18.??[点击下载](https://nodejs.org/en/download/)
- 进入前端项目目录，执行 npm install
- 打包前端项目：执行 npm run build，生成 dist 目录
4. 上传服务器：
- 后端：把 deploy/prd 上传到云服务器，进入 /app 目录，执行 docker compose -p finlink -f docker-compose-mid.yml up -d
- 前端：把打包好的 dist 上传到 deploy/prd/app/nginx/web 目录下
5. 查看效果：访问 http://云服务器ip
