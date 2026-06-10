#!/bin/bash

set -e

echo "==== 1. 更新系统 ===="
sudo dnf update -y

echo "==== 2. 安装基础依赖 ===="
sudo dnf install -y dnf-plugins-core curl wget

echo "==== 3. 添加 Docker 腾讯云镜像源 ===="
sudo dnf config-manager --add-repo https://mirrors.cloud.tencent.com/docker-ce/linux/centos/docker-ce.repo

echo "==== 4. 更新缓存 ===="
sudo dnf makecache

echo "==== 5. 安装 Docker ===="
sudo dnf install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

echo "==== 6. 启动 Docker ===="
sudo systemctl enable --now docker

echo "==== 7. 配置镜像加速 ===="
sudo mkdir -p /etc/docker

sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://mirror.ccs.tencentyun.com"]
}
EOF

sudo systemctl daemon-reload
sudo systemctl restart docker

echo "==== 8. 验证安装 ===="
docker --version
docker compose version
systemctl status docker --no-pager

echo "==== DONE: Docker installed successfully ===="