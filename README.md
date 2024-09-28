## 项目介绍
拼多多领券中心，助力用户便捷领取和平台分发优惠券红包，促进商家销售和平台 GMV 指标提升。平台包括优惠券秒杀、分发、结算以及搜索等业务，支持大量用户同时进行优惠券领取功能，以及完成平台百万级别用户优惠券分发功能，保障不漏发、不多发等特性。使用缓存、分库分表、RocketMQ5.x 以及 Sentinel 等技术支撑平台稳定运行。

## 目录结构 🚀
coupon-system  
├── distribution        -- 分发模块  
├── engine              -- 引擎模块  
├── framework           -- 基础模块  
├── gateway             -- 网关模块  
├── merchant-admin      -- 后管模板  
├── search              -- 搜索模块  
└── settlement          -- 结算模块  

## 技术选型
| 技术 | 说明 | 版本 | 
|:---:|:---:|:---:|
| Spring Boot | 基本框架 | 2.7.0 |
| SpringCloud Alibaba | 分布式框架 | 2021.0.1.0 |
| SpringCloud Gateway | 网关框架 | 3.1.3 |
| MyBatis-Plus | 持久层框架 | 3.5.7 |
| MySQL | 数据库 | 8.0.27 |
| ShardingSphere | 数据库分片工具 | 5.2.0 |
| Knife4j | 文档生产工具 | 3.0.3 |
| Redis | 缓存 | Latest |
| RocketMQ | 消息队列 | 2.3.0 |
| Canal | BinLog 订阅组件 | 1.1.6 |
| XXL-Job | 分布式定时任务框架 | 2.4.1 |
| EasyExcel | Excel 处理工具 | 3.2.1 |
| Knife4j | 文档生产工具 | 3.0.3 |
| ElasticSearch | 搜索引擎 | 7.6.2 |

## 系统架构图
![cliuc](https://github.com/user-attachments/assets/05860457-bdbf-4f55-b99e-451b7c677bf3)


## 业务架构图
![业务流程 drawio(1)](https://github.com/user-attachments/assets/c5b06440-5cae-43e4-87cf-a8a4285bdbc7)
