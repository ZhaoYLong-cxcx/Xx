-- 数据库
drop database if exists `cash`;
create database if not exists `cash` character set utf8;
-- 使用数据库
use `cash`;
-- 帐号
drop table if exists `account`;
create table if not exists `account`
(
  id int primary key auto_increment comment '帐号编号',
  username varchar(12) not null comment '帐号',
  password varchar(128) not null comment '密码'
);
-- 商品信息
drop table if exists `goods`;
create table if not exists `goods`
(
  id int primary key auto_increment comment '商品编号',
  name varchar(128) not null comment '商品名称',
  introduce varchar(256) default '暂无' not null comment '商品简介',
  stock int not null comment '商品库存',
  unit varchar(12) not null comment '库存单位',
  price int not null comment '商品价格，单位：分',
  discount int default 100 not null comment '商品折扣，[0,100]'
);
-- 订单
drop table if exists `order`;
create table if not exists `order`
(
  id varchar(32) primary key comment '订单编号',
  account_id int not null comment '帐号编号',
  account_name varchar(12) not null comment '帐号',
  create_time datetime not null comment '创建时间',
  finish_time datetime default null comment '完成时间',
  actual_amount int not null comment '实际金额，单位：分',
  total_money int not null comment '总金额，单位：分',
  order_status int not null comment '支付状态 1 待支付 2 完成'
);
-- 订单项
drop table if exists `order_item`;
create table if not exists `order_item`
(
  id int primary key auto_increment comment '订单条目编号',
  order_id varchar(32) not null comment '订单编号',
  goods_id int not null comment '商品编号',
  goods_name varchar(128) not null comment '商品名称',
  goods_introduce varchar(256) default '暂无' not null comment '商品简介',
  goods_num int not null comment '商品数量',
  goods_unit varchar(12) not null comment '库存单位',
  goods_price int not null comment '商品价格，单位：分',
  goods_discount int default 100 not null comment '商品折扣，[0,100]'
);