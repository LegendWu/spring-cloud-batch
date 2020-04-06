## JOB定义表
DROP TABLE IF EXISTS BATCH_JOB;
CREATE TABLE `BATCH_JOB` (
   `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
   `JOB_NAME` varchar(100) DEFAULT NULL COMMENT '任务名称（bean ID）',
   `JOB_GROUP` varchar(255) DEFAULT NULL COMMENT '任务分组',
   `JOB_INSTANCE_ID` varchar(20) DEFAULT NULL COMMENT '任务实例ID（记录本次执行的实例，可用于续跑）',
   `CRON` varchar(20) DEFAULT NULL COMMENT '调度时间配置',
   `CRON_TEMPLATE` varchar(20) DEFAULT NULL COMMENT '调度时间配置模板',
   `DESCRIPTION` varchar(200) DEFAULT NULL COMMENT '任务描述',
   `STATUS` varchar(20) DEFAULT NULL COMMENT '任务状态',
   `IS_MULTI_RUN` varchar(10) DEFAULT 'YES' COMMENT '当天任务是否可以多次执行',
   `MAX_RETRY_TIMES` int(11) DEFAULT -1 COMMENT '最大异常重试次数（-1代表无限制）',
   `RETRY_TIMES` int(11) DEFAULT 0 COMMENT '当前重试次数',
   PRIMARY KEY (`ID`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 
## 任务参数配置表
DROP TABLE IF EXISTS BATCH_JOB_CONFIG;
CREATE TABLE `BATCH_JOB_CONFIG` (
   `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
   `CONF_CODE` varchar(30) DEFAULT NULL COMMENT '配置编码',
   `CONF_VALUE` varchar(500) DEFAULT NULL COMMENT '配置值',
   `CONF_DESC` varchar(1000) DEFAULT NULL COMMENT '配置描述',
   PRIMARY KEY (`ID`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

## 测试任务表
DROP TABLE IF EXISTS CAT;
CREATE TABLE `CAT` (
   `ID` int(11) NOT NULL AUTO_INCREMENT,
   `CATNAME` varchar(30) DEFAULT NULL,
   `CATAGE` varchar(10) DEFAULT NULL,
   `CATADDRESS` varchar(255) DEFAULT NULL,
   `IS_KEY_RANGE` int(11) DEFAULT 0,
   `IS_KEY_STORE` int(11) DEFAULT 0,
   `IS_ENTITY_STORE` int(11) DEFAULT 0,
   PRIMARY KEY (`ID`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 
## 任务参数配置表
DROP TABLE IF EXISTS BATCH_JOB_CONFIG;
CREATE TABLE `BATCH_JOB_CONFIG` (
   `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
   `CONF_CODE` varchar(30) DEFAULT NULL COMMENT '配置编码',
   `CONF_VALUE` varchar(500) DEFAULT NULL COMMENT '配置值',
   `CONF_DESC` varchar(1000) DEFAULT NULL COMMENT '配置描述',
   PRIMARY KEY (`ID`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;