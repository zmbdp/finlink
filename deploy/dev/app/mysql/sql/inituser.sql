create database if not exists `finlink_dev` default character set utf8mb4 collate utf8mb4_general_ci;

create user 'zmbdpdev'@'%' identified BY 'Hf@173503494';

grant all privileges on finlink_dev.* to 'zmbdpdev'@'%';
FLUSH PRIVILEGES;

