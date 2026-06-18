-- ************************************************************
-- 3-20 LLM 对接测试 MCP 网关 - 验证数据增量脚本
--
-- 对应文档「第 3-20 节：验证服务，LLM 对接测试 MCP 接口」
-- 库：ai_mcp_gateway_v2（表结构以 mapper XML 为准）
-- 数据与参考项目 ai-mcp-gateway-01/docs/dev-ops/bak/3-20-ai_mcp_gateway_v2.sql 对齐
--
-- 数据说明：
--   gateway_004 : auth=1（强校验），用于验证鉴权失败分支（传错误 api_key）
--   gateway_005 : auth=0（不校验），启用，课程演示主链路
--   tool 15     : gateway_005 → protocol_id 25225178（HTTP，POST get_company_employee）
--   mapping     : 单请求对象 xxxRequest01（含 company.deep.x01 嵌套，验证复杂入参解析）
--
-- 注意：mapping 仅暴露单个请求对象，与 SessionPort 取 arguments 第一个 value 的约定一致
--
-- 幂等：重复执行先清理对应数据再插入
-- ************************************************************

USE `ai_mcp_gateway_v2`;

-- ------------------------------------------------------------
-- 1. 网关：gateway_004（鉴权失败验证）/ gateway_005（主演示）
-- ------------------------------------------------------------
DELETE FROM `mcp_gateway` WHERE `gateway_id` IN ('gateway_004', 'gateway_005');

INSERT INTO `mcp_gateway` (`gateway_id`, `gateway_name`, `gateway_desc`, `version`, `status`, `auth`)
VALUES
    ('gateway_004', '测试网关', '测试网关', '1.0.0', 0, 1),
    ('gateway_005', '课程演示', '课程演示', '1.0.0', 1, 0);

-- ------------------------------------------------------------
-- 2. 鉴权 Token：gateway_005 绑定 token（rate_limit=36000）
--    gateway_005 auth=0 不校验，token 即使过期也不影响连接
-- ------------------------------------------------------------
DELETE FROM `mcp_gateway_auth` WHERE `gateway_id` IN ('gateway_004', 'gateway_005');

INSERT INTO `mcp_gateway_auth` (`gateway_id`, `api_key`, `rate_limit`, `expire_time`, `status`)
VALUES
    ('gateway_005', 'gw-GPJBQHFeBWVMSGASFii5xtsmlHF5SjURFwh7C7yGRP3UtX', 36000, '2099-02-25 01:23:57', 1);

-- ------------------------------------------------------------
-- 3. 工具配置：gateway_005 → tool_id 11275147（protocol_id=25225178，http）
-- ------------------------------------------------------------
DELETE FROM `mcp_gateway_tool` WHERE `gateway_id` IN ('gateway_004', 'gateway_005');

INSERT INTO `mcp_gateway_tool` (`gateway_id`, `tool_id`, `tool_name`, `tool_type`, `tool_description`, `tool_version`, `protocol_id`, `protocol_type`)
VALUES
    ('gateway_005', 11275147, 'JavaSDKMCPClient_getCompanyEmployee', 'function', '课程演示', '1.0.0', 25225178, 'http');

-- ------------------------------------------------------------
-- 4. HTTP 协议配置：protocol_id=25225178（POST get_company_employee）
--    下游业务服务：http://localhost:8701/api/v1/mcp/get_company_employee
-- ------------------------------------------------------------
DELETE FROM `mcp_protocol_http` WHERE `protocol_id` = 25225178;

INSERT INTO `mcp_protocol_http` (`protocol_id`, `http_url`, `http_method`, `http_headers`, `timeout`, `retry_times`, `status`)
VALUES
    (25225178, 'http://localhost:8701/api/v1/mcp/get_company_employee', 'post', '{"Content-Type":"application/json"}', 30000, 3, 1);

-- ------------------------------------------------------------
-- 5. 协议映射（protocol_id=25225178，单个请求对象 xxxRequest01）
--    请求参数结构（与参考项目 3-20 一致）：
--      xxxRequest01 (object)        公司员工信息查询请求
--        ├─ city (string)           城市名称(拼音),例如: beijing
--        ├─ company (object)        公司信息
--        │    ├─ deep (object)      测试（嵌套对象）
--        │    │    └─ x01 (string)  测试
--        │    ├─ name (string)      公司名称
--        │    └─ type (string)      公司类型
-- ------------------------------------------------------------
DELETE FROM `mcp_protocol_mapping` WHERE `protocol_id` = 25225178;

INSERT INTO `mcp_protocol_mapping` (`protocol_id`, `mapping_type`, `parent_path`, `field_name`, `mcp_path`, `mcp_type`, `mcp_desc`, `is_required`, `sort_order`)
VALUES
    (25225178, 'request', NULL,                  'xxxRequest01',            'xxxRequest01',                 'object',  '公司员工信息查询请求', 1, 1),
    (25225178, 'request', 'xxxRequest01',        'city',                    'xxxRequest01.city',            'string',  '城市名称(拼音),例如: beijing', 1, 1),
    (25225178, 'request', 'xxxRequest01',        'company',                 'xxxRequest01.company',         'object',  '公司信息', 1, 2),
    (25225178, 'request', 'xxxRequest01.company', 'deep',                   'xxxRequest01.company.deep',    'object',  '测试', 1, 1),
    (25225178, 'request', 'xxxRequest01.company.deep', 'x01',               'xxxRequest01.company.deep.x01', 'string', '测试', 1, 1),
    (25225178, 'request', 'xxxRequest01.company', 'name',                   'xxxRequest01.company.name',    'string',  '公司名称', 1, 2),
    (25225178, 'request', 'xxxRequest01.company', 'type',                   'xxxRequest01.company.type',    'string',  '公司类型', 1, 3);

-- ************************************************************
-- 验证用例（手动 POST /api-gateway/admin/test_call_gateway）：
--
-- 用例1 - 主链路（gateway_005，LLM 自主调用工具）：
--   {"gatewayId":"gateway_005","message":"查询北京字节跳动的员工信息","authApiKey":"gw-GPJBQHFeBWVMSGASFii5xtsmlHF5SjURFwh7C7yGRP3UtX","timeout":60000,"reload":true}
--
-- 用例2 - 鉴权失败（gateway_004 强校验 + 错误 key）：
--   {"gatewayId":"gateway_004","message":"查询员工信息","authApiKey":"wrong_key","timeout":60000}
--
-- 用例3 - 缓存复用（reload=false，复用已构建的 ChatModel）：
--   {"gatewayId":"gateway_005","message":"查询上海阿里巴巴的员工信息","authApiKey":"gw-GPJBQHFeBWVMSGASFii5xtsmlHF5SjURFwh7C7yGRP3UtX","reload":false}
-- ************************************************************
