-- MySQL dump 10.13  Distrib 8.0.42, for Linux (x86_64)
--
-- Host: localhost    Database: qknow_dev
-- ------------------------------------------------------
-- Server version	8.0.42-0ubuntu0.24.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `QRTZ_BLOB_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_BLOB_TRIGGERS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `QRTZ_BLOB_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Blob类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_BLOB_TRIGGERS`
--

LOCK TABLES `QRTZ_BLOB_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_BLOB_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_BLOB_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_CALENDARS`
--

DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_CALENDARS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`,`calendar_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='日历信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_CALENDARS`
--

LOCK TABLES `QRTZ_CALENDARS` WRITE;
/*!40000 ALTER TABLE `QRTZ_CALENDARS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_CALENDARS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_CRON_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_CRON_TRIGGERS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `QRTZ_CRON_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Cron类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_CRON_TRIGGERS`
--

LOCK TABLES `QRTZ_CRON_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_CRON_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_CRON_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_FIRED_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_FIRED_TRIGGERS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint NOT NULL COMMENT '触发的时间',
  `sched_time` bigint NOT NULL COMMENT '定时器制定的时间',
  `priority` int NOT NULL COMMENT '优先级',
  `state` varchar(16) NOT NULL COMMENT '状态',
  `job_name` varchar(200) DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`,`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='已触发的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_FIRED_TRIGGERS`
--

LOCK TABLES `QRTZ_FIRED_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_FIRED_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_FIRED_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_JOB_DETAILS`
--

DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_JOB_DETAILS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) NOT NULL COMMENT '任务组名',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`job_name`,`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_JOB_DETAILS`
--

LOCK TABLES `QRTZ_JOB_DETAILS` WRITE;
/*!40000 ALTER TABLE `QRTZ_JOB_DETAILS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_JOB_DETAILS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_LOCKS`
--

DROP TABLE IF EXISTS `QRTZ_LOCKS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_LOCKS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`,`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='存储的悲观锁信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_LOCKS`
--

LOCK TABLES `QRTZ_LOCKS` WRITE;
/*!40000 ALTER TABLE `QRTZ_LOCKS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_LOCKS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_PAUSED_TRIGGER_GRPS`
--

DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`,`trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='暂停的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_PAUSED_TRIGGER_GRPS`
--

LOCK TABLES `QRTZ_PAUSED_TRIGGER_GRPS` WRITE;
/*!40000 ALTER TABLE `QRTZ_PAUSED_TRIGGER_GRPS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_PAUSED_TRIGGER_GRPS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_SCHEDULER_STATE`
--

DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_SCHEDULER_STATE` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`,`instance_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='调度器状态表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_SCHEDULER_STATE`
--

LOCK TABLES `QRTZ_SCHEDULER_STATE` WRITE;
/*!40000 ALTER TABLE `QRTZ_SCHEDULER_STATE` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_SCHEDULER_STATE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_SIMPLE_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_SIMPLE_TRIGGERS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `QRTZ_SIMPLE_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='简单触发器的信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_SIMPLE_TRIGGERS`
--

LOCK TABLES `QRTZ_SIMPLE_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_SIMPLE_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_SIMPLE_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_SIMPROP_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_SIMPROP_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_SIMPROP_TRIGGERS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `QRTZ_SIMPROP_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='同步机制的行锁表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_SIMPROP_TRIGGERS`
--

LOCK TABLES `QRTZ_SIMPROP_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_SIMPROP_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_SIMPROP_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_TRIGGERS` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) NOT NULL COMMENT '触发器的类型',
  `start_time` bigint NOT NULL COMMENT '开始时间',
  `end_time` bigint DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  KEY `sched_name` (`sched_name`,`job_name`,`job_group`),
  CONSTRAINT `QRTZ_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `QRTZ_JOB_DETAILS` (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='触发器详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_TRIGGERS`
--

LOCK TABLES `QRTZ_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dm_datasource`
--

DROP TABLE IF EXISTS `dm_datasource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dm_datasource` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `datasource_name` varchar(128) NOT NULL COMMENT '数据源名称',
  `datasource_type` varchar(32) NOT NULL DEFAULT '0' COMMENT '数据源类型',
  `datasource_config` varchar(1024) DEFAULT NULL COMMENT '数据源配置(json字符串)',
  `ip` varchar(32) NOT NULL COMMENT 'IP地址',
  `port` int NOT NULL COMMENT '端口号',
  `list_count` int DEFAULT NULL COMMENT '数据库表数（预留）',
  `sync_count` int DEFAULT NULL COMMENT '同步记录数（预留）',
  `data_size` int DEFAULT NULL COMMENT '同步数据量大小（预留）',
  `description` varchar(1024) DEFAULT NULL COMMENT '描述',
  `valid_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 COMMENT='数据源';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dm_datasource`
--

LOCK TABLES `dm_datasource` WRITE;
/*!40000 ALTER TABLE `dm_datasource` DISABLE KEYS */;
INSERT INTO `dm_datasource` VALUES (1,'本地数据库','MySql','{\"username\":\"root\",\"password\":\"admin123\",\"dbname\":\"qknow_dev\"}','127.0.0.1',3306,NULL,NULL,NULL,NULL,1,0,'小桐',1,'2025-07-29 15:18:26','',NULL,'2025-07-29 15:18:26',NULL);
/*!40000 ALTER TABLE `dm_datasource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ext_attribute_mapping`
--

DROP TABLE IF EXISTS `ext_attribute_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ext_attribute_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `workspace_id` bigint NOT NULL COMMENT '工作区id',
  `task_id` bigint NOT NULL COMMENT '任务id',
  `table_name` varchar(128) NOT NULL COMMENT '表名',
  `table_comment` varchar(128) DEFAULT NULL COMMENT '表显示名称',
  `field_name` varchar(128) NOT NULL COMMENT '字段名',
  `field_comment` varchar(256) DEFAULT NULL COMMENT '字段显示名称',
  `attribute_id` bigint DEFAULT NULL COMMENT '属性id',
  `attribute_name` varchar(128) DEFAULT NULL COMMENT '属性名称',
  `valid_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 COMMENT='属性映射';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ext_attribute_mapping`
--

LOCK TABLES `ext_attribute_mapping` WRITE;
/*!40000 ALTER TABLE `ext_attribute_mapping` DISABLE KEYS */;
INSERT INTO `ext_attribute_mapping` VALUES (1,1001,1,'system_role','角色信息表','id','角色ID',4,'角色id',0,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(2,1001,1,'system_role','角色信息表','role_name','角色名称',5,'角色名称',0,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(3,1001,1,'system_user','用户信息表','id','用户ID',1,'用户id',0,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(4,1001,1,'system_user','用户信息表','user_name','用户账号',2,'用户名称',0,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL);
/*!40000 ALTER TABLE `ext_attribute_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ext_entity_pool`
--

DROP TABLE IF EXISTS `ext_entity_pool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ext_entity_pool` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `workspace_id` bigint NOT NULL COMMENT '工作区id',
  `task_id` bigint NOT NULL COMMENT '任务id',
  `doc_id` bigint NOT NULL COMMENT '文档id',
  `paragraph_index` int NOT NULL COMMENT '段落索引',
  `entity_id` varchar(128) NOT NULL COMMENT '实体ID',
  `entity_name` varchar(255) NOT NULL COMMENT '实体名称',
  `entity_type` varchar(128) NOT NULL COMMENT '实体类型',
  `aliases` text COMMENT '实体别名，JSON格式',
  `definition` text COMMENT '实体定义',
  `attributes` text COMMENT '实体属性，JSON格式',
  `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '处理状态;0：待处理，1：已确认，2：已拒绝',
  `process_time` datetime DEFAULT NULL COMMENT '处理时间',
  `processor_id` bigint DEFAULT NULL COMMENT '处理人id',
  `process_by` varchar(128) DEFAULT NULL COMMENT '处理人',
  `process_remark` varchar(512) DEFAULT NULL COMMENT '处理备注',
  `valid_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_status` (`status`),
  KEY `idx_entity_name` (`entity_name`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb3 COMMENT='实体池表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ext_entity_pool`
--

LOCK TABLES `ext_entity_pool` WRITE;
/*!40000 ALTER TABLE `ext_entity_pool` DISABLE KEYS */;
INSERT INTO `ext_entity_pool` VALUES (23,1001,3,3,1,'person_001_1753926297509','小明','人物','[]','在人工智能领域有着深厚造诣的科学家，2015年以优异成绩从清华大学计算机科学与技术专业毕业获博士学位，后加入字节跳动担任人工智能实验室核心研究员，专注自然语言处理和机器学习算法研究','{\"毕业院校\":[\"清华大学\"],\"工作单位\":[\"字节跳动\"],\"职位\":[\"人工智能实验室核心研究员\"],\"研究领域\":[\"自然语言处理\",\"机器学习算法\"]}',1,'2025-08-01 15:33:42',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:42',NULL),(24,1001,3,3,1,'person_002_1753926297509','小华','人物','[]','在生物科技领域取得突破性进展，本科和硕士就读北京大学，后赴美哈佛深造获博士学位，归国后加入华大基因任精准医疗研发中心高级科学家','{\"毕业院校\":[\"北京大学\",\"哈佛大学\"],\"工作单位\":[\"华大基因\"],\"职位\":[\"精准医疗研发中心高级科学家\"],\"研究领域\":[\"生物科技\",\"基因组学研究\"]}',1,'2025-08-01 15:33:42',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:42',NULL),(25,1001,3,3,1,'person_003_1753926297509','小李','人物','[]','专注新能源材料研究，毕业于上海交通大学机械与动力工程学院，现任职宁德时代新能源科技股份有限公司首席材料科学家','{\"毕业院校\":[\"上海交通大学\"],\"工作单位\":[\"宁德时代新能源科技股份有限公司\"],\"职位\":[\"首席材料科学家\"],\"研究领域\":[\"新能源材料研究\"]}',1,'2025-08-01 15:33:42',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:42',NULL),(26,1001,3,3,1,'organization_001_1753926297509','清华大学','组织','[\"清华\"]','中国顶尖学府，直属教育部，位列双一流、985工程、211工程，前身清华学堂','{\"所在地\":[\"北京市海淀区\"],\"建设项目\":[\"双一流\",\"985工程\",\"211工程\"],\"学校类型\":[\"全国重点大学\"]}',1,'2025-08-01 15:33:42',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:42',NULL),(27,1001,3,3,1,'organization_002_1753926297509','字节跳动','组织','[]','全球领先互联网科技公司，小明任职于此','{\"掌握技术\":[\"自然语言处理\",\"机器学习算法\"]}',1,'2025-08-01 15:33:42',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:42',NULL),(28,1001,3,3,1,'organization_003_1753926297509','北京大学','组织','[]','位于北京海淀区的著名综合性研究型大学，被誉为中国科学的摇篮','{\"所在地\":[\"北京市海淀区\"],\"学校类型\":[\"综合性研究型大学\"]}',1,'2025-08-01 15:33:42',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:42',NULL),(29,1001,3,3,1,'organization_004_1753926297509','哈佛大学','组织','[]','享誉世界的私立研究型大学，成立于1636年','{\"所在地\":[\"美国\"]}',1,'2025-08-01 15:33:42',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:42',NULL),(30,1001,3,3,1,'organization_005_1753926297509','华大基因','组织','[]','全球领先生命科学前沿机构，致力于基因组学研究及其在医学健康等领域的应用，小华任职于此','{\"所在地\":[\"上海张江高科技园区\"],\"研究领域\":[\"基因组学研究\",\"医学健康\"]}',1,'2025-08-01 15:33:42',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:42',NULL),(31,1001,3,3,1,'organization_006_1753926297509','上海交通大学','组织','[]','位于上海的顶尖工科强校，工程学科实力雄厚','{\"所在地\":[\"上海\"],\"掌握技术\":[\"机械与动力工程\"],\"学校类型\":[\"顶尖工科强校\"]}',1,'2025-08-01 15:33:43',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:43',NULL),(32,1001,3,3,1,'organization_007_1753926297509','宁德时代新能源科技股份有限公司','组织','[]','全球领先锂离子电池研发制造公司，总部位于福建宁德，在深圳设有重要研发中心，专注为电动汽车和储能系统提供高效能源解决方案','{\"所在地\":[\"福建宁德\",\"深圳\"],\"掌握技术\":[\"锂离子电池研发\"]}',1,'2025-08-01 15:33:43',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:43',NULL),(33,1001,3,3,1,'location_001_1753926297509','北京市海淀区','地点','[]','清华大学和北京大学所在地','{}',1,'2025-08-01 15:33:43',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:43',NULL),(34,1001,3,3,1,'location_002_1753926297509','北京市','地点','[]','北京大学所在地','{}',1,'2025-08-01 15:33:43',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:43',NULL),(35,1001,3,3,1,'location_003_1753926297509','上海','地点','[]','上海交通大学和华大基因所在地','{}',1,'2025-08-01 15:33:43',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:43',NULL),(36,1001,3,3,1,'location_004_1753926297509','美国','地点','[]','哈佛大学所在地','{}',1,'2025-08-01 15:33:43',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:43',NULL),(37,1001,3,3,1,'location_005_1753926297509','深圳','地点','[]','宁德时代深圳研发中心所在地','{}',1,'2025-08-01 15:33:43',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:43',NULL),(38,1001,3,3,1,'location_006_1753926297509','福建宁德','地点','[]','宁德时代总部所在地','{}',1,'2025-08-01 15:33:43',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:43',NULL),(39,1001,3,3,1,'location_007_1753926297509','上海张江高科技园区','地点','[]','华大基因所在地','{}',1,'2025-08-01 15:33:43',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:43',NULL),(40,1001,3,3,1,'technique_001_1753926297509','自然语言处理','技术','[]','','{}',1,'2025-08-01 15:33:43',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:43',NULL),(41,1001,3,3,1,'technique_002_1753926297509','机器学习算法','技术','[]','','{}',1,'2025-08-01 15:33:43',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:43',NULL),(42,1001,3,3,1,'technique_003_1753926297509','基因组学研究','技术','[]','','{}',1,'2025-08-01 15:33:43',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:43',NULL),(43,1001,3,3,1,'technique_004_1753926297509','新能源材料研究','技术','[]','','{}',1,'2025-08-01 15:33:43',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:43',NULL),(44,1001,3,3,1,'technique_005_1753926297509','机械与动力工程','技术','[]','','{}',1,'2025-08-01 15:33:43',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:43',NULL),(45,1001,3,3,1,'technique_006_1753926297509','锂离子电池研发','技术','[]','','{}',1,'2025-08-01 15:33:43',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:33:43',NULL),(46,1001,3,3,1,'organization_009_1753926297509','合肥工业大学','组织','合工大,肥工','是中华人民共和国教育部直属全国重点大学，教育部、工信部和安徽省政府共建高校，教育部与国防科工局共建高校。是“双一流”建设高校、国家“211工程”重点建设高校、国家“985工程”优势学科创新平台建设高校。入选“2011计划”、“111计划”、卓越工程师教育培养计划、国家大学生创新性实验计划、国家级大学生创新创业训练计划、全国高校实践育人创新创业基地、全国首批深化创新创业教育改革示范高校、全国创新创业典型经验高校。','{\"杰出校友\":[\"郭灵杰\",\"阮垚\"],\"合作院校\":[\"苏研院\",\"清华大学\"]}',1,'2025-08-01 15:33:43',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 15:32:33','小桐',NULL,'2025-08-01 15:33:43',NULL),(47,1001,3,3,1,'organization_003_1753926297509','北大','组织','Beijing University','中国顶尖学府','{\"合作企业\":[\"苏州空天信息研究院\"]}',0,NULL,NULL,NULL,NULL,1,0,'小桐',1,'2025-07-31 16:30:09','小桐',NULL,'2025-08-01 15:31:21',NULL);
/*!40000 ALTER TABLE `ext_entity_pool` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ext_relation_mapping`
--

DROP TABLE IF EXISTS `ext_relation_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ext_relation_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `workspace_id` bigint NOT NULL COMMENT '工作区id',
  `task_id` bigint NOT NULL COMMENT '任务id',
  `table_name` varchar(128) NOT NULL COMMENT '表名',
  `table_comment` varchar(128) DEFAULT NULL COMMENT '表显示名称',
  `field_name` varchar(128) NOT NULL COMMENT '字段名',
  `field_comment` varchar(256) DEFAULT NULL COMMENT '字段显示名称',
  `relation` varchar(128) NOT NULL COMMENT '关系',
  `relation_table` varchar(256) DEFAULT NULL COMMENT '关联表',
  `relation_table_name` varchar(128) DEFAULT NULL COMMENT '关联表名称',
  `relation_field` varchar(128) DEFAULT NULL COMMENT '关联表字段',
  `relation_name_field` varchar(128) DEFAULT NULL COMMENT '关联表实体字段',
  `valid_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 COMMENT='关系映射';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ext_relation_mapping`
--

LOCK TABLES `ext_relation_mapping` WRITE;
/*!40000 ALTER TABLE `ext_relation_mapping` DISABLE KEYS */;
INSERT INTO `ext_relation_mapping` VALUES (1,1001,1,'system_user','用户信息表','role_id','','角色','system_role','角色信息表','id','role_name',0,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL);
/*!40000 ALTER TABLE `ext_relation_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ext_relationship_pool`
--

DROP TABLE IF EXISTS `ext_relationship_pool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ext_relationship_pool` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `workspace_id` bigint NOT NULL COMMENT '工作区id',
  `task_id` bigint NOT NULL COMMENT '任务id',
  `doc_id` bigint NOT NULL COMMENT '文档id',
  `paragraph_index` int NOT NULL COMMENT '段落索引',
  `source_entity_id` varchar(128) NOT NULL COMMENT '源实体ID',
  `target_entity_id` varchar(128) NOT NULL COMMENT '目标实体ID',
  `relationship_type` varchar(128) NOT NULL COMMENT '关系类型',
  `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '处理状态;0：待处理，1：已确认，2：已拒绝',
  `process_time` datetime DEFAULT NULL COMMENT '处理时间',
  `processor_id` bigint DEFAULT NULL COMMENT '处理人id',
  `process_by` varchar(128) DEFAULT NULL COMMENT '处理人',
  `process_remark` varchar(512) DEFAULT NULL COMMENT '处理备注',
  `valid_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_status` (`status`),
  KEY `idx_source_target` (`source_entity_id`,`target_entity_id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb3 COMMENT='关系池表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ext_relationship_pool`
--

LOCK TABLES `ext_relationship_pool` WRITE;
/*!40000 ALTER TABLE `ext_relationship_pool` DISABLE KEYS */;
INSERT INTO `ext_relationship_pool` VALUES (26,1001,3,3,1,'person_001_1753926297509','organization_001_1753926297509','毕业院校',1,'2025-08-01 15:34:18',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:18',NULL),(27,1001,3,3,1,'person_001_1753926297509','organization_002_1753926297509','工作单位',1,'2025-08-01 15:34:18',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:18',NULL),(28,1001,3,3,1,'organization_001_1753926297509','location_001_1753926297509','所在地',1,'2025-08-01 15:34:18',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:18',NULL),(29,1001,3,3,1,'organization_002_1753926297509','technique_001_1753926297509','掌握技术',1,'2025-08-01 15:34:18',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:18',NULL),(30,1001,3,3,1,'organization_002_1753926297509','technique_002_1753926297509','掌握技术',1,'2025-08-01 15:34:18',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:18',NULL),(31,1001,3,3,1,'person_002_1753926297509','organization_003_1753926297509','毕业院校',1,'2025-08-01 15:34:18',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:18',NULL),(32,1001,3,3,1,'person_002_1753926297509','organization_004_1753926297509','毕业院校',1,'2025-08-01 15:34:18',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:18',NULL),(33,1001,3,3,1,'organization_003_1753926297509','location_001_1753926297509','所在地',1,'2025-08-01 15:34:18',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:18',NULL),(34,1001,3,3,1,'organization_004_1753926297509','location_004_1753926297509','所在地',1,'2025-08-01 15:34:18',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:18',NULL),(35,1001,3,3,1,'person_002_1753926297509','organization_005_1753926297509','工作单位',1,'2025-08-01 15:34:18',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:18',NULL),(36,1001,3,3,1,'organization_005_1753926297509','location_007_1753926297509','所在地',1,'2025-08-01 15:34:18',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:18',NULL),(37,1001,3,3,1,'person_003_1753926297509','organization_006_1753926297509','毕业院校',1,'2025-08-01 15:34:18',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:18',NULL),(38,1001,3,3,1,'organization_006_1753926297509','location_003_1753926297509','所在地',1,'2025-08-01 15:34:18',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:18',NULL),(39,1001,3,3,1,'person_003_1753926297509','organization_007_1753926297509','工作单位',1,'2025-08-01 15:34:18',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:18',NULL),(40,1001,3,3,1,'organization_007_1753926297509','location_006_1753926297509','所在地',1,'2025-08-01 15:34:18',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:18',NULL),(41,1001,3,3,1,'organization_007_1753926297509','location_005_1753926297509','所在地',1,'2025-08-01 15:34:19',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:19',NULL),(42,1001,3,3,1,'person_001_1753926297509','technique_001_1753926297509','掌握技术',1,'2025-08-01 15:34:19',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:19',NULL),(43,1001,3,3,1,'person_001_1753926297509','technique_002_1753926297509','掌握技术',1,'2025-08-01 15:34:19',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:19',NULL),(44,1001,3,3,1,'person_002_1753926297509','technique_003_1753926297509','掌握技术',1,'2025-08-01 15:34:19',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:19',NULL),(45,1001,3,3,1,'person_003_1753926297509','technique_004_1753926297509','掌握技术',1,'2025-08-01 15:34:19',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:19',NULL),(46,1001,3,3,1,'organization_006_1753926297509','technique_005_1753926297509','掌握技术',1,'2025-08-01 15:34:19',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:19',NULL),(47,1001,3,3,1,'organization_007_1753926297509','technique_006_1753926297509','掌握技术',1,'2025-08-01 15:34:19',NULL,NULL,'',1,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-08-01 15:34:19',NULL);
/*!40000 ALTER TABLE `ext_relationship_pool` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ext_schema`
--

DROP TABLE IF EXISTS `ext_schema`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ext_schema` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `workspace_id` bigint NOT NULL COMMENT '工作区id',
  `name` varchar(128) NOT NULL COMMENT '概念名称',
  `description` varchar(1024) DEFAULT NULL COMMENT '概念描述',
  `color` varchar(32) DEFAULT NULL COMMENT '概念颜色',
  `valid_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb3 COMMENT='概念配置';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ext_schema`
--

LOCK TABLES `ext_schema` WRITE;
/*!40000 ALTER TABLE `ext_schema` DISABLE KEYS */;
INSERT INTO `ext_schema` VALUES (1,1001,'人物',NULL,'#006EFE',1,1,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(2,1001,'歌曲',NULL,'#A109FF',1,1,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(3,1001,'国家',NULL,'#FD0E02',1,1,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(4,1001,'城市',NULL,'#FDB202',1,1,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(5,1001,'企业',NULL,'#FF0FDF',1,1,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(7,1001,'用户',NULL,'#0026FF',1,1,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(8,1001,'角色',NULL,'#33FF00',1,1,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(9,1001,'人物',NULL,'#D7BDE2',1,0,'小桐',1,'2025-07-30 22:33:43','小桐',NULL,'2025-07-30 22:33:43',NULL),(10,1001,'学校',NULL,'#A9CCE3',1,0,'小桐',1,'2025-07-30 22:33:53','小桐',NULL,'2025-07-30 22:33:53',NULL),(11,1001,'公司',NULL,'#FCF3CF',1,0,'小桐',1,'2025-07-30 22:34:01','小桐',NULL,'2025-07-30 22:34:01',NULL),(12,1001,'地点',NULL,'#FDEBD0',1,1,'小桐',1,'2025-07-30 22:34:40','小桐',NULL,'2025-07-30 22:34:40',NULL),(13,1001,'地点',NULL,'#A9CCE3',1,0,'小桐',1,'2025-07-30 22:36:26','小桐',NULL,'2025-07-30 22:36:26',NULL),(14,1001,'技术',NULL,'#A3E4D7',1,0,'小桐',1,'2025-07-30 22:36:49','小桐',NULL,'2025-07-30 22:36:49',NULL);
/*!40000 ALTER TABLE `ext_schema` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ext_schema_attribute`
--

DROP TABLE IF EXISTS `ext_schema_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ext_schema_attribute` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `workspace_id` bigint NOT NULL COMMENT '工作区id',
  `schema_id` bigint NOT NULL COMMENT '概念id',
  `schema_name` varchar(128) NOT NULL COMMENT '概念名称',
  `name` varchar(128) NOT NULL COMMENT '属性名称',
  `name_code` varchar(128) NOT NULL COMMENT '属性名称代码',
  `require_flag` tinyint(1) NOT NULL COMMENT '是否必填',
  `data_type` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '数据类型;0：文本，1：整数，2：小数，3：时间，4：字节类型，5：布尔值',
  `multiple_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '单/多值;0：单值，1：多值',
  `validate_type` tinyint unsigned DEFAULT NULL COMMENT '校验方式;0：唯一性校验，1：长度校验，2：区间校验',
  `min_value` decimal(10,0) DEFAULT NULL COMMENT '最小值（用于区间校验）',
  `max_value` decimal(10,0) DEFAULT NULL COMMENT '最大值（用于区间校验）',
  `valid_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 COMMENT='概念属性';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ext_schema_attribute`
--

LOCK TABLES `ext_schema_attribute` WRITE;
/*!40000 ALTER TABLE `ext_schema_attribute` DISABLE KEYS */;
INSERT INTO `ext_schema_attribute` VALUES (1,1001,7,'用户','用户id','id',1,1,0,0,NULL,NULL,1,1,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(2,1001,7,'用户','用户名称','user_name',1,0,0,1,NULL,256,1,1,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(3,1001,7,'用户','手机号码','phonenumber',0,0,0,1,NULL,11,1,1,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(4,1001,8,'角色','角色id','id',1,1,0,NULL,NULL,NULL,1,1,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(5,1001,8,'角色','角色名称','role_name',0,0,0,NULL,NULL,NULL,1,1,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL);
/*!40000 ALTER TABLE `ext_schema_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ext_schema_mapping`
--

DROP TABLE IF EXISTS `ext_schema_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ext_schema_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `workspace_id` bigint NOT NULL COMMENT '工作区id',
  `task_id` bigint NOT NULL COMMENT '任务id',
  `table_name` varchar(128) NOT NULL COMMENT '表名',
  `table_comment` varchar(128) DEFAULT NULL COMMENT '表显示名称',
  `entity_name_field` varchar(32) DEFAULT NULL COMMENT '实体名称列',
  `schema_id` bigint DEFAULT NULL COMMENT '概念id',
  `schema_name` varchar(128) DEFAULT NULL COMMENT '概念名称',
  `valid_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3 COMMENT='概念映射';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ext_schema_mapping`
--

LOCK TABLES `ext_schema_mapping` WRITE;
/*!40000 ALTER TABLE `ext_schema_mapping` DISABLE KEYS */;
INSERT INTO `ext_schema_mapping` VALUES (1,1001,1,'system_role','角色信息表','role_name',8,'角色',0,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(2,1001,1,'system_user','用户信息表','user_name',7,'用户',0,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL);
/*!40000 ALTER TABLE `ext_schema_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ext_schema_relation`
--

DROP TABLE IF EXISTS `ext_schema_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ext_schema_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `workspace_id` bigint NOT NULL COMMENT '工作区id',
  `start_schema_id` bigint NOT NULL COMMENT '起点概念id',
  `relation` varchar(128) NOT NULL COMMENT '关系',
  `end_schema_id` bigint NOT NULL COMMENT '终点概念id',
  `inverse_flag` tinyint(1) NOT NULL COMMENT '是否可逆',
  `valid_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3 COMMENT='关系配置';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ext_schema_relation`
--

LOCK TABLES `ext_schema_relation` WRITE;
/*!40000 ALTER TABLE `ext_schema_relation` DISABLE KEYS */;
INSERT INTO `ext_schema_relation` VALUES (1,1001,1,'创作',2,0,1,1,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(2,1001,1,'所属',3,0,1,1,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(3,1001,4,'所属',3,0,1,1,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(4,1001,5,'所属',4,0,1,1,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(5,1001,9,'毕业院校',10,0,1,0,'小桐',1,'2025-07-30 22:39:40','小桐',NULL,'2025-07-30 22:39:40',NULL),(6,1001,9,'工作单位',11,0,1,0,'小桐',1,'2025-07-30 22:39:56','小桐',NULL,'2025-07-30 22:39:56',NULL),(7,1001,10,'所在地',13,0,1,0,'小桐',1,'2025-07-30 22:40:13','小桐',NULL,'2025-07-30 22:40:13',NULL),(8,1001,11,'所在地',13,0,1,0,'小桐',1,'2025-07-30 22:40:21','小桐',NULL,'2025-07-30 22:40:21',NULL),(9,1001,10,'掌握技术',14,0,1,0,'小桐',1,'2025-07-30 22:40:58','小桐',NULL,'2025-07-30 22:40:58',NULL),(10,1001,9,'掌握技术',14,0,1,0,'小桐',1,'2025-07-30 22:41:08','小桐',NULL,'2025-07-30 22:41:08',NULL),(11,1001,11,'掌握技术',14,0,1,0,'小桐',1,'2025-07-30 22:41:18','小桐',NULL,'2025-07-30 22:41:18',NULL);
/*!40000 ALTER TABLE `ext_schema_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ext_struct_task`
--

DROP TABLE IF EXISTS `ext_struct_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ext_struct_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `workspace_id` bigint NOT NULL COMMENT '工作区id',
  `name` varchar(128) NOT NULL COMMENT '任务名称',
  `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '任务状态;0：未执行，1：进行中：2：已完成',
  `publish_status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '发布状态;0：未发布，1：已发布',
  `publish_time` datetime NOT NULL COMMENT '发布时间',
  `publisher_id` bigint NOT NULL COMMENT '发布人id',
  `publish_by` varchar(128) DEFAULT NULL COMMENT '发布人',
  `datasource_id` bigint NOT NULL COMMENT '数据源id',
  `datasource_name` varchar(128) NOT NULL COMMENT '数据源名称',
  `valid_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 COMMENT='结构化抽取任务';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ext_struct_task`
--

LOCK TABLES `ext_struct_task` WRITE;
/*!40000 ALTER TABLE `ext_struct_task` DISABLE KEYS */;
/*!40000 ALTER TABLE `ext_struct_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ext_unstruct_task`
--

DROP TABLE IF EXISTS `ext_unstruct_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ext_unstruct_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `workspace_id` bigint NOT NULL COMMENT '工作区id',
  `name` varchar(128) NOT NULL COMMENT '任务名称',
  `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '任务状态;0：未执行，1：进行中：2：已完成',
  `publish_status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '发布状态;0：未发布，1：已发布',
  `publish_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `publisher_id` bigint DEFAULT NULL COMMENT '发布人id',
  `publish_by` varchar(128) DEFAULT NULL COMMENT '发布人',
  `valid_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 COMMENT='非结构化抽取任务';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ext_unstruct_task`
--

LOCK TABLES `ext_unstruct_task` WRITE;
/*!40000 ALTER TABLE `ext_unstruct_task` DISABLE KEYS */;
INSERT INTO `ext_unstruct_task` VALUES (3,1001,'1',2,0,'2025-07-30 22:50:00',NULL,NULL,1,0,'小桐',1,'2025-07-30 22:50:00','小桐',NULL,'2025-07-30 22:50:00',NULL);
/*!40000 ALTER TABLE `ext_unstruct_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ext_unstruct_task_doc_rel`
--

DROP TABLE IF EXISTS `ext_unstruct_task_doc_rel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ext_unstruct_task_doc_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `workspace_id` bigint NOT NULL COMMENT '工作区id',
  `task_id` bigint NOT NULL COMMENT '任务id',
  `doc_id` bigint NOT NULL COMMENT '文件id',
  `doc_name` varchar(128) NOT NULL COMMENT '文件名',
  `valid_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 COMMENT='任务文件关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ext_unstruct_task_doc_rel`
--

LOCK TABLES `ext_unstruct_task_doc_rel` WRITE;
/*!40000 ALTER TABLE `ext_unstruct_task_doc_rel` DISABLE KEYS */;
INSERT INTO `ext_unstruct_task_doc_rel` VALUES (1,1001,1,1,'全球流行文化的交汇点.docx',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(2,1001,2,2,'硅谷的创新者与全球技术变革.docx',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(3,1001,3,3,'测试文档1.docx',1,0,'小桐',1,'2025-07-30 22:50:00','小桐',1,'2025-07-30 22:50:00',NULL);
/*!40000 ALTER TABLE `ext_unstruct_task_doc_rel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ext_unstruct_task_relation`
--

DROP TABLE IF EXISTS `ext_unstruct_task_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ext_unstruct_task_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `workspace_id` bigint NOT NULL COMMENT '工作区id',
  `task_id` bigint NOT NULL COMMENT '任务id',
  `relation_id` bigint NOT NULL COMMENT '关系id',
  `valid_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb3 COMMENT='任务关系关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ext_unstruct_task_relation`
--

LOCK TABLES `ext_unstruct_task_relation` WRITE;
/*!40000 ALTER TABLE `ext_unstruct_task_relation` DISABLE KEYS */;
INSERT INTO `ext_unstruct_task_relation` VALUES (1,1001,1,1,1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(2,1001,1,2,1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(3,1001,1,3,1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(4,1001,1,4,1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(5,1001,2,1,1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(6,1001,2,2,1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(7,1001,2,3,1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(8,1001,2,4,1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(9,1001,3,5,1,0,'小桐',1,'2025-07-30 22:50:00','小桐',NULL,'2025-07-30 22:50:00',NULL),(10,1001,3,6,1,0,'小桐',1,'2025-07-30 22:50:00','小桐',NULL,'2025-07-30 22:50:00',NULL),(11,1001,3,7,1,0,'小桐',1,'2025-07-30 22:50:00','小桐',NULL,'2025-07-30 22:50:00',NULL),(12,1001,3,8,1,0,'小桐',1,'2025-07-30 22:50:00','小桐',NULL,'2025-07-30 22:50:00',NULL),(13,1001,3,9,1,0,'小桐',1,'2025-07-30 22:50:00','小桐',NULL,'2025-07-30 22:50:00',NULL),(14,1001,3,10,1,0,'小桐',1,'2025-07-30 22:50:00','小桐',NULL,'2025-07-30 22:50:00',NULL),(15,1001,3,11,1,0,'小桐',1,'2025-07-30 22:50:00','小桐',NULL,'2025-07-30 22:50:00',NULL);
/*!40000 ALTER TABLE `ext_unstruct_task_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ext_unstruct_task_text`
--

DROP TABLE IF EXISTS `ext_unstruct_task_text`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ext_unstruct_task_text` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `workspace_id` bigint NOT NULL COMMENT '工作区id',
  `task_id` bigint NOT NULL COMMENT '任务id',
  `doc_id` bigint NOT NULL COMMENT '文件id',
  `paragraph_index` bigint DEFAULT NULL COMMENT '段落标识',
  `text` text NOT NULL COMMENT '文字内容',
  `valid_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb3 COMMENT='任务文件段落关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ext_unstruct_task_text`
--

LOCK TABLES `ext_unstruct_task_text` WRITE;
/*!40000 ALTER TABLE `ext_unstruct_task_text` DISABLE KEYS */;
INSERT INTO `ext_unstruct_task_text` VALUES (1,1001,1,1,1,'歌手周杰伦创作了多首经典歌曲，如《稻香》，这首歌深受中国各地听众的喜爱。',0,0,'小桐',NULL,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(2,1001,1,1,2,'在北京举办的华语乐坛盛事——第30届金曲奖颁奖典礼上，歌手邓紫棋凭借其歌曲《句号》获得了最佳女歌手奖。',0,0,'小桐',NULL,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(3,1001,1,1,3,'上海是中国电影产业的重要基地之一，这里诞生了许多优秀的作品，比如由导演徐峥执导的电影《我不是药神》。',0,0,'小桐',NULL,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(4,1001,2,2,1,'斯坦福大学位于美国加利福尼亚州硅谷，是一所世界领先的学府，培养了包括埃隆·马斯克在内的多位科技界领军人物。',0,0,'小桐',NULL,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(5,1001,2,2,2,'谷歌DeepMind总部设在旧金山湾区，该公司由英国伦敦的研究团队创建，并因开发出击败围棋冠军的AI程序《AlphaGo》而闻名。',0,0,'小桐',NULL,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(6,1001,2,2,3,'演员兼程序员梅丽莎·劳奇毕业于加州大学洛杉矶分校（UCLA），她曾在电视剧《生活大爆炸》中饰演科学家角色。',0,0,'小桐',NULL,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(7,1001,2,2,4,'东京属于温带季风气候，同时也孕育了许多面向全球市场的人工智能初创公司，如总部位于该市的Preferred Networks。',0,0,'小桐',NULL,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(8,1001,2,2,5,'日本歌手椎名林檎创作的歌曲《歌舞伎町女王》流行于东京都，同时她也为一部讲述人工智能伦理问题的日本电视综艺《未来人类》演唱过主题曲。',0,0,'小桐',NULL,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(9,1001,2,2,6,'中国杭州是互联网企业阿里巴巴集团的所在地，这座城市还以全年温和湿润的亚热带季风气候著称。',0,0,'小桐',NULL,'2025-05-27 13:35:02','小桐',1,'2025-07-29 15:18:27',NULL),(10,1001,2,2,10,'加拿大歌手格莱姆斯曾发布歌曲《We Appreciate Power》，探讨人工智能对社会的影响，并与埃隆·马斯克共同关注脑机接口技术的发展。',0,0,'小桐',NULL,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(12,1001,3,3,1,'在人工智能与生物科技交叉领域的前沿，汇聚了多位杰出的青年科学家。小明是一位在人工智能领域有着深厚造诣的科学家，他于2015年以优异的成绩从中国顶尖学府清华大学计算机科学与技术专业毕业，获得博士学位。清华大学，简称“清华”，坐落于北京市海淀区，是中国教育部直属的全国重点大学，位列国家“双一流”、“985工程”、“211工程”建设高校，其前身可追溯至1911年成立的清华学堂。毕业后，小明加入了全球领先的互联网科技公司字节跳动，担任人工智能实验室的核心研究员，专注于自然语言处理和机器学习算法的研究，为公司的技术创新做出了重要贡献。另一位青年才俊小华，在生物科技领域取得了突破性进展。她本科和硕士均就读于北京大学生命科学学院，北京大学同样位于北京海淀区，是中国著名的综合性研究型大学，被誉为“中国科学的摇篮”。小华随后赴美国哈佛大学深造并获得博士学位，哈佛大学是享誉世界的私立研究型大学，成立于1636年。学成归国后，小华加入了位于上海张江高科技园区的华大基因公司，华大基因是全球领先的生命科学前沿机构，致力于基因组学研究及其在医学健康等领域的应用，小华目前是该公司精准医疗研发中心的高级科学家。还有一位值得关注的学者小李，他专注于新能源材料研究。小李毕业于上海交通大学机械与动力工程学院，上海交通大学是位于上海的一所顶尖工科强校，历史悠久，工程学科实力雄厚。他目前在深圳的宁德时代新能源科技股份有限公司担任首席材料科学家，宁德时代是全球领先的锂离子电池研发制造公司，总部位于福建宁德，在深圳设有重要的研发中心，专注于为电动汽车和储能系统提供高效的能源解决方案。',0,0,'小桐',1,'2025-07-31 09:44:58','小桐',NULL,'2025-07-31 09:44:58',NULL);
/*!40000 ALTER TABLE `ext_unstruct_task_text` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table`
--

DROP TABLE IF EXISTS `gen_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table` (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table`
--

LOCK TABLES `gen_table` WRITE;
/*!40000 ALTER TABLE `gen_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `gen_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table_column`
--

DROP TABLE IF EXISTS `gen_table_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table_column` (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) DEFAULT '' COMMENT '字典类型',
  `sort` int DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table_column`
--

LOCK TABLES `gen_table_column` WRITE;
/*!40000 ALTER TABLE `gen_table_column` DISABLE KEYS */;
/*!40000 ALTER TABLE `gen_table_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kmc_category`
--

DROP TABLE IF EXISTS `kmc_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kmc_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `workspace_id` bigint NOT NULL COMMENT '工作区id',
  `parent_id` bigint NOT NULL COMMENT '父级id',
  `name` varchar(128) NOT NULL COMMENT '分类名称',
  `order_num` int DEFAULT NULL COMMENT '显示顺序',
  `ancestors` varchar(128) DEFAULT NULL COMMENT '祖级列表',
  `valid_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb3 COMMENT='知识分类';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kmc_category`
--

LOCK TABLES `kmc_category` WRITE;
/*!40000 ALTER TABLE `kmc_category` DISABLE KEYS */;
INSERT INTO `kmc_category` VALUES (1,1001,0,'文化与艺术',1,'0',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(2,1001,0,'历史事件与人物',2,'0',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(3,1001,0,'体育与娱乐',3,'0',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(4,1001,0,'自然环境与生态保护',4,'0',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(5,1001,0,'经济与发展',5,'0',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(6,1001,0,'健康与医疗',6,'0',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(7,1001,0,'科技创新与应用',7,'0',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(8,1001,1,'文学',1,'0,1',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(9,1001,1,'音乐与影视作品',2,'0,1',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(10,1001,1,'视觉艺术',3,'0,1',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(11,1001,1,'表演艺术',4,'0,1',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(12,1001,2,'古代文明',1,'0,2',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(13,1001,2,'近代历史',2,'0,2',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(14,1001,2,'现代史',3,'0,2',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(15,1001,3,'体育赛事',1,'0,3',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(16,1001,3,'影视娱乐',2,'0,3',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(17,1001,3,'游戏产业',3,'0,3',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(18,1001,2,'人物故事',0,'0,2',1,0,'小桐',1,'2025-07-30 22:42:25',NULL,NULL,'2025-07-30 22:42:25',NULL);
/*!40000 ALTER TABLE `kmc_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kmc_document`
--

DROP TABLE IF EXISTS `kmc_document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kmc_document` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `workspace_id` bigint NOT NULL COMMENT '工作区id',
  `category_id` bigint NOT NULL COMMENT '知识分类id',
  `category_name` varchar(128) DEFAULT NULL COMMENT '知识分类名称',
  `name` varchar(256) NOT NULL COMMENT '文件名称',
  `path` varchar(1024) NOT NULL COMMENT '文件路径',
  `description` varchar(1024) DEFAULT NULL COMMENT '文件描述',
  `valid_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 COMMENT='知识文件表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kmc_document`
--

LOCK TABLES `kmc_document` WRITE;
/*!40000 ALTER TABLE `kmc_document` DISABLE KEYS */;
INSERT INTO `kmc_document` VALUES (1,1001,9,'音乐与影视作品','全球流行文化的交汇点.docx','/2025/05/27/683517a88fceebcb4928de44.docx','用于测试非结构化抽取文件',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(2,1001,7,'科技创新与应用','硅谷的创新者与全球技术变革.docx','/2025/05/27/683540a58fce4f307dbf6f0a.docx','用于测试非结构化抽取文件',1,0,'小桐',1,'2025-07-29 15:18:27','小桐',1,'2025-07-29 15:18:27',NULL),(3,1001,18,'人物故事','测试文档1.docx','/2025/07/30/688a2f8cf3fc9675162f0db5.docx',NULL,1,0,'小桐',1,'2025-07-30 22:43:35','小桐',NULL,'2025-07-30 22:43:35',NULL),(4,1001,18,'人物故事','测试文档2.docx','/2025/07/30/688a2f9cf3fc9675162f0db6.docx',NULL,1,0,'小桐',1,'2025-07-30 22:43:41','小桐',NULL,'2025-07-30 22:43:41',NULL);
/*!40000 ALTER TABLE `kmc_document` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `sender_id` bigint DEFAULT NULL COMMENT '发送人',
  `receiver_id` bigint DEFAULT NULL COMMENT '接收人',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息标题',
  `content` varchar(3072) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息模板内容',
  `category` tinyint unsigned NOT NULL COMMENT '消息类别',
  `msg_level` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '消息等级',
  `module` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '消息模块',
  `entity_type` tinyint unsigned DEFAULT NULL COMMENT '实体类型',
  `entity_id` bigint DEFAULT NULL COMMENT '实体id',
  `entity_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息链接',
  `has_read` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  `has_retraction` tinyint(1) DEFAULT '0' COMMENT '是否撤回',
  `valid_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='消息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_config`
--

DROP TABLE IF EXISTS `system_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_config` (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_config`
--

LOCK TABLES `system_config` WRITE;
/*!40000 ALTER TABLE `system_config` DISABLE KEYS */;
INSERT INTO `system_config` VALUES (1,'主框架页-默认皮肤样式名称','sys.index.skinName','skin-blue','Y','小桐','2025-07-29 15:18:26','',NULL,'蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),(2,'用户管理-账号初始密码','sys.user.initPassword','123456','Y','小桐','2025-07-29 15:18:26','',NULL,'初始化密码 123456'),(3,'主框架页-侧边栏主题','sys.index.sideTheme','theme-dark','Y','小桐','2025-07-29 15:18:26','',NULL,'深色主题theme-dark，浅色主题theme-light'),(4,'账号自助-验证码开关','sys.account.captchaEnabled','false','Y','小桐','2025-07-29 15:18:26','',NULL,'是否开启验证码功能（true开启，false关闭）'),(5,'账号自助-是否开启用户注册功能','sys.account.registerUser','false','Y','小桐','2025-07-29 15:18:26','',NULL,'是否开启注册用户功能（true开启，false关闭）'),(6,'用户登录-黑名单列表','sys.login.blackIPList','','Y','小桐','2025-07-29 15:18:26','',NULL,'设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');
/*!40000 ALTER TABLE `system_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_content`
--

DROP TABLE IF EXISTS `system_content`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_content` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `sys_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '系统名称',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '系统logo',
  `login_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录页面logo',
  `carousel_image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '轮播图',
  `contact_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电子邮箱',
  `copyright` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '版权方',
  `record_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备案号',
  `del_flag` int DEFAULT NULL COMMENT '删除标记',
  `status` int DEFAULT NULL COMMENT '状态',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `creator_id` int DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `updater_id` int DEFAULT NULL COMMENT '修改人id',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_content`
--

LOCK TABLES `system_content` WRITE;
/*!40000 ALTER TABLE `system_content` DISABLE KEYS */;
INSERT INTO `system_content` VALUES (1,NULL,'','','','400-660-8208','support@qiantong.tech','Copyright© 2025 江苏千桐科技有限公司 版权所有','苏ICP备2022008519号-3',0,NULL,NULL,NULL,NULL,'小桐',1,'2025-01-13 13:18:06',NULL);
/*!40000 ALTER TABLE `system_content` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_dept`
--

DROP TABLE IF EXISTS `system_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_dept` (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) DEFAULT '' COMMENT '部门名称',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_dept`
--

LOCK TABLES `system_dept` WRITE;
/*!40000 ALTER TABLE `system_dept` DISABLE KEYS */;
INSERT INTO `system_dept` VALUES (100,0,'0','千桐科技',0,'唐朝辉','15888888888','support@qiantong.tech','0','0','小桐','2025-07-29 15:18:24','',NULL),(101,100,'0,100','南京总公司',1,'唐朝辉','15888888888','support@qiantong.tech','0','0','小桐','2025-07-29 15:18:24','',NULL),(102,100,'0,100','郑州分公司',2,'唐朝辉','15888888888','support@qiantong.tech','0','0','小桐','2025-07-29 15:18:24','',NULL),(103,101,'0,100,101','研发部门',1,'唐朝辉','15888888888','support@qiantong.tech','0','0','小桐','2025-07-29 15:18:24','',NULL),(104,101,'0,100,101','市场部门',2,'唐朝辉','15888888888','support@qiantong.tech','0','0','小桐','2025-07-29 15:18:24','',NULL),(105,101,'0,100,101','测试部门',3,'唐朝辉','15888888888','support@qiantong.tech','0','0','小桐','2025-07-29 15:18:24','',NULL),(106,101,'0,100,101','财务部门',4,'唐朝辉','15888888888','support@qiantong.tech','0','0','小桐','2025-07-29 15:18:24','',NULL),(107,101,'0,100,101','运维部门',5,'唐朝辉','15888888888','support@qiantong.tech','0','0','小桐','2025-07-29 15:18:24','',NULL),(108,102,'0,100,102','市场部门',1,'唐朝辉','15888888888','support@qiantong.tech','0','0','小桐','2025-07-29 15:18:24','',NULL),(109,102,'0,100,102','财务部门',2,'唐朝辉','15888888888','support@qiantong.tech','0','0','小桐','2025-07-29 15:18:24','',NULL);
/*!40000 ALTER TABLE `system_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_dict_data`
--

DROP TABLE IF EXISTS `system_dict_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_dict_data` (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_dict_data`
--

LOCK TABLES `system_dict_data` WRITE;
/*!40000 ALTER TABLE `system_dict_data` DISABLE KEYS */;
INSERT INTO `system_dict_data` VALUES (1,1,'男','0','sys_user_sex','','','Y','0','小桐','2025-07-29 15:18:26','',NULL,'性别男'),(2,2,'女','1','sys_user_sex','','','N','0','小桐','2025-07-29 15:18:26','',NULL,'性别女'),(3,3,'未知','2','sys_user_sex','','','N','0','小桐','2025-07-29 15:18:26','',NULL,'性别未知'),(4,1,'显示','0','sys_show_hide','','primary','Y','0','小桐','2025-07-29 15:18:26','',NULL,'显示菜单'),(5,2,'隐藏','1','sys_show_hide','','danger','N','0','小桐','2025-07-29 15:18:26','',NULL,'隐藏菜单'),(6,1,'正常','0','sys_normal_disable','','primary','Y','0','小桐','2025-07-29 15:18:26','',NULL,'正常状态'),(7,2,'停用','1','sys_normal_disable','','danger','N','0','小桐','2025-07-29 15:18:26','',NULL,'停用状态'),(8,1,'正常','0','sys_job_status','','primary','Y','0','小桐','2025-07-29 15:18:26','',NULL,'正常状态'),(9,2,'暂停','1','sys_job_status','','danger','N','0','小桐','2025-07-29 15:18:26','',NULL,'停用状态'),(10,1,'默认','DEFAULT','sys_job_group','','','Y','0','小桐','2025-07-29 15:18:26','',NULL,'默认分组'),(11,2,'系统','SYSTEM','sys_job_group','','','N','0','小桐','2025-07-29 15:18:26','',NULL,'系统分组'),(12,1,'是','Y','sys_yes_no','','primary','Y','0','小桐','2025-07-29 15:18:26','',NULL,'系统默认是'),(13,2,'否','N','sys_yes_no','','danger','N','0','小桐','2025-07-29 15:18:26','',NULL,'系统默认否'),(14,1,'通知','1','sys_notice_type','','warning','Y','0','小桐','2025-07-29 15:18:26','',NULL,'通知'),(15,2,'公告','2','sys_notice_type','','success','N','0','小桐','2025-07-29 15:18:26','',NULL,'公告'),(16,1,'正常','0','sys_notice_status','','primary','Y','0','小桐','2025-07-29 15:18:26','',NULL,'正常状态'),(17,2,'关闭','1','sys_notice_status','','danger','N','0','小桐','2025-07-29 15:18:26','',NULL,'关闭状态'),(18,99,'其他','0','sys_oper_type','','info','N','0','小桐','2025-07-29 15:18:26','',NULL,'其他操作'),(19,1,'新增','1','sys_oper_type','','info','N','0','小桐','2025-07-29 15:18:26','',NULL,'新增操作'),(20,2,'修改','2','sys_oper_type','','info','N','0','小桐','2025-07-29 15:18:26','',NULL,'修改操作'),(21,3,'删除','3','sys_oper_type','','danger','N','0','小桐','2025-07-29 15:18:26','',NULL,'删除操作'),(22,4,'授权','4','sys_oper_type','','primary','N','0','小桐','2025-07-29 15:18:26','',NULL,'授权操作'),(23,5,'导出','5','sys_oper_type','','warning','N','0','小桐','2025-07-29 15:18:26','',NULL,'导出操作'),(24,6,'导入','6','sys_oper_type','','warning','N','0','小桐','2025-07-29 15:18:26','',NULL,'导入操作'),(25,7,'强退','7','sys_oper_type','','danger','N','0','小桐','2025-07-29 15:18:26','',NULL,'强退操作'),(26,8,'生成代码','8','sys_oper_type','','warning','N','0','小桐','2025-07-29 15:18:26','',NULL,'生成操作'),(27,9,'清空数据','9','sys_oper_type','','danger','N','0','小桐','2025-07-29 15:18:26','',NULL,'清空操作'),(28,1,'成功','0','sys_common_status','','primary','N','0','小桐','2025-07-29 15:18:26','',NULL,'正常状态'),(29,2,'失败','1','sys_common_status','','danger','N','0','小桐','2025-07-29 15:18:26','',NULL,'停用状态'),(30,0,'文本','0','ext_data_type','','default','N','0','小桐','2025-07-29 15:18:26','',NULL,'文本类型'),(31,1,'整数','1','ext_data_type','','default','N','0','小桐','2025-07-29 15:18:26','',NULL,'整数类型'),(32,2,'小数','2','ext_data_type','','default','N','0','小桐','2025-07-29 15:18:26','',NULL,'小数类型'),(33,3,'时间','3','ext_data_type','','default','N','0','小桐','2025-07-29 15:18:26','',NULL,'时间类型'),(34,4,'字节类型','4','ext_data_type','','default','N','0','小桐','2025-07-29 15:18:26','',NULL,'字节类型'),(35,5,'布尔值','5','ext_data_type','','default','N','0','小桐','2025-07-29 15:18:26','',NULL,'布尔值类型'),(36,0,'唯一性校验','0','ext_data_check','','default','N','0','小桐','2025-07-29 15:18:26','',NULL,'唯一性校验'),(37,1,'长度校验','1','ext_data_check','','default','N','0','小桐','2025-07-29 15:18:26','',NULL,'长度校验'),(38,2,'区间校验','2','ext_data_check','','default','N','0','小桐','2025-07-29 15:18:26','',NULL,'区间校验'),(39,0,'未发布','0','publish_status','','warning','N','0','小桐','2025-07-29 15:18:26','',NULL,'未发布状态'),(40,1,'已发布','1','publish_status','','success','N','0','小桐','2025-07-29 15:18:26','',NULL,'已发布状态'),(41,0,'未执行','0','ext_task_status','','primary','N','0','小桐','2025-07-29 15:18:26','',NULL,'未执行状态'),(42,1,'进行中','1','ext_task_status','','warning','N','0','小桐','2025-07-29 15:18:26','',NULL,'进行中状态'),(43,2,'已完成','2','ext_task_status','','success','N','0','小桐','2025-07-29 15:18:26','',NULL,'已完成状态'),(44,3,'执行失败','3','ext_task_status','','danger','N','0','小桐','2025-07-29 15:18:26','',NULL,'执行失败状态'),(45,1,'MySql','MySql','datasource_type','','primary','N','0','小桐','2025-07-29 15:18:26','',NULL,'MySql数据库'),(46,2,'DM8','DM8','datasource_type','','primary','N','1','小桐','2025-07-29 15:18:26','',NULL,'达梦8数据库'),(47,3,'Oracle','Oracle','datasource_type','','primary','N','1','小桐','2025-07-29 15:18:26','',NULL,'Oracle数据库'),(48,4,'Oracle11','Oracle11','datasource_type','','primary','N','1','小桐','2025-07-29 15:18:26','',NULL,'Oracle11数据库'),(49,5,'Kingbase8','Kingbase8','datasource_type','','primary','N','1','小桐','2025-07-29 15:18:26','',NULL,'人大金仓8数据库');
/*!40000 ALTER TABLE `system_dict_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_dict_type`
--

DROP TABLE IF EXISTS `system_dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_dict_type` (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_dict_type`
--

LOCK TABLES `system_dict_type` WRITE;
/*!40000 ALTER TABLE `system_dict_type` DISABLE KEYS */;
INSERT INTO `system_dict_type` VALUES (1,'用户性别','sys_user_sex','0','小桐','2025-07-29 15:18:25','',NULL,'用户性别列表'),(2,'菜单状态','sys_show_hide','0','小桐','2025-07-29 15:18:25','',NULL,'菜单状态列表'),(3,'系统开关','sys_normal_disable','0','小桐','2025-07-29 15:18:25','',NULL,'系统开关列表'),(4,'任务状态','sys_job_status','0','小桐','2025-07-29 15:18:25','',NULL,'任务状态列表'),(5,'任务分组','sys_job_group','0','小桐','2025-07-29 15:18:25','',NULL,'任务分组列表'),(6,'系统是否','sys_yes_no','0','小桐','2025-07-29 15:18:25','',NULL,'系统是否列表'),(7,'通知类型','sys_notice_type','0','小桐','2025-07-29 15:18:25','',NULL,'通知类型列表'),(8,'通知状态','sys_notice_status','0','小桐','2025-07-29 15:18:25','',NULL,'通知状态列表'),(9,'操作类型','sys_oper_type','0','小桐','2025-07-29 15:18:26','',NULL,'操作类型列表'),(10,'系统状态','sys_common_status','0','小桐','2025-07-29 15:18:26','',NULL,'登录状态列表'),(11,'数据类型','ext_data_type','0','小桐','2025-07-29 15:18:26','',NULL,'数据类型列表'),(12,'数据校验','ext_data_check','0','小桐','2025-07-29 15:18:26','',NULL,'数据校验列表'),(13,'发布状态','publish_status','0','小桐','2025-07-29 15:18:26','',NULL,'发布状态列表'),(14,'任务执行状态','ext_task_status','0','小桐','2025-07-29 15:18:26','',NULL,'任务执行状态列表'),(15,'数据源类型','datasource_type','0','小桐','2025-07-29 15:18:26','',NULL,'数据源类型列表');
/*!40000 ALTER TABLE `system_dict_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_job`
--

DROP TABLE IF EXISTS `system_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_job`
--

LOCK TABLES `system_job` WRITE;
/*!40000 ALTER TABLE `system_job` DISABLE KEYS */;
INSERT INTO `system_job` VALUES (1,'系统默认（无参）','DEFAULT','ryTask.ryNoParams','0/10 * * * * ?','3','1','1','小桐','2025-07-29 15:18:26','',NULL,''),(2,'系统默认（有参）','DEFAULT','ryTask.ryParams(\'ry\')','0/15 * * * * ?','3','1','1','小桐','2025-07-29 15:18:26','',NULL,''),(3,'系统默认（多参）','DEFAULT','ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)','0/20 * * * * ?','3','1','1','小桐','2025-07-29 15:18:26','',NULL,''),(100,'结构化任务抽取','DEFAULT','extStructTaskServiceImpl.consumeQueue()','0 0/1 * * * ?','1','1','1','小桐','2025-07-29 15:18:26','',NULL,''),(101,'非结构化任务抽取','DEFAULT','extUnstructTaskServiceImpl.consumeQueue()','0 0/1 * * * ?','1','1','1','小桐','2025-07-29 15:18:26','','2025-07-30 22:55:51','');
/*!40000 ALTER TABLE `system_job` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_job_log`
--

DROP TABLE IF EXISTS `system_job_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_job_log` (
  `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
  `status` char(1) DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) DEFAULT '' COMMENT '异常信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_job_log`
--

LOCK TABLES `system_job_log` WRITE;
/*!40000 ALTER TABLE `system_job_log` DISABLE KEYS */;
INSERT INTO `system_job_log` VALUES (1,'非结构化任务抽取','DEFAULT','extUnstructTaskServiceImpl.consumeQueue()','非结构化任务抽取 总共耗时：3毫秒','0','','2025-07-30 23:00:00'),(2,'非结构化任务抽取','DEFAULT','extUnstructTaskServiceImpl.consumeQueue()','非结构化任务抽取 总共耗时：9毫秒','0','','2025-07-30 23:05:00'),(3,'非结构化任务抽取','DEFAULT','extUnstructTaskServiceImpl.consumeQueue()','非结构化任务抽取 总共耗时：3572毫秒','0','','2025-07-30 23:10:03'),(4,'非结构化任务抽取','DEFAULT','extUnstructTaskServiceImpl.consumeQueue()','非结构化任务抽取 总共耗时：1毫秒','0','','2025-07-30 23:15:00');
/*!40000 ALTER TABLE `system_job_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_logininfor`
--

DROP TABLE IF EXISTS `system_logininfor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_logininfor` (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT '' COMMENT '操作系统',
  `status` char(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`),
  KEY `idx_system_logininfor_s` (`status`),
  KEY `idx_system_logininfor_lt` (`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=238 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统访问记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_logininfor`
--

LOCK TABLES `system_logininfor` WRITE;
/*!40000 ALTER TABLE `system_logininfor` DISABLE KEYS */;
INSERT INTO `system_logininfor` VALUES (100,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-29 15:33:42'),(101,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-29 15:43:25'),(102,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:48:50'),(103,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:48:50'),(104,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:48:50'),(105,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:48:50'),(106,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:48:50'),(107,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码错误','2025-07-29 15:49:18'),(108,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:49:18'),(109,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:49:18'),(110,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:49:18'),(111,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:49:18'),(112,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码错误','2025-07-29 15:49:55'),(113,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:49:55'),(114,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:49:55'),(115,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:49:55'),(116,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:49:55'),(117,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:49:55'),(118,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:49:55'),(119,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:49:55'),(120,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:49:55'),(121,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:49:55'),(122,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:49:55'),(123,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:49:55'),(124,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:49:55'),(125,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:49:55'),(126,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:49:55'),(127,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','退出成功','2025-07-29 15:53:31'),(128,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-29 15:53:36'),(129,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码错误','2025-07-29 15:53:54'),(130,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:53:54'),(131,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:53:54'),(132,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:53:54'),(133,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:53:54'),(134,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:53:54'),(135,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:53:54'),(136,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:53:54'),(137,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:53:54'),(138,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:53:54'),(139,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码错误','2025-07-29 15:54:17'),(140,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:17'),(141,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:17'),(142,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:17'),(143,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:18'),(144,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:18'),(145,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:18'),(146,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:18'),(147,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:18'),(148,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:18'),(149,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:18'),(150,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:18'),(151,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:18'),(152,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:18'),(153,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:18'),(154,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:18'),(155,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:18'),(156,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:18'),(157,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:18'),(158,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:18'),(159,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-29 15:54:18'),(160,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-29 15:57:23'),(161,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-29 16:15:39'),(162,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 08:48:10'),(163,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','退出成功','2025-07-30 11:04:01'),(164,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 11:04:08'),(165,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 11:26:11'),(166,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 11:31:21'),(167,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 11:34:25'),(168,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-30 11:34:27'),(169,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码错误','2025-07-30 11:34:33'),(170,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-30 11:34:42'),(171,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码错误','2025-07-30 11:34:49'),(172,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','退出成功','2025-07-30 11:45:13'),(173,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 11:45:18'),(174,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','退出成功','2025-07-30 11:54:13'),(175,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 14:01:32'),(176,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 14:06:16'),(177,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 14:08:44'),(178,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 14:12:48'),(179,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 14:14:14'),(180,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 14:16:49'),(181,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 14:23:17'),(182,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 14:30:52'),(183,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 14:35:11'),(184,'qKnow','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-30 14:36:58'),(185,'qKnow','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码错误','2025-07-30 14:37:06'),(186,'qKnow','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-30 14:37:10'),(187,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 14:38:28'),(188,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','1','验证码错误','2025-07-30 14:41:18'),(189,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 14:41:23'),(190,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 14:49:33'),(191,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 15:44:31'),(192,'qKnow','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-30 16:24:43'),(193,'qKnow','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码错误','2025-07-30 16:24:55'),(194,'qKnow','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码已失效','2025-07-30 16:25:01'),(195,'qKnow','127.0.0.1','内网IP','Downloading Tool','Unknown','1','验证码错误','2025-07-30 16:25:10'),(196,'qKnow','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2025-07-30 16:26:22'),(197,'qKnow','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2025-07-30 16:26:45'),(198,'qKnow','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2025-07-30 16:30:01'),(199,'qKnow','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2025-07-30 16:36:42'),(200,'admin','127.0.0.1','内网IP','Chrome 13','Linux','1','用户不存在/密码错误','2025-07-30 17:09:07'),(201,'admin','127.0.0.1','内网IP','Chrome 13','Linux','1','用户不存在/密码错误','2025-07-30 17:09:19'),(202,'admin','127.0.0.1','内网IP','Chrome 13','Linux','1','用户不存在/密码错误','2025-07-30 17:09:24'),(203,'admin','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 17:09:51'),(204,'admin','127.0.0.1','内网IP','Chrome 13','Linux','0','退出成功','2025-07-30 17:10:18'),(205,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 17:21:01'),(206,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-30 23:10:20'),(207,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-31 17:36:41'),(208,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-31 17:41:50'),(209,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-31 17:49:09'),(210,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-31 17:53:32'),(211,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-31 18:06:59'),(212,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-31 18:22:27'),(213,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-31 19:51:33'),(214,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-31 19:55:57'),(215,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-31 22:10:06'),(216,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-31 22:51:27'),(217,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-31 22:56:52'),(218,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-07-31 23:06:03'),(219,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-08-01 09:01:33'),(220,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-08-01 09:14:13'),(221,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-08-01 09:30:31'),(222,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-08-01 09:55:55'),(223,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-08-01 10:10:20'),(224,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-08-01 10:15:35'),(225,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-08-01 10:27:10'),(226,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-08-01 10:28:50'),(227,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-08-01 10:32:14'),(228,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-08-01 10:36:14'),(229,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-08-01 10:37:01'),(230,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-08-01 10:40:24'),(231,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-08-01 11:32:19'),(232,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-08-01 13:55:43'),(233,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-08-01 14:10:51'),(234,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-08-01 15:33:05'),(235,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-08-01 17:18:49'),(236,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-08-01 17:52:59'),(237,'qKnow','127.0.0.1','内网IP','Chrome 13','Linux','0','登录成功','2025-08-04 09:38:23');
/*!40000 ALTER TABLE `system_logininfor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_menu`
--

DROP TABLE IF EXISTS `system_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) DEFAULT '' COMMENT '路由名称',
  `is_frame` int DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2117 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_menu`
--

LOCK TABLES `system_menu` WRITE;
/*!40000 ALTER TABLE `system_menu` DISABLE KEYS */;
INSERT INTO `system_menu` VALUES (1,'系统管理',0,5,'system',NULL,'','',1,0,'M','0','0','','系统设置','小桐','2025-07-29 15:18:24','',NULL,'系统管理目录'),(2,'系统监控',0,6,'monitor',NULL,'','',1,0,'M','0','0','','系统监控','小桐','2025-07-29 15:18:24','',NULL,'系统监控目录'),(3,'系统工具',0,7,'tool',NULL,'','',1,0,'M','0','0','','系统工具','小桐','2025-07-29 15:18:24','',NULL,'系统工具目录'),(100,'用户管理',1,1,'user','system/system/user/index','','',1,0,'C','0','0','system:user:list','user','小桐','2025-07-29 15:18:24','',NULL,'用户管理菜单'),(101,'角色管理',1,2,'role','system/system/role/index','','',1,0,'C','0','0','system:role:list','peoples','小桐','2025-07-29 15:18:24','',NULL,'角色管理菜单'),(102,'菜单管理',1,3,'menu','system/system/menu/index','','',1,0,'C','0','0','system:menu:list','tree-table','小桐','2025-07-29 15:18:24','',NULL,'菜单管理菜单'),(103,'部门管理',1,4,'dept','system/system/dept/index','','',1,0,'C','0','0','system:dept:list','tree','小桐','2025-07-29 15:18:24','',NULL,'部门管理菜单'),(104,'岗位管理',1,5,'post','system/system/post/index','','',1,0,'C','0','0','system:post:list','post','小桐','2025-07-29 15:18:24','',NULL,'岗位管理菜单'),(105,'字典管理',1,6,'dict','system/system/dict/index','','',1,0,'C','0','0','system:dict:list','dict','小桐','2025-07-29 15:18:24','',NULL,'字典管理菜单'),(106,'参数设置',1,7,'config','system/system/config/index','','',1,0,'C','0','0','system:config:list','edit','小桐','2025-07-29 15:18:24','',NULL,'参数设置菜单'),(107,'通知公告',1,8,'notice','system/system/notice/index','','',1,0,'C','0','0','system:notice:list','message','小桐','2025-07-29 15:18:24','',NULL,'通知公告菜单'),(108,'日志管理',1,9,'log','','','',1,0,'M','0','0','','log','小桐','2025-07-29 15:18:24','',NULL,'日志管理菜单'),(109,'在线用户',2,1,'online','system/monitor/online/index','','',1,0,'C','0','0','monitor:online:list','online','小桐','2025-07-29 15:18:24','',NULL,'在线用户菜单'),(110,'定时任务',2,2,'job','system/monitor/job/index','','',1,0,'C','0','0','monitor:job:list','job','小桐','2025-07-29 15:18:24','',NULL,'定时任务菜单'),(111,'服务监控',2,4,'server','system/monitor/server/index','','',1,0,'C','0','0','monitor:server:list','server','小桐','2025-07-29 15:18:24','',NULL,'服务监控菜单'),(112,'缓存监控',2,5,'cache','system/monitor/cache/index','','',1,0,'C','0','0','monitor:cache:list','redis','小桐','2025-07-29 15:18:24','',NULL,'缓存监控菜单'),(113,'缓存列表',2,6,'cacheList','system/monitor/cache/list','','',1,0,'C','0','0','monitor:cache:list','redis-list','小桐','2025-07-29 15:18:24','',NULL,'缓存列表菜单'),(114,'代码生成',3,2,'gen','system/tool/gen/index','','',1,0,'C','0','0','tool:gen:list','code','小桐','2025-07-29 15:18:24','',NULL,'代码生成菜单'),(115,'系统接口',3,3,'swagger','system/tool/swagger/index','','',1,0,'C','0','0','tool:swagger:list','swagger','小桐','2025-07-29 15:18:24','',NULL,'系统接口菜单'),(500,'操作日志',108,1,'operlog','system/monitor/operlog/index','','',1,0,'C','0','0','monitor:operlog:list','form','小桐','2025-07-29 15:18:24','',NULL,'操作日志菜单'),(501,'登录日志',108,2,'logininfor','system/monitor/logininfor/index','','',1,0,'C','0','0','monitor:logininfor:list','logininfor','小桐','2025-07-29 15:18:24','',NULL,'登录日志菜单'),(1000,'用户查询',100,1,'','','','',1,0,'F','0','0','system:user:query','#','小桐','2025-07-29 15:18:24','',NULL,''),(1001,'用户新增',100,2,'','','','',1,0,'F','0','0','system:user:add','#','小桐','2025-07-29 15:18:24','',NULL,''),(1002,'用户修改',100,3,'','','','',1,0,'F','0','0','system:user:edit','#','小桐','2025-07-29 15:18:24','',NULL,''),(1003,'用户删除',100,4,'','','','',1,0,'F','0','0','system:user:remove','#','小桐','2025-07-29 15:18:24','',NULL,''),(1004,'用户导出',100,5,'','','','',1,0,'F','0','0','system:user:export','#','小桐','2025-07-29 15:18:24','',NULL,''),(1005,'用户导入',100,6,'','','','',1,0,'F','0','0','system:user:import','#','小桐','2025-07-29 15:18:24','',NULL,''),(1006,'重置密码',100,7,'','','','',1,0,'F','0','0','system:user:resetPwd','#','小桐','2025-07-29 15:18:24','',NULL,''),(1007,'角色查询',101,1,'','','','',1,0,'F','0','0','system:role:query','#','小桐','2025-07-29 15:18:24','',NULL,''),(1008,'角色新增',101,2,'','','','',1,0,'F','0','0','system:role:add','#','小桐','2025-07-29 15:18:24','',NULL,''),(1009,'角色修改',101,3,'','','','',1,0,'F','0','0','system:role:edit','#','小桐','2025-07-29 15:18:24','',NULL,''),(1010,'角色删除',101,4,'','','','',1,0,'F','0','0','system:role:remove','#','小桐','2025-07-29 15:18:24','',NULL,''),(1011,'角色导出',101,5,'','','','',1,0,'F','0','0','system:role:export','#','小桐','2025-07-29 15:18:24','',NULL,''),(1012,'菜单查询',102,1,'','','','',1,0,'F','0','0','system:menu:query','#','小桐','2025-07-29 15:18:25','',NULL,''),(1013,'菜单新增',102,2,'','','','',1,0,'F','0','0','system:menu:add','#','小桐','2025-07-29 15:18:25','',NULL,''),(1014,'菜单修改',102,3,'','','','',1,0,'F','0','0','system:menu:edit','#','小桐','2025-07-29 15:18:25','',NULL,''),(1015,'菜单删除',102,4,'','','','',1,0,'F','0','0','system:menu:remove','#','小桐','2025-07-29 15:18:25','',NULL,''),(1016,'部门查询',103,1,'','','','',1,0,'F','0','0','system:dept:query','#','小桐','2025-07-29 15:18:25','',NULL,''),(1017,'部门新增',103,2,'','','','',1,0,'F','0','0','system:dept:add','#','小桐','2025-07-29 15:18:25','',NULL,''),(1018,'部门修改',103,3,'','','','',1,0,'F','0','0','system:dept:edit','#','小桐','2025-07-29 15:18:25','',NULL,''),(1019,'部门删除',103,4,'','','','',1,0,'F','0','0','system:dept:remove','#','小桐','2025-07-29 15:18:25','',NULL,''),(1020,'岗位查询',104,1,'','','','',1,0,'F','0','0','system:post:query','#','小桐','2025-07-29 15:18:25','',NULL,''),(1021,'岗位新增',104,2,'','','','',1,0,'F','0','0','system:post:add','#','小桐','2025-07-29 15:18:25','',NULL,''),(1022,'岗位修改',104,3,'','','','',1,0,'F','0','0','system:post:edit','#','小桐','2025-07-29 15:18:25','',NULL,''),(1023,'岗位删除',104,4,'','','','',1,0,'F','0','0','system:post:remove','#','小桐','2025-07-29 15:18:25','',NULL,''),(1024,'岗位导出',104,5,'','','','',1,0,'F','0','0','system:post:export','#','小桐','2025-07-29 15:18:25','',NULL,''),(1025,'字典查询',105,1,'#','','','',1,0,'F','0','0','system:dict:query','#','小桐','2025-07-29 15:18:25','',NULL,''),(1026,'字典新增',105,2,'#','','','',1,0,'F','0','0','system:dict:add','#','小桐','2025-07-29 15:18:25','',NULL,''),(1027,'字典修改',105,3,'#','','','',1,0,'F','0','0','system:dict:edit','#','小桐','2025-07-29 15:18:25','',NULL,''),(1028,'字典删除',105,4,'#','','','',1,0,'F','0','0','system:dict:remove','#','小桐','2025-07-29 15:18:25','',NULL,''),(1029,'字典导出',105,5,'#','','','',1,0,'F','0','0','system:dict:export','#','小桐','2025-07-29 15:18:25','',NULL,''),(1030,'参数查询',106,1,'#','','','',1,0,'F','0','0','system:config:query','#','小桐','2025-07-29 15:18:25','',NULL,''),(1031,'参数新增',106,2,'#','','','',1,0,'F','0','0','system:config:add','#','小桐','2025-07-29 15:18:25','',NULL,''),(1032,'参数修改',106,3,'#','','','',1,0,'F','0','0','system:config:edit','#','小桐','2025-07-29 15:18:25','',NULL,''),(1033,'参数删除',106,4,'#','','','',1,0,'F','0','0','system:config:remove','#','小桐','2025-07-29 15:18:25','',NULL,''),(1034,'参数导出',106,5,'#','','','',1,0,'F','0','0','system:config:export','#','小桐','2025-07-29 15:18:25','',NULL,''),(1035,'公告查询',107,1,'#','','','',1,0,'F','0','0','system:notice:query','#','小桐','2025-07-29 15:18:25','',NULL,''),(1036,'公告新增',107,2,'#','','','',1,0,'F','0','0','system:notice:add','#','小桐','2025-07-29 15:18:25','',NULL,''),(1037,'公告修改',107,3,'#','','','',1,0,'F','0','0','system:notice:edit','#','小桐','2025-07-29 15:18:25','',NULL,''),(1038,'公告删除',107,4,'#','','','',1,0,'F','0','0','system:notice:remove','#','小桐','2025-07-29 15:18:25','',NULL,''),(1039,'操作查询',500,1,'#','','','',1,0,'F','0','0','monitor:operlog:query','#','小桐','2025-07-29 15:18:25','',NULL,''),(1040,'操作删除',500,2,'#','','','',1,0,'F','0','0','monitor:operlog:remove','#','小桐','2025-07-29 15:18:25','',NULL,''),(1041,'日志导出',500,3,'#','','','',1,0,'F','0','0','monitor:operlog:export','#','小桐','2025-07-29 15:18:25','',NULL,''),(1042,'登录查询',501,1,'#','','','',1,0,'F','0','0','monitor:logininfor:query','#','小桐','2025-07-29 15:18:25','',NULL,''),(1043,'登录删除',501,2,'#','','','',1,0,'F','0','0','monitor:logininfor:remove','#','小桐','2025-07-29 15:18:25','',NULL,''),(1044,'日志导出',501,3,'#','','','',1,0,'F','0','0','monitor:logininfor:export','#','小桐','2025-07-29 15:18:25','',NULL,''),(1045,'账户解锁',501,4,'#','','','',1,0,'F','0','0','monitor:logininfor:unlock','#','小桐','2025-07-29 15:18:25','',NULL,''),(1046,'在线查询',109,1,'#','','','',1,0,'F','0','0','monitor:online:query','#','小桐','2025-07-29 15:18:25','',NULL,''),(1047,'批量强退',109,2,'#','','','',1,0,'F','0','0','monitor:online:batchLogout','#','小桐','2025-07-29 15:18:25','',NULL,''),(1048,'单条强退',109,3,'#','','','',1,0,'F','0','0','monitor:online:forceLogout','#','小桐','2025-07-29 15:18:25','',NULL,''),(1049,'任务查询',110,1,'#','','','',1,0,'F','0','0','monitor:job:query','#','小桐','2025-07-29 15:18:25','',NULL,''),(1050,'任务新增',110,2,'#','','','',1,0,'F','0','0','monitor:job:add','#','小桐','2025-07-29 15:18:25','',NULL,''),(1051,'任务修改',110,3,'#','','','',1,0,'F','0','0','monitor:job:edit','#','小桐','2025-07-29 15:18:25','',NULL,''),(1052,'任务删除',110,4,'#','','','',1,0,'F','0','0','monitor:job:remove','#','小桐','2025-07-29 15:18:25','',NULL,''),(1053,'状态修改',110,5,'#','','','',1,0,'F','0','0','monitor:job:changeStatus','#','小桐','2025-07-29 15:18:25','',NULL,''),(1054,'任务导出',110,6,'#','','','',1,0,'F','0','0','monitor:job:export','#','小桐','2025-07-29 15:18:25','',NULL,''),(1055,'生成查询',114,1,'#','','','',1,0,'F','0','0','tool:gen:query','#','小桐','2025-07-29 15:18:25','',NULL,''),(1056,'生成修改',114,2,'#','','','',1,0,'F','0','0','tool:gen:edit','#','小桐','2025-07-29 15:18:25','',NULL,''),(1057,'生成删除',114,3,'#','','','',1,0,'F','0','0','tool:gen:remove','#','小桐','2025-07-29 15:18:25','',NULL,''),(1058,'导入代码',114,4,'#','','','',1,0,'F','0','0','tool:gen:import','#','小桐','2025-07-29 15:18:25','',NULL,''),(1059,'预览代码',114,5,'#','','','',1,0,'F','0','0','tool:gen:preview','#','小桐','2025-07-29 15:18:25','',NULL,''),(1060,'生成代码',114,6,'#','','','',1,0,'F','0','0','tool:gen:code','#','小桐','2025-07-29 15:18:25','',NULL,''),(2000,'知识中心',0,1,'kmc',NULL,NULL,NULL,1,0,'M','0','0','','知识中心','小桐','2025-07-29 15:18:25','',NULL,''),(2001,'知识分类',2000,0,'kmcCategory','kmc/kmcCategory/index','',NULL,1,0,'C','0','0','kmc:kmcCategory:kmcCategory:list','#','小桐','2025-07-29 15:18:25','',NULL,''),(2002,'知识分类导出',2001,1,'',NULL,NULL,NULL,1,0,'F','0','0','kmc:kmcCategory:kmcCategory:export','#','小桐','2025-07-29 15:18:25','',NULL,''),(2003,'知识分类导入',2001,2,'',NULL,NULL,NULL,1,0,'F','0','0','kmc:kmcCategory:kmcCategory:import','#','小桐','2025-07-29 15:18:25','',NULL,''),(2004,'知识分类详情',2001,3,'',NULL,NULL,NULL,1,0,'F','0','0','kmc:kmcCategory:kmcCategory:query','#','小桐','2025-07-29 15:18:25','',NULL,''),(2005,'知识分类新增',2001,4,'',NULL,NULL,NULL,1,0,'F','0','0','kmc:kmcCategory:kmcCategory:add','#','小桐','2025-07-29 15:18:25','',NULL,''),(2006,'知识分类修改',2001,5,'',NULL,NULL,NULL,1,0,'F','0','0','kmc:kmcCategory:kmcCategory:edit','#','小桐','2025-07-29 15:18:25','',NULL,''),(2007,'知识分类删除',2001,6,'',NULL,NULL,NULL,1,0,'F','0','0','kmc:kmcCategory:kmcCategory:remove','#','小桐','2025-07-29 15:18:25','',NULL,''),(2008,'文件管理',2000,1,'kmcDocument','kmc/kmcDocument/index','',NULL,1,0,'C','0','0','kmcDocument:kmcDocument:document:list','#','小桐','2025-07-29 15:18:25','',NULL,''),(2009,'知识文件导出',2008,1,'',NULL,NULL,NULL,1,0,'F','0','0','kmcDocument:kmcDocument:document:export','#','小桐','2025-07-29 15:18:25','',NULL,''),(2010,'知识文件导入',2008,2,'',NULL,NULL,NULL,1,0,'F','0','0','kmcDocument:kmcDocument:document:import','#','小桐','2025-07-29 15:18:25','',NULL,''),(2011,'知识文件详情',2008,3,'',NULL,NULL,NULL,1,0,'F','0','0','kmcDocument:kmcDocument:document:query','#','小桐','2025-07-29 15:18:25','',NULL,''),(2012,'知识文件新增',2008,4,'',NULL,NULL,NULL,1,0,'F','0','0','kmcDocument:kmcDocument:document:add','#','小桐','2025-07-29 15:18:25','',NULL,''),(2013,'知识文件修改',2008,5,'',NULL,NULL,NULL,1,0,'F','0','0','kmcDocument:kmcDocument:document:edit','#','小桐','2025-07-29 15:18:25','',NULL,''),(2014,'知识文件删除',2008,6,'',NULL,NULL,NULL,1,0,'F','0','0','kmcDocument:kmcDocument:document:remove','#','小桐','2025-07-29 15:18:25','',NULL,''),(2015,'知识抽取',0,2,'ext',NULL,NULL,NULL,1,0,'M','0','0','','知识抽取','小桐','2025-07-29 15:18:25','',NULL,''),(2016,'概念配置',2015,1,'schema','ext/extSchema/index',NULL,NULL,1,0,'C','0','0','ext:extSchema:schema:list','#','小桐','2025-07-29 15:18:25','',NULL,''),(2017,'概念配置查询',2016,1,'',NULL,NULL,NULL,1,0,'F','0','0','ext:extSchema:schema:query','#','小桐','2025-07-29 15:18:25','',NULL,''),(2018,'概念配置新增',2016,2,'',NULL,NULL,NULL,1,0,'F','0','0','ext:extSchema:schema:add','#','小桐','2025-07-29 15:18:25','',NULL,''),(2019,'概念配置修改',2016,3,'',NULL,NULL,NULL,1,0,'F','0','0','ext:extSchema:schema:edit','#','小桐','2025-07-29 15:18:25','',NULL,''),(2020,'概念配置删除',2016,4,'',NULL,NULL,NULL,1,0,'F','0','0','ext:extSchema:schema:remove','#','小桐','2025-07-29 15:18:25','',NULL,''),(2021,'概念配置导出',2016,5,'',NULL,NULL,NULL,1,0,'F','0','0','ext:extSchema:schema:export','#','小桐','2025-07-29 15:18:25','',NULL,''),(2022,'概念配置导入',2016,6,'',NULL,NULL,NULL,1,0,'F','0','0','ext:extSchema:schema:import','#','小桐','2025-07-29 15:18:25','',NULL,''),(2023,'关系配置',2015,2,'relation','ext/extSchemaRelation/index',NULL,NULL,1,0,'C','0','0','ext:extSchemaRelation:relation:list','#','小桐','2025-07-29 15:18:25','',NULL,''),(2024,'关系配置查询',2023,1,'#','',NULL,NULL,1,0,'F','0','0','ext:extSchemaRelation:relation:query','#','小桐','2025-07-29 15:18:25','',NULL,''),(2025,'关系配置新增',2023,2,'#','',NULL,NULL,1,0,'F','0','0','ext:extSchemaRelation:relation:add','#','小桐','2025-07-29 15:18:25','',NULL,''),(2026,'关系配置修改',2023,3,'#','',NULL,NULL,1,0,'F','0','0','ext:extSchemaRelation:relation:edit','#','小桐','2025-07-29 15:18:25','',NULL,''),(2027,'关系配置删除',2023,4,'#','',NULL,NULL,1,0,'F','0','0','ext:extSchemaRelation:relation:remove','#','小桐','2025-07-29 15:18:25','',NULL,''),(2028,'关系配置导出',2023,5,'#','',NULL,NULL,1,0,'F','0','0','ext:extSchemaRelation:relation:export','#','小桐','2025-07-29 15:18:25','',NULL,''),(2029,'关系配置导入',2023,6,'#','',NULL,NULL,1,0,'F','0','0','ext:extSchemaRelation:relation:import','#','小桐','2025-07-29 15:18:25','',NULL,''),(2030,'非结构化抽取',2015,3,'unstructTask','ext/extUnstructTask/index',NULL,NULL,1,0,'C','0','0','ext:extUnstructTask:unstructtask:list','#','小桐','2025-07-29 15:18:25','',NULL,''),(2031,'非结构化抽取任务查询',2030,1,'#','',NULL,NULL,1,0,'F','0','0','ext:extUnstructTask:unstructtask:query','#','小桐','2025-07-29 15:18:25','',NULL,''),(2032,'非结构化抽取任务新增',2030,2,'#','',NULL,NULL,1,0,'F','0','0','ext:extUnstructTask:unstructtask:add','#','小桐','2025-07-29 15:18:25','',NULL,''),(2033,'非结构化抽取任务修改',2030,3,'#','',NULL,NULL,1,0,'F','0','0','ext:extUnstructTask:unstructtask:edit','#','小桐','2025-07-29 15:18:25','',NULL,''),(2034,'非结构化抽取任务删除',2030,4,'#','',NULL,NULL,1,0,'F','0','0','ext:extUnstructTask:unstructtask:remove','#','小桐','2025-07-29 15:18:25','',NULL,''),(2035,'非结构化抽取任务导出',2030,5,'#','',NULL,NULL,1,0,'F','0','0','ext:extUnstructTask:unstructtask:export','#','小桐','2025-07-29 15:18:25','',NULL,''),(2036,'非结构化抽取任务导入',2030,6,'#','',NULL,NULL,1,0,'F','0','0','ext:extUnstructTask:unstructtask:import','#','小桐','2025-07-29 15:18:25','',NULL,''),(2037,'结构化抽取',2015,4,'extStructTask','ext/extStructTask/index',NULL,NULL,1,0,'C','0','0','ext:extStructTask:struct:list','#','小桐','2025-07-29 15:18:25','',NULL,''),(2038,'结构化抽取任务查询',2037,1,'#','',NULL,NULL,1,0,'F','0','0','ext:extStructTask:struct:query','#','小桐','2025-07-29 15:18:25','',NULL,''),(2039,'结构化抽取任务新增',2037,2,'#','',NULL,NULL,1,0,'F','0','0','ext:extStructTask:struct:add','#','小桐','2025-07-29 15:18:25','',NULL,''),(2040,'结构化抽取任务修改',2037,3,'#','',NULL,NULL,1,0,'F','0','0','ext:extStructTask:struct:edit','#','小桐','2025-07-29 15:18:25','',NULL,''),(2041,'结构化抽取任务删除',2037,4,'#','',NULL,NULL,1,0,'F','0','0','ext:extStructTask:struct:remove','#','小桐','2025-07-29 15:18:25','',NULL,''),(2042,'结构化抽取任务导出',2037,5,'#','',NULL,NULL,1,0,'F','0','0','ext:extStructTask:struct:export','#','小桐','2025-07-29 15:18:25','',NULL,''),(2043,'结构化抽取任务导入',2037,6,'#','',NULL,NULL,1,0,'F','0','0','ext:extStructTask:struct:import','#','小桐','2025-07-29 15:18:25','',NULL,''),(2044,'知识应用',0,3,'app',NULL,NULL,NULL,1,0,'M','0','0','','知识应用','小桐','2025-07-29 15:18:25','',NULL,''),(2045,'图谱探索',2044,0,'graphExploration','app/graphExploration/index',NULL,NULL,1,0,'C','0','0',NULL,'#','小桐','2025-07-29 15:18:25','',NULL,''),(2046,'数据管理',0,4,'dm',NULL,NULL,NULL,1,0,'M','0','0','','数据管理','小桐','2025-07-29 15:18:25','',NULL,''),(2047,'数据源',2046,4,'dmDatasource','dm/dmDatasource/index',NULL,NULL,1,0,'C','0','0','dm:datasource:datasource:list','#','小桐','2025-07-29 15:18:25','',NULL,''),(2048,'数据源查询',2047,1,'',NULL,NULL,NULL,1,0,'F','0','0','dm:datasource:datasource:query','#','小桐','2025-07-29 15:18:25','',NULL,''),(2049,'数据源新增',2047,2,'',NULL,NULL,NULL,1,0,'F','0','0','dm:datasource:datasource:add','#','小桐','2025-07-29 15:18:25','',NULL,''),(2050,'数据源修改',2047,3,'',NULL,NULL,NULL,1,0,'F','0','0','dm:datasource:datasource:edit','#','小桐','2025-07-29 15:18:25','',NULL,''),(2051,'数据源删除',2047,4,'',NULL,NULL,NULL,1,0,'F','0','0','dm:datasource:datasource:remove','#','小桐','2025-07-29 15:18:25','',NULL,''),(2052,'数据源导出',2047,5,'',NULL,NULL,NULL,1,0,'F','0','0','dm:datasource:datasource:export','#','小桐','2025-07-29 15:18:25','',NULL,''),(2053,'数据源导入',2047,6,'',NULL,NULL,NULL,1,0,'F','0','0','dm:datasource:datasource:import','#','小桐','2025-07-29 15:18:25','',NULL,''),(2100,'实体池管理',2015,5,'entityPool','ext/extEntityPool/index',NULL,NULL,1,0,'C','0','0','ext:extEntityPool:list','#','小桐','2025-07-29 15:18:35','',NULL,''),(2101,'实体池查询',2100,1,'#','',NULL,NULL,1,0,'F','0','0','ext:extEntityPool:query','#','小桐','2025-07-29 15:18:35','',NULL,''),(2102,'实体池新增',2100,2,'#','',NULL,NULL,1,0,'F','0','0','ext:extEntityPool:add','#','小桐','2025-07-29 15:18:35','',NULL,''),(2103,'实体池修改',2100,3,'#','',NULL,NULL,1,0,'F','0','0','ext:extEntityPool:edit','#','小桐','2025-07-29 15:18:35','',NULL,''),(2104,'实体池删除',2100,4,'#','',NULL,NULL,1,0,'F','0','0','ext:extEntityPool:remove','#','小桐','2025-07-29 15:18:35','',NULL,''),(2105,'实体池导出',2100,5,'#','',NULL,NULL,1,0,'F','0','0','ext:extEntityPool:export','#','小桐','2025-07-29 15:18:35','',NULL,''),(2106,'实体池处理',2100,6,'#','',NULL,NULL,1,0,'F','0','0','ext:extEntityPool:process','#','小桐','2025-07-29 15:18:35','',NULL,''),(2110,'关系池管理',2015,6,'relationshipPool','ext/extRelationshipPool/index',NULL,NULL,1,0,'C','0','0','ext:extRelationshipPool:list','#','小桐','2025-07-29 15:18:35','',NULL,''),(2111,'关系池查询',2110,1,'#','',NULL,NULL,1,0,'F','0','0','ext:extRelationshipPool:query','#','小桐','2025-07-29 15:18:35','',NULL,''),(2112,'关系池新增',2110,2,'#','',NULL,NULL,1,0,'F','0','0','ext:extRelationshipPool:add','#','小桐','2025-07-29 15:18:35','',NULL,''),(2113,'关系池修改',2110,3,'#','',NULL,NULL,1,0,'F','0','0','ext:extRelationshipPool:edit','#','小桐','2025-07-29 15:18:35','',NULL,''),(2114,'关系池删除',2110,4,'#','',NULL,NULL,1,0,'F','0','0','ext:extRelationshipPool:remove','#','小桐','2025-07-29 15:18:35','',NULL,''),(2115,'关系池导出',2110,5,'#','',NULL,NULL,1,0,'F','0','0','ext:extRelationshipPool:export','#','小桐','2025-07-29 15:18:35','',NULL,''),(2116,'关系池处理',2110,6,'#','',NULL,NULL,1,0,'F','0','0','ext:extRelationshipPool:process','#','小桐','2025-07-29 15:18:35','',NULL,'');
/*!40000 ALTER TABLE `system_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_notice`
--

DROP TABLE IF EXISTS `system_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) NOT NULL COMMENT '公告标题',
  `notice_type` char(1) NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_notice`
--

LOCK TABLES `system_notice` WRITE;
/*!40000 ALTER TABLE `system_notice` DISABLE KEYS */;
INSERT INTO `system_notice` VALUES (1,'qKnow千知平台正式开源！','2',_binary '知识中心、知识抽取、知识图谱核心三大功能已发布','0','小桐','2025-05-28 18:00:00','',NULL,NULL),(2,'qKnow期待与您携手共建知识体系！','1',_binary '期待您的加入','0','小桐','2025-07-29 15:18:26','2025-05-28 18:00:00',NULL,NULL);
/*!40000 ALTER TABLE `system_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_oper_log`
--

DROP TABLE IF EXISTS `system_oper_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_oper_log` (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) DEFAULT '' COMMENT '模块标题',
  `business_type` int DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) DEFAULT '' COMMENT '请求方式',
  `operator_type` int DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) DEFAULT '' COMMENT '返回参数',
  `status` int DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`),
  KEY `idx_system_oper_log_bt` (`business_type`),
  KEY `idx_system_oper_log_s` (`status`),
  KEY `idx_system_oper_log_ot` (`oper_time`)
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_oper_log`
--

LOCK TABLES `system_oper_log` WRITE;
/*!40000 ALTER TABLE `system_oper_log` DISABLE KEYS */;
INSERT INTO `system_oper_log` VALUES (1,'关系配置',3,'tech.qiantong.qknow.module.ext.controller.admin.extSchemaRelation.ExtSchemaRelationController.remove()','DELETE',1,'qKnow','研发部门','/ext/relation/1','127.0.0.1','内网IP','{}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:23:55',79),(2,'关系配置',3,'tech.qiantong.qknow.module.ext.controller.admin.extSchemaRelation.ExtSchemaRelationController.remove()','DELETE',1,'qKnow','研发部门','/ext/relation/2','127.0.0.1','内网IP','{}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:23:57',15),(3,'关系配置',3,'tech.qiantong.qknow.module.ext.controller.admin.extSchemaRelation.ExtSchemaRelationController.remove()','DELETE',1,'qKnow','研发部门','/ext/relation/3','127.0.0.1','内网IP','{}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:23:58',34),(4,'关系配置',3,'tech.qiantong.qknow.module.ext.controller.admin.extSchemaRelation.ExtSchemaRelationController.remove()','DELETE',1,'qKnow','研发部门','/ext/relation/4','127.0.0.1','内网IP','{}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:24:01',15),(5,'概念配置',3,'tech.qiantong.qknow.module.ext.controller.admin.extSchema.ExtSchemaController.remove()','DELETE',1,'qKnow','研发部门','/ext/schema/1','127.0.0.1','内网IP','{}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:24:05',20),(6,'概念配置',3,'tech.qiantong.qknow.module.ext.controller.admin.extSchema.ExtSchemaController.remove()','DELETE',1,'qKnow','研发部门','/ext/schema/2','127.0.0.1','内网IP','{}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:24:06',16),(7,'概念配置',3,'tech.qiantong.qknow.module.ext.controller.admin.extSchema.ExtSchemaController.remove()','DELETE',1,'qKnow','研发部门','/ext/schema/3','127.0.0.1','内网IP','{}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:24:06',16),(8,'概念配置',3,'tech.qiantong.qknow.module.ext.controller.admin.extSchema.ExtSchemaController.remove()','DELETE',1,'qKnow','研发部门','/ext/schema/4','127.0.0.1','内网IP','{}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:24:07',12),(9,'概念配置',3,'tech.qiantong.qknow.module.ext.controller.admin.extSchema.ExtSchemaController.remove()','DELETE',1,'qKnow','研发部门','/ext/schema/5','127.0.0.1','内网IP','{}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:24:07',17),(10,'概念配置',3,'tech.qiantong.qknow.module.ext.controller.admin.extSchema.ExtSchemaController.remove()','DELETE',1,'qKnow','研发部门','/ext/schema/7','127.0.0.1','内网IP','{}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:24:07',15),(11,'概念属性',3,'tech.qiantong.qknow.module.ext.controller.admin.extSchemaAttribute.ExtSchemaAttributeController.remove()','DELETE',1,'qKnow','研发部门','/ext/attribute/1,2,3','127.0.0.1','内网IP','{}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:24:07',16),(12,'概念配置',3,'tech.qiantong.qknow.module.ext.controller.admin.extSchema.ExtSchemaController.remove()','DELETE',1,'qKnow','研发部门','/ext/schema/8','127.0.0.1','内网IP','{}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:24:08',12),(13,'概念属性',3,'tech.qiantong.qknow.module.ext.controller.admin.extSchemaAttribute.ExtSchemaAttributeController.remove()','DELETE',1,'qKnow','研发部门','/ext/attribute/4,5','127.0.0.1','内网IP','{}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:24:08',18),(14,'概念配置',1,'tech.qiantong.qknow.module.ext.controller.admin.extSchema.ExtSchemaController.add()','POST',1,'qKnow','研发部门','/ext/schema','127.0.0.1','内网IP','{\"color\":\"#D7BDE2\",\"createBy\":\"小桐\",\"createTime\":\"2025-07-30 22:33:42\",\"creatorId\":1,\"name\":\"人物\",\"params\":{},\"workspaceId\":1001}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:33:42',47),(15,'概念配置',1,'tech.qiantong.qknow.module.ext.controller.admin.extSchema.ExtSchemaController.add()','POST',1,'qKnow','研发部门','/ext/schema','127.0.0.1','内网IP','{\"color\":\"#A9CCE3\",\"createBy\":\"小桐\",\"createTime\":\"2025-07-30 22:33:53\",\"creatorId\":1,\"name\":\"学校\",\"params\":{},\"workspaceId\":1001}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:33:53',19),(16,'概念配置',1,'tech.qiantong.qknow.module.ext.controller.admin.extSchema.ExtSchemaController.add()','POST',1,'qKnow','研发部门','/ext/schema','127.0.0.1','内网IP','{\"color\":\"#FCF3CF\",\"createBy\":\"小桐\",\"createTime\":\"2025-07-30 22:34:01\",\"creatorId\":1,\"name\":\"公司\",\"params\":{},\"workspaceId\":1001}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:34:01',18),(17,'概念配置',1,'tech.qiantong.qknow.module.ext.controller.admin.extSchema.ExtSchemaController.add()','POST',1,'qKnow','研发部门','/ext/schema','127.0.0.1','内网IP','{\"color\":\"#FDEBD0\",\"createBy\":\"小桐\",\"createTime\":\"2025-07-30 22:34:40\",\"creatorId\":1,\"name\":\"地点\",\"params\":{},\"workspaceId\":1001}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:34:40',18),(18,'概念配置',3,'tech.qiantong.qknow.module.ext.controller.admin.extSchema.ExtSchemaController.remove()','DELETE',1,'qKnow','研发部门','/ext/schema/12','127.0.0.1','内网IP','{}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:34:46',12),(19,'概念配置',1,'tech.qiantong.qknow.module.ext.controller.admin.extSchema.ExtSchemaController.add()','POST',1,'qKnow','研发部门','/ext/schema','127.0.0.1','内网IP','{\"color\":\"#A9CCE3\",\"createBy\":\"小桐\",\"createTime\":\"2025-07-30 22:36:26\",\"creatorId\":1,\"name\":\"地点\",\"params\":{},\"workspaceId\":1001}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:36:26',22),(20,'概念配置',1,'tech.qiantong.qknow.module.ext.controller.admin.extSchema.ExtSchemaController.add()','POST',1,'qKnow','研发部门','/ext/schema','127.0.0.1','内网IP','{\"color\":\"#A3E4D7\",\"createBy\":\"小桐\",\"createTime\":\"2025-07-30 22:36:48\",\"creatorId\":1,\"name\":\"技术\",\"params\":{},\"workspaceId\":1001}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:36:48',21),(21,'关系配置',1,'tech.qiantong.qknow.module.ext.controller.admin.extSchemaRelation.ExtSchemaRelationController.add()','POST',1,'qKnow','研发部门','/ext/relation','127.0.0.1','内网IP','{\"createBy\":\"小桐\",\"createTime\":\"2025-07-30 22:39:40\",\"creatorId\":1,\"endSchemaId\":10,\"inverseFlag\":0,\"params\":{},\"relation\":\"毕业院校\",\"startSchemaId\":9,\"workspaceId\":1001}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:39:40',17),(22,'关系配置',1,'tech.qiantong.qknow.module.ext.controller.admin.extSchemaRelation.ExtSchemaRelationController.add()','POST',1,'qKnow','研发部门','/ext/relation','127.0.0.1','内网IP','{\"createBy\":\"小桐\",\"createTime\":\"2025-07-30 22:39:55\",\"creatorId\":1,\"endSchemaId\":11,\"inverseFlag\":0,\"params\":{},\"relation\":\"工作单位\",\"startSchemaId\":9,\"workspaceId\":1001}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:39:55',13),(23,'关系配置',1,'tech.qiantong.qknow.module.ext.controller.admin.extSchemaRelation.ExtSchemaRelationController.add()','POST',1,'qKnow','研发部门','/ext/relation','127.0.0.1','内网IP','{\"createBy\":\"小桐\",\"createTime\":\"2025-07-30 22:40:12\",\"creatorId\":1,\"endSchemaId\":13,\"inverseFlag\":0,\"params\":{},\"relation\":\"所在地\",\"startSchemaId\":10,\"workspaceId\":1001}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:40:12',12),(24,'关系配置',1,'tech.qiantong.qknow.module.ext.controller.admin.extSchemaRelation.ExtSchemaRelationController.add()','POST',1,'qKnow','研发部门','/ext/relation','127.0.0.1','内网IP','{\"createBy\":\"小桐\",\"createTime\":\"2025-07-30 22:40:21\",\"creatorId\":1,\"endSchemaId\":13,\"inverseFlag\":0,\"params\":{},\"relation\":\"所在地\",\"startSchemaId\":11,\"workspaceId\":1001}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:40:21',13),(25,'关系配置',1,'tech.qiantong.qknow.module.ext.controller.admin.extSchemaRelation.ExtSchemaRelationController.add()','POST',1,'qKnow','研发部门','/ext/relation','127.0.0.1','内网IP','{\"createBy\":\"小桐\",\"createTime\":\"2025-07-30 22:40:57\",\"creatorId\":1,\"endSchemaId\":14,\"inverseFlag\":0,\"params\":{},\"relation\":\"掌握技术\",\"startSchemaId\":10,\"workspaceId\":1001}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:40:58',12),(26,'关系配置',1,'tech.qiantong.qknow.module.ext.controller.admin.extSchemaRelation.ExtSchemaRelationController.add()','POST',1,'qKnow','研发部门','/ext/relation','127.0.0.1','内网IP','{\"createBy\":\"小桐\",\"createTime\":\"2025-07-30 22:41:07\",\"creatorId\":1,\"endSchemaId\":14,\"inverseFlag\":0,\"params\":{},\"relation\":\"掌握技术\",\"startSchemaId\":9,\"workspaceId\":1001}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:41:07',12),(27,'关系配置',1,'tech.qiantong.qknow.module.ext.controller.admin.extSchemaRelation.ExtSchemaRelationController.add()','POST',1,'qKnow','研发部门','/ext/relation','127.0.0.1','内网IP','{\"createBy\":\"小桐\",\"createTime\":\"2025-07-30 22:41:17\",\"creatorId\":1,\"endSchemaId\":14,\"inverseFlag\":0,\"params\":{},\"relation\":\"掌握技术\",\"startSchemaId\":11,\"workspaceId\":1001}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:41:17',13),(28,'知识分类',1,'tech.qiantong.qknow.module.kmc.controller.admin.kmcCategory.KmcCategoryController.add()','POST',1,'qKnow','研发部门','/kmc/kmcCategory','127.0.0.1','内网IP','{\"ancestors\":\"0,2\",\"createBy\":\"小桐\",\"createTime\":\"2025-07-30 22:42:25\",\"creatorId\":1,\"name\":\"人物故事\",\"orderNum\":0,\"params\":{},\"parentId\":2,\"workspaceId\":1001}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:42:25',22),(29,'知识文件',1,'tech.qiantong.qknow.module.kmc.controller.admin.kmcDocument.KmcDocumentController.add()','POST',1,'qKnow','研发部门','/kmcDocument/document','127.0.0.1','内网IP','{\"categoryId\":18,\"categoryName\":\"人物故事\",\"createBy\":\"小桐\",\"createTime\":\"2025-07-30 22:43:35\",\"creatorId\":1,\"name\":\"测试文档1.docx\",\"params\":{},\"path\":\"/2025/07/30/688a2f8cf3fc9675162f0db5.docx\",\"workspaceId\":1001}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:43:35',18),(30,'知识文件',1,'tech.qiantong.qknow.module.kmc.controller.admin.kmcDocument.KmcDocumentController.add()','POST',1,'qKnow','研发部门','/kmcDocument/document','127.0.0.1','内网IP','{\"categoryId\":18,\"categoryName\":\"人物故事\",\"createBy\":\"小桐\",\"createTime\":\"2025-07-30 22:43:41\",\"creatorId\":1,\"name\":\"测试文档2.docx\",\"params\":{},\"path\":\"/2025/07/30/688a2f9cf3fc9675162f0db6.docx\",\"workspaceId\":1001}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:43:41',13),(31,'非结构化抽取任务',1,'tech.qiantong.qknow.module.ext.controller.admin.extUnstructTask.ExtUnstructTaskController.add()','POST',1,'qKnow','研发部门','/ext/unstructTask','127.0.0.1','内网IP','{\"createBy\":\"小桐\",\"createTime\":\"2025-07-30 22:50:00\",\"creatorId\":1,\"id\":3,\"name\":\"1\",\"params\":{\"docIds\":\"3\",\"relationIds\":\"5,6,7,8,9,10,11\"},\"publishStatus\":0,\"status\":0,\"updateTime\":\"2025-07-30 22:50:00\",\"workspaceId\":1001}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 22:50:00',135),(32,'定时任务',2,'tech.qiantong.qknow.quartz.controller.SysJobController.changeStatus()','PUT',1,'qKnow','研发部门','/monitor/job/changeStatus','127.0.0.1','内网IP','{\"jobId\":101,\"misfirePolicy\":\"0\",\"params\":{},\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-07-30 22:55:51',30),(33,'非结构化抽取任务',3,'tech.qiantong.qknow.module.ext.controller.admin.extUnstructTask.ExtUnstructTaskController.remove()','DELETE',1,'qKnow','研发部门','/ext/unstructTask/1','127.0.0.1','内网IP','{}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-30 23:31:49',744),(34,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.processEntity()','POST',1,'qKnow','研发部门','/ext/entityPool/process','127.0.0.1','内网IP','{\"remark\":\"test\",\"id\":\"3\",\"status\":\"1\"}',NULL,1,'实体确认后存入Neo4j失败: null','2025-07-31 00:04:07',37),(35,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.processEntity()','POST',1,'qKnow','研发部门','/ext/entityPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"4\",\"status\":\"1\"}',NULL,1,'实体确认后存入Neo4j失败: 实体存入Neo4j失败: null','2025-07-31 00:42:05',75),(36,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.processEntity()','POST',1,'qKnow','研发部门','/ext/entityPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"2\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"处理成功\",\"code\":200},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 00:45:24',1873),(37,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.processEntity()','POST',1,'qKnow','研发部门','/ext/entityPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"3\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"处理成功\",\"code\":200},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 08:52:31',158),(38,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"1\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"关系确认失败：源实体或目标实体未确认\",\"code\":500},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 08:53:19',29),(39,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"确认张三就职于腾讯的关系\",\"id\":\"1\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"处理成功\",\"code\":200},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 09:10:58',2266),(40,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.processEntity()','POST',1,'qKnow','研发部门','/ext/entityPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"23\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"处理成功\",\"code\":200},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 09:48:05',701),(41,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"26\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"关系确认失败：源实体或目标实体未确认\",\"code\":500},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 09:48:20',34),(42,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"27\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"关系确认失败：源实体或目标实体未确认\",\"code\":500},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 09:49:01',24),(43,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"测试关系确认失败的情况\",\"id\":\"26\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"关系确认失败：源实体或目标实体未确认\",\"code\":500},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 09:56:44',336),(44,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"测试修复后的关系确认失败情况\",\"id\":\"26\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"关系确认失败：源实体或目标实体未确认\",\"code\":500},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 09:57:54',370),(45,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"26\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"关系确认失败：源实体或目标实体未确认\",\"code\":500},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 10:04:50',26),(46,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"26\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"关系确认失败：源实体或目标实体未确认\",\"code\":500},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 10:05:12',23),(47,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.processEntity()','POST',1,'qKnow','研发部门','/ext/entityPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"26\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"处理成功\",\"code\":200},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 10:05:59',544),(48,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.processEntity()','POST',1,'qKnow','研发部门','/ext/entityPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"23\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"处理成功\",\"code\":200},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 10:09:26',104),(49,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"26\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"关系确认失败：源实体或目标实体未确认\",\"code\":500},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 10:09:39',15),(50,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.processEntity()','POST',1,'qKnow','研发部门','/ext/entityPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"24\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"处理成功\",\"code\":200},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 10:10:03',70),(51,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"26\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"关系确认失败：源实体或目标实体未确认\",\"code\":500},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 10:10:14',12),(52,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.processEntity()','POST',1,'qKnow','研发部门','/ext/entityPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"26\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"处理成功\",\"code\":200},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 10:10:25',59),(53,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.processEntity()','POST',1,'qKnow','研发部门','/ext/entityPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"23\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"处理成功\",\"code\":200},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 10:21:08',331),(54,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"26\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"关系确认失败：源实体或目标实体未确认\",\"code\":500},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 10:21:16',31),(55,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.processEntity()','POST',1,'qKnow','研发部门','/ext/entityPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"26\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"处理成功\",\"code\":200},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 10:21:30',75),(56,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"26\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"处理成功\",\"code\":200},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 10:21:41',89),(57,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"27\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"关系确认失败：源实体或目标实体未确认\",\"code\":500},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 10:22:02',19),(58,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"28\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"关系确认失败：源实体或目标实体未确认\",\"code\":500},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 10:26:49',47),(59,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"27\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"关系确认失败：源实体或目标实体未确认\",\"code\":500},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 10:29:35',27),(60,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"27\",\"status\":\"1\"}','{\"code\":500,\"msg\":\"关系确认失败：源实体或目标实体未确认\"}',0,NULL,'2025-07-31 10:32:57',43),(61,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"27\",\"status\":\"1\"}','{\"code\":500,\"msg\":\"关系确认失败：源实体或目标实体未确认\"}',0,NULL,'2025-07-31 10:32:58',16),(62,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"27\",\"status\":\"1\"}','{\"code\":500,\"msg\":\"关系确认失败：源实体或目标实体未确认\"}',0,NULL,'2025-07-31 10:33:04',13),(63,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.batchProcessEntities()','POST',1,'qKnow','研发部门','/ext/entityPool/batch-process','127.0.0.1','内网IP','{\"idList\":[23,24,25,26,27,28,29,30,31,32],\"status\":1,\"remark\":\"\"}',NULL,1,'java.lang.Integer cannot be cast to java.lang.Long','2025-07-31 10:57:31',5),(64,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.batchProcessEntities()','POST',1,'qKnow','研发部门','/ext/entityPool/batch-process','127.0.0.1','内网IP','{\"idList\":[23,24,25,26,27,28,29,30,31,32],\"status\":1,\"remark\":\"\"}','{\"code\":200,\"data\":{\"successIds\":[23,24,25,26,27,28,29,30,31,32],\"failIds\":[],\"errorMessages\":[],\"failCount\":0,\"successCount\":10,\"totalCount\":10},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 11:02:50',1716),(65,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.batchProcessEntities()','POST',1,'qKnow','研发部门','/ext/entityPool/batch-process','127.0.0.1','内网IP','{\"idList\":[23],\"status\":1,\"remark\":\"\"}','{\"code\":200,\"data\":{\"successIds\":[23],\"failIds\":[],\"errorMessages\":[],\"failCount\":0,\"successCount\":1,\"totalCount\":1},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 11:03:48',52),(66,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.batchProcessEntities()','POST',1,'qKnow','研发部门','/ext/entityPool/batch-process','127.0.0.1','内网IP','{\"idList\":[23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45],\"status\":1,\"remark\":\"\"}','{\"code\":200,\"data\":{\"successIds\":[23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45],\"failIds\":[],\"errorMessages\":[],\"failCount\":0,\"successCount\":23,\"totalCount\":23},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 11:05:17',854),(67,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.batchProcessEntities()','POST',1,'qKnow','研发部门','/ext/entityPool/batch-process','127.0.0.1','内网IP','{\"idList\":[23],\"status\":1,\"remark\":\"\"}','{\"code\":200,\"data\":{\"successIds\":[23],\"failIds\":[],\"errorMessages\":[],\"failCount\":0,\"successCount\":1,\"totalCount\":1},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 11:34:52',357),(68,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.batchProcessEntities()','POST',1,'qKnow','研发部门','/ext/entityPool/batch-process','127.0.0.1','内网IP','{\"idList\":[23,24,25,26,27,28,29,30,31,32],\"status\":1,\"remark\":\"\"}','{\"code\":200,\"data\":{\"successIds\":[23,24,25,26,27,28,29,30,31,32],\"failIds\":[],\"errorMessages\":[],\"failCount\":0,\"successCount\":10,\"totalCount\":10},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 13:43:31',770),(69,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.processEntity()','POST',1,'qKnow','研发部门','/ext/entityPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"40\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"处理成功\",\"code\":200},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 13:44:14',91),(70,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.batchProcessEntities()','POST',1,'qKnow','研发部门','/ext/entityPool/batch-process','127.0.0.1','内网IP','{\"idList\":[23,24,25,26,27,28,29,30,31,32],\"status\":1,\"remark\":\"\"}','{\"code\":200,\"data\":{\"successIds\":[23,24,25,26,27,28,29,30,31,32],\"failIds\":[],\"errorMessages\":[],\"failCount\":0,\"successCount\":10,\"totalCount\":10},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 14:02:04',671),(71,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.batchProcessEntities()','POST',1,'qKnow','研发部门','/ext/entityPool/batch-process','127.0.0.1','内网IP','{\"idList\":[23,24,25,26,27,28,29,30,31,32],\"status\":1,\"remark\":\"\"}','{\"code\":200,\"data\":{\"successIds\":[23,24,25,26,27,28,29,30,31,32],\"failIds\":[],\"errorMessages\":[],\"failCount\":0,\"successCount\":10,\"totalCount\":10},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 14:13:49',1137),(72,'实体池',1,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.createExtEntityPool()','POST',1,'qKnow','研发部门','/ext/entityPool/create','127.0.0.1','内网IP','{\"aliases\":\"合工大,肥工\",\"attributes\":\"{\\\"杰出校友\\\":[\\\"郭灵杰\\\",\\\"阮垚\\\"],\\\"合作院校\\\":[\\\"苏研院\\\",\\\"清华大学\\\"]}\",\"createBy\":\"小桐\",\"createTime\":\"2025-07-31 15:32:33.438\",\"creatorId\":1,\"definition\":\"是中华人民共和国教育部直属全国重点大学，教育部、工信部和安徽省政府共建高校，教育部与国防科工局共建高校。是“双一流”建设高校、国家“211工程”重点建设高校、国家“985工程”优势学科创新平台建设高校。入选“2011计划”、“111计划”、卓越工程师教育培养计划、国家大学生创新性实验计划、国家级大学生创新创业训练计划、全国高校实践育人创新创业基地、全国首批深化创新创业教育改革示范高校、全国创新创业典型经验高校。\",\"docId\":3,\"entityId\":\"organization_009_1753926297509\",\"entityName\":\"合肥工业大学\",\"entityType\":\"组织\",\"paragraphIndex\":1,\"status\":0,\"taskId\":3,\"workspaceId\":1001}','{\"code\":200,\"data\":46,\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 15:32:33',94),(73,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.processEntity()','POST',1,'qKnow','研发部门','/ext/entityPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"46\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"处理成功\",\"code\":200},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 15:37:41',1212),(74,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.batchProcessEntities()','POST',1,'qKnow','研发部门','/ext/entityPool/batch-process','127.0.0.1','内网IP','{\"idList\":[23,24,25,26,28,29,30,31,32],\"status\":1,\"remark\":\"\"}','{\"code\":200,\"data\":{\"successIds\":[23,24,25,26,28,29,30,31,32],\"failIds\":[],\"errorMessages\":[],\"failCount\":0,\"successCount\":9,\"totalCount\":9},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 15:38:53',600),(75,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.batchProcessEntities()','POST',1,'qKnow','研发部门','/ext/entityPool/batch-process','127.0.0.1','内网IP','{\"idList\":[23,24,25,26,27,28,29,30,31,32],\"status\":1,\"remark\":\"\"}','{\"code\":200,\"data\":{\"successIds\":[23,24,25,26,27,28,29,30,31,32],\"failIds\":[],\"errorMessages\":[],\"failCount\":0,\"successCount\":10,\"totalCount\":10},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 16:23:05',1641),(76,'实体池',1,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.createExtEntityPool()','POST',1,'qKnow','研发部门','/ext/entityPool/create','127.0.0.1','内网IP','{\"aliases\":\"Beijing University\",\"attributes\":\"{\\\"合作企业\\\":[\\\"苏州空天信息研究院\\\"]}\",\"createBy\":\"小桐\",\"createTime\":\"2025-07-31 16:30:09.232\",\"creatorId\":1,\"definition\":\"中国顶尖学府\",\"docId\":3,\"entityId\":\"organization_003_1753926297509\",\"entityName\":\"北大\",\"entityType\":\"组织\",\"paragraphIndex\":1,\"status\":0,\"taskId\":3,\"workspaceId\":1001}','{\"code\":200,\"data\":47,\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 16:30:09',77),(77,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.updateExtEntityPool()','PUT',1,'qKnow','研发部门','/ext/entityPool/update','127.0.0.1','内网IP','{\"aliases\":\"Beijing University\",\"attributes\":\"{\\\"合作企业\\\":[\\\"苏州空天信息研究院\\\"]}\",\"createTime\":\"2025-07-31 16:30:09\",\"definition\":\"中国顶尖学府\",\"docId\":3,\"entityId\":\"organization_003_1753926297509\",\"entityName\":\"北大\",\"entityType\":\"组织\",\"id\":47,\"paragraphIndex\":1,\"status\":0,\"taskId\":3,\"updateBy\":\"小桐\",\"updateTime\":\"2025-07-31 16:33:21.841\",\"updaterId\":1,\"workspaceId\":1001}','{\"code\":200,\"data\":1,\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 16:33:21',42),(78,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.mergeEntityInfo()','POST',1,'qKnow','研发部门','/ext/entityPool/merge-entity','127.0.0.1','内网IP','{\"entityPoolId\":\"47\",\"remark\":\"\",\"candidateId\":\"organization_003_1753926297509\"}','{\"code\":500,\"msg\":\"候选实体不存在\"}',0,NULL,'2025-07-31 22:36:55',978),(79,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.mergeEntityInfo()','POST',1,'qKnow','研发部门','/ext/entityPool/merge-entity','127.0.0.1','内网IP','{\"entityPoolId\":\"47\",\"remark\":\"\",\"candidateId\":\"organization_003_1753926297509\"}','{\"code\":500,\"msg\":\"候选实体不存在\"}',0,NULL,'2025-07-31 22:51:58',1290),(80,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.mergeEntityInfo()','POST',1,'qKnow','研发部门','/ext/entityPool/merge-entity','127.0.0.1','内网IP','{\"entityPoolId\":\"47\",\"remark\":\"\",\"candidateId\":\"organization_003_1753926297509\"}','{\"code\":200,\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 22:57:20',925),(81,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.processEntity()','POST',1,'qKnow','研发部门','/ext/entityPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"28\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"处理成功\",\"code\":200},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 23:33:35',305),(82,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.processEntity()','POST',1,'qKnow','研发部门','/ext/entityPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"23\",\"status\":\"1\"}','{\"code\":200,\"data\":{\"msg\":\"处理成功\",\"code\":200},\"msg\":\"操作成功\"}',0,NULL,'2025-07-31 23:33:59',88),(83,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.batchProcessEntities()','POST',1,'qKnow','研发部门','/ext/entityPool/batch-process','127.0.0.1','内网IP','{\"idList\":[23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46],\"status\":1,\"remark\":\"\"}','{\"code\":200,\"data\":{\"successIds\":[23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46],\"failIds\":[],\"errorMessages\":[],\"failCount\":0,\"successCount\":24,\"totalCount\":24},\"msg\":\"操作成功\"}',0,NULL,'2025-08-01 10:03:05',3091),(84,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"26\",\"status\":\"1\"}',NULL,1,'关系确认后存入Neo4j失败: 关系存入Neo4j失败: Node(9) already exists with label `Entity` and property `id` = \'person_001_1753926297509\'; Error code \'Neo.ClientError.Schema.ConstraintValidationFailed\'; nested exception is org.neo4j.driver.exceptions.ClientException: Node(9) already exists with label `Entity` and property `id` = \'person_001_1753926297509\'','2025-08-01 10:05:13',20),(85,'关系池',2,'tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.ExtRelationshipPoolController.processRelationship()','POST',1,'qKnow','研发部门','/ext/relationshipPool/process','127.0.0.1','内网IP','{\"remark\":\"\",\"id\":\"26\",\"status\":\"1\"}',NULL,1,'关系确认后存入Neo4j失败: 关系存入Neo4j失败: Node(9) already exists with label `Entity` and property `id` = \'person_001_1753926297509\'; Error code \'Neo.ClientError.Schema.ConstraintValidationFailed\'; nested exception is org.neo4j.driver.exceptions.ClientException: Node(9) already exists with label `Entity` and property `id` = \'person_001_1753926297509\'','2025-08-01 10:07:06',19),(86,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.batchProcessEntities()','POST',1,'qKnow','研发部门','/ext/entityPool/batch-process','127.0.0.1','内网IP','{\"idList\":[23],\"status\":1,\"remark\":\"\"}','{\"code\":200,\"data\":{\"successIds\":[23],\"failIds\":[],\"errorMessages\":[],\"failCount\":0,\"successCount\":1,\"totalCount\":1,\"message\":\"批量处理成功：全部 1 个实体处理成功\"},\"msg\":\"操作成功\"}',0,NULL,'2025-08-01 10:27:23',611),(87,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.batchProcessEntities()','POST',1,'qKnow','研发部门','/ext/entityPool/batch-process','127.0.0.1','内网IP','{\"idList\":[23],\"status\":1,\"remark\":\"\"}','{\"code\":200,\"data\":{\"successIds\":[23],\"failIds\":[],\"errorMessages\":[],\"failCount\":0,\"successCount\":1,\"totalCount\":1,\"message\":\"批量处理成功：全部 1 个实体处理成功\"},\"msg\":\"操作成功\"}',0,NULL,'2025-08-01 10:28:58',96),(88,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.batchProcessEntities()','POST',1,'qKnow','研发部门','/ext/entityPool/batch-process','127.0.0.1','内网IP','{\"idList\":[23],\"status\":1,\"remark\":\"\"}','{\"code\":500,\"msg\":\"批量处理失败：1 个实体处理失败\"}',0,NULL,'2025-08-01 10:32:23',23),(89,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.batchProcessEntities()','POST',1,'qKnow','研发部门','/ext/entityPool/batch-process','127.0.0.1','内网IP','{\"idList\":[23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47],\"status\":1,\"remark\":\"\"}','{\"code\":200,\"data\":{\"successIds\":[23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47],\"failIds\":[],\"errorMessages\":[],\"failCount\":0,\"successCount\":25,\"totalCount\":25,\"message\":\"批量处理成功：全部 25 个实体处理成功\"},\"msg\":\"操作成功\"}',0,NULL,'2025-08-01 15:31:21',1175),(90,'实体池',2,'tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.ExtEntityPoolController.batchProcessEntities()','POST',1,'qKnow','研发部门','/ext/entityPool/batch-process','127.0.0.1','内网IP','{\"idList\":[23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46],\"status\":1,\"remark\":\"\"}','{\"code\":200,\"data\":{\"successIds\":[23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46],\"failIds\":[],\"errorMessages\":[],\"failCount\":0,\"successCount\":24,\"totalCount\":24,\"message\":\"批量处理成功：全部 24 个实体处理成功\"},\"msg\":\"操作成功\"}',0,NULL,'2025-08-01 15:33:42',846);
/*!40000 ALTER TABLE `system_oper_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_post`
--

DROP TABLE IF EXISTS `system_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_post` (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_post`
--

LOCK TABLES `system_post` WRITE;
/*!40000 ALTER TABLE `system_post` DISABLE KEYS */;
INSERT INTO `system_post` VALUES (1,'ceo','董事长',1,'0','小桐','2025-07-29 15:18:24','',NULL,''),(2,'se','项目经理',2,'0','小桐','2025-07-29 15:18:24','',NULL,''),(3,'hr','人力资源',3,'0','小桐','2025-07-29 15:18:24','',NULL,''),(4,'user','普通员工',4,'0','小桐','2025-07-29 15:18:24','',NULL,'');
/*!40000 ALTER TABLE `system_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_role`
--

DROP TABLE IF EXISTS `system_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` char(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_role`
--

LOCK TABLES `system_role` WRITE;
/*!40000 ALTER TABLE `system_role` DISABLE KEYS */;
INSERT INTO `system_role` VALUES (1,'超级管理员','admin',1,'1',1,1,'0','0','小桐','2025-07-29 15:18:24','',NULL,'超级管理员'),(2,'普通角色','common',2,'2',1,1,'0','0','小桐','2025-07-29 15:18:24','',NULL,'普通角色');
/*!40000 ALTER TABLE `system_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_role_dept`
--

DROP TABLE IF EXISTS `system_role_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和部门关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_role_dept`
--

LOCK TABLES `system_role_dept` WRITE;
/*!40000 ALTER TABLE `system_role_dept` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_role_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_role_menu`
--

DROP TABLE IF EXISTS `system_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_role_menu`
--

LOCK TABLES `system_role_menu` WRITE;
/*!40000 ALTER TABLE `system_role_menu` DISABLE KEYS */;
INSERT INTO `system_role_menu` VALUES (1,2100),(1,2101),(1,2102),(1,2103),(1,2104),(1,2105),(1,2106),(1,2110),(1,2111),(1,2112),(1,2113),(1,2114),(1,2115),(1,2116);
/*!40000 ALTER TABLE `system_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_user`
--

DROP TABLE IF EXISTS `system_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `status` char(1) DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_user`
--

LOCK TABLES `system_user` WRITE;
/*!40000 ALTER TABLE `system_user` DISABLE KEYS */;
INSERT INTO `system_user` VALUES (1,103,'qKnow','小桐','00','support@qiantong.tech','15888888888','0','','$2a$10$M9QTlVS3URMVLDMMmJYYress8MgeKE0ahcNQSwO.T/TI8/U1U7pF6','0','0','127.0.0.1','2025-08-04 09:38:24','小桐','2025-07-29 15:18:24','','2025-08-04 09:38:23','管理员'),(100,NULL,'admin','管理员','00','','','0','','$2a$10$M9QTlVS3URMVLDMMmJYYress8MgeKE0ahcNQSwO.T/TI8/U1U7pF6','0','0','127.0.0.1','2025-07-30 17:09:51','','2025-07-29 15:47:19','','2025-07-30 17:09:51',NULL);
/*!40000 ALTER TABLE `system_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_user_post`
--

DROP TABLE IF EXISTS `system_user_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_user_post` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户与岗位关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_user_post`
--

LOCK TABLES `system_user_post` WRITE;
/*!40000 ALTER TABLE `system_user_post` DISABLE KEYS */;
INSERT INTO `system_user_post` VALUES (1,1);
/*!40000 ALTER TABLE `system_user_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_user_role`
--

DROP TABLE IF EXISTS `system_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户和角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_user_role`
--

LOCK TABLES `system_user_role` WRITE;
/*!40000 ALTER TABLE `system_user_role` DISABLE KEYS */;
INSERT INTO `system_user_role` VALUES (1,1),(100,1);
/*!40000 ALTER TABLE `system_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'qknow_dev'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-04 14:24:20
