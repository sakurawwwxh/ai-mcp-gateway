-- ************************************************************
-- 3-20 LLM 对接测试 - ai_mcp_gateway_v2 完整库快照
--
-- 表结构以当前 master 代码的 mapper XML / PO 为准（mcp_gateway 含 version+auth 列）。
-- 在 3-10 v2 快照基础上：补 version/auth 列，并追加 3-20 LLM 验证数据。
-- 用于本地全新建库回放。
-- ************************************************************

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE='NO_AUTO_VALUE_ON_ZERO', SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE IF NOT EXISTS `ai_mcp_gateway_v2` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `ai_mcp_gateway_v2`;

-- ------------------------------------------------------------
-- 表 mcp_gateway（含 version + auth 列）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `mcp_gateway`;
CREATE TABLE `mcp_gateway` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `gateway_id` varchar(64) NOT NULL COMMENT '网关唯一标识',
  `gateway_name` varchar(128) NOT NULL COMMENT '网关名称',
  `gateway_desc` varchar(512) DEFAULT NULL COMMENT '网关描述',
  `version` varchar(16) DEFAULT NULL COMMENT '网关版本',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `auth` tinyint(1) NOT NULL DEFAULT '0' COMMENT '校验状态：0-不校验，1-强校验',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_gateway_id` (`gateway_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='MCP网关配置表';

INSERT INTO `mcp_gateway` (`id`, `gateway_id`, `gateway_name`, `gateway_desc`, `version`, `status`, `auth`, `create_time`, `update_time`)
VALUES
    (1, 'gateway_001', '员工信息查询网关', '用于查询公司员工信息的MCP网关', '1.0.0', 1, 0, '2026-01-02 13:10:19', '2026-01-02 13:10:19'),
    (4, 'gateway_004', '测试网关', '测试网关', '1.0.0', 0, 1, '2026-06-17 00:00:00', '2026-06-17 00:00:00'),
    (5, 'gateway_005', '课程演示', '课程演示', '1.0.0', 1, 0, '2026-06-17 00:00:00', '2026-06-17 00:00:00');

-- ------------------------------------------------------------
-- 表 mcp_gateway_auth
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `mcp_gateway_auth`;
CREATE TABLE `mcp_gateway_auth` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `gateway_id` varchar(64) NOT NULL COMMENT '网关ID',
  `api_key` varchar(128) DEFAULT NULL COMMENT 'API密钥',
  `rate_limit` int DEFAULT '1000' COMMENT '速率限制（次/小时）',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_gateway` (`gateway_id`),
  KEY `idx_gateway_id` (`gateway_id`),
  KEY `idx_api_key` (`api_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户网关权限表';

INSERT INTO `mcp_gateway_auth` (`id`, `gateway_id`, `api_key`, `rate_limit`, `expire_time`, `status`, `create_time`, `update_time`)
VALUES
    (1, 'gateway_001', 'RS590LKPOD8877DDLMFKS4', 1000, '2029-01-02 16:44:19', 1, '2026-01-02 16:44:19', '2026-01-02 16:44:34'),
    (2, 'gateway_005', 'gw-GPJBQHFeBWVMSGASFii5xtsmlHF5SjURFwh7C7yGRP3UtX', 36000, '2099-02-25 01:23:57', 1, '2026-06-17 00:00:00', '2026-06-17 00:00:00');

-- ------------------------------------------------------------
-- 表 mcp_gateway_tool
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `mcp_gateway_tool`;
CREATE TABLE `mcp_gateway_tool` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `gateway_id` varchar(64) NOT NULL COMMENT '网关ID',
  `tool_id` bigint NOT NULL COMMENT '工具ID',
  `tool_name` varchar(128) NOT NULL COMMENT 'MCP工具名称',
  `tool_type` varchar(32) NOT NULL DEFAULT 'function' COMMENT '工具类型：function/resource',
  `tool_description` varchar(512) NOT NULL COMMENT '工具描述',
  `tool_version` varchar(16) NOT NULL COMMENT '工具版本',
  `protocol_id` bigint NOT NULL COMMENT '协议ID',
  `protocol_type` varchar(4) NOT NULL DEFAULT 'http' COMMENT '协议类型；http、dubbo、rabbitmq',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_tool_name` (`gateway_id`,`tool_name`),
  UNIQUE KEY `uq_tool_id` (`tool_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `mcp_gateway_tool` (`id`, `gateway_id`, `tool_id`, `tool_name`, `tool_type`, `tool_description`, `tool_version`, `protocol_id`, `protocol_type`, `create_time`, `update_time`)
VALUES
    (1, 'gateway_001', 1, 'JavaSDKMCPClient_getCompanyEmployee', 'function', '获取公司雇员信息', '1.0.0', 1, 'http', '2026-02-01 19:12:44', '2026-02-01 19:46:07'),
    (2, 'gateway_005', 11275147, 'JavaSDKMCPClient_getCompanyEmployee', 'function', '课程演示', '1.0.0', 25225178, 'http', '2026-06-17 00:00:00', '2026-06-17 00:00:00');

-- ------------------------------------------------------------
-- 表 mcp_protocol_http
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `mcp_protocol_http`;
CREATE TABLE `mcp_protocol_http` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `protocol_id` bigint NOT NULL COMMENT '协议ID',
  `http_url` varchar(512) NOT NULL COMMENT 'HTTP接口地址',
  `http_method` varchar(16) NOT NULL DEFAULT 'POST' COMMENT 'HTTP请求方法：GET/POST/PUT/DELETE',
  `http_headers` text COMMENT 'HTTP请求头（JSON格式）',
  `timeout` int DEFAULT '30000' COMMENT '超时时间（毫秒）',
  `retry_times` tinyint DEFAULT '0' COMMENT '重试次数',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='MCP工具注册表';

INSERT INTO `mcp_protocol_http` (`id`, `protocol_id`, `http_url`, `http_method`, `http_headers`, `timeout`, `retry_times`, `status`, `create_time`, `update_time`)
VALUES
    (1, 1, 'http://localhost:8701/api/v1/mcp/get_company_employee', 'post', '{"Content-Type": "application/json"}', 30000, 0, 1, '2026-01-02 13:10:19', '2026-02-01 19:14:57'),
    (2, 2, 'http://localhost:8701/api/v1/mcp/query-by-id', 'get', '{"Content-Type": "application/json"}', 30000, 0, 1, '2026-01-02 13:10:19', '2026-02-01 19:14:58'),
    (3, 25225178, 'http://localhost:8701/api/v1/mcp/get_company_employee', 'post', '{"Content-Type":"application/json"}', 30000, 3, 1, '2026-06-17 00:00:00', '2026-06-17 00:00:00');

-- ------------------------------------------------------------
-- 表 mcp_protocol_mapping（以 protocol_id 关联）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `mcp_protocol_mapping`;
CREATE TABLE `mcp_protocol_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `protocol_id` bigint NOT NULL COMMENT '协议ID',
  `mapping_type` varchar(32) NOT NULL COMMENT '映射类型：request-请求参数映射，response-响应数据映射',
  `parent_path` varchar(256) DEFAULT NULL COMMENT '父级路径（根节点为NULL）',
  `field_name` varchar(128) NOT NULL COMMENT '字段名称',
  `mcp_path` varchar(256) NOT NULL COMMENT 'MCP完整路径',
  `mcp_type` varchar(32) NOT NULL COMMENT 'MCP数据类型：string/number/boolean/object/array',
  `mcp_desc` varchar(512) DEFAULT NULL COMMENT 'MCP字段描述',
  `is_required` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否必填：0-否，1-是',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_mapping_type` (`mapping_type`),
  KEY `idx_parent_path` (`parent_path`),
  KEY `idx_mcp_path` (`mcp_path`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='MCP映射配置表';

-- protocol_id=1（gateway_001 原始映射）
INSERT INTO `mcp_protocol_mapping` (`protocol_id`, `mapping_type`, `parent_path`, `field_name`, `mcp_path`, `mcp_type`, `mcp_desc`, `is_required`, `sort_order`, `create_time`, `update_time`)
VALUES
    (1, 'request', NULL, 'xxxRequest01', 'xxxRequest01', 'object', NULL, 1, 1, '2026-01-02 13:10:19', '2026-02-01 19:45:59'),
    (1, 'request', 'xxxRequest01', 'city', 'xxxRequest01.city', 'string', '城市名称,如果是中文汉字请先转换为汉语拼音,例如北京:beijing', 1, 1, '2026-01-02 13:10:19', '2026-02-01 19:46:00'),
    (1, 'request', 'xxxRequest01', 'company', 'xxxRequest01.company', 'object', '公司信息,如果是中文汉字请先转换为汉语拼音,例如北京:jd/alibaba', 1, 2, '2026-01-02 13:10:19', '2026-02-01 19:46:01'),
    (1, 'request', 'xxxRequest01.company', 'name', 'xxxRequest01.company.name', 'string', '公司名称', 1, 1, '2026-01-02 13:10:19', '2026-02-01 19:46:01'),
    (1, 'request', 'xxxRequest01.company', 'type', 'xxxRequest01.company.type', 'string', '公司类型', 1, 2, '2026-01-02 13:10:19', '2026-02-01 19:46:02');

-- protocol_id=25225178（gateway_005 LLM 验证映射，与增量脚本一致，单请求对象 xxxRequest01）
INSERT INTO `mcp_protocol_mapping` (`protocol_id`, `mapping_type`, `parent_path`, `field_name`, `mcp_path`, `mcp_type`, `mcp_desc`, `is_required`, `sort_order`, `create_time`, `update_time`)
VALUES
    (25225178, 'request', NULL, 'xxxRequest01', 'xxxRequest01', 'object', '公司员工信息查询请求', 1, 1, '2026-06-17 00:00:00', '2026-06-17 00:00:00'),
    (25225178, 'request', 'xxxRequest01', 'city', 'xxxRequest01.city', 'string', '城市名称(拼音),例如: beijing', 1, 1, '2026-06-17 00:00:00', '2026-06-17 00:00:00'),
    (25225178, 'request', 'xxxRequest01', 'company', 'xxxRequest01.company', 'object', '公司信息', 1, 2, '2026-06-17 00:00:00', '2026-06-17 00:00:00'),
    (25225178, 'request', 'xxxRequest01.company', 'deep', 'xxxRequest01.company.deep', 'object', '测试', 1, 1, '2026-06-17 00:00:00', '2026-06-17 00:00:00'),
    (25225178, 'request', 'xxxRequest01.company.deep', 'x01', 'xxxRequest01.company.deep.x01', 'string', '测试', 1, 1, '2026-06-17 00:00:00', '2026-06-17 00:00:00'),
    (25225178, 'request', 'xxxRequest01.company', 'name', 'xxxRequest01.company.name', 'string', '公司名称', 1, 2, '2026-06-17 00:00:00', '2026-06-17 00:00:00'),
    (25225178, 'request', 'xxxRequest01.company', 'type', 'xxxRequest01.company.type', 'string', '公司类型', 1, 3, '2026-06-17 00:00:00', '2026-06-17 00:00:00');

/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
