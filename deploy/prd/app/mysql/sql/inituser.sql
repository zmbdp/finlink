create database if not exists `finlink_prd` default character set utf8mb4 collate utf8mb4_general_ci;

create user 'zmbdpprd'@'%' identified BY 'Hf@173503494';

grant all privileges on finlink_prd.* to 'zmbdpprd'@'%';
FLUSH PRIVILEGES;

