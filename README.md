# finlink

## 资金管理系统快速开始

### 安装 docker & docker-compose
1. 把 deploy/prd 目录上传到云服务器上
2. 进入 deploy/prd 运行 chmod +x install-docker.sh
3. 再运行 ./install-docker.sh
4. 进入 deploy/prd/app/ 运行 docker compose -p finlink -f docker-compose-mid.yml up -d

### 开发环境搭建

1. 首先要准备云服务器或者虚拟机，并且这个云服务器/虚拟机上得有 docker & docker-compose，下载方式如上所述
2. 然后把 deploy/dev 上传到云服务器上
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

---
## 重装系统演示：
1. 进入 deploy/prd/ 运行 
```bash
chmod +x reset.sh
```
2. 再运行 ./reset.sh