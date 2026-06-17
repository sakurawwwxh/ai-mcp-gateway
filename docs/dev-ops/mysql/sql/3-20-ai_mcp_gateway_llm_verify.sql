-- ************************************************************
-- 3-20 LLM 对接测试 MCP 网关 - 验证数据增量脚本
--
-- 对应文档「第 3-20 节：验证服务，LLM 对接测试 MCP 接口」
-- 库：ai_mcp_gateway_v2（表结构以 mapper XML 为准）
--
-- 数据说明：
--   gateway_004 : auth=1（强校验），用于验证鉴权失败分支（传错误 api_key）
--   gateway_005 : auth=0（不校验），启用，课程演示主链路
--   tool 15     : gateway_005 → protocol_id 25225178（HTTP 协议）
--   8 条 http   : 协议配置（含嵌套入参 /employee/query-by-id）
--   29 条 mapping: 覆盖嵌套入参 xxxRequest01 / xxxRequest02 结构
--
-- 幂等：重复执行先清理对应 gateway/tool/protocol 数据再插入
-- ************************************************************

USE `ai_mcp_gateway_v2`;

-- ------------------------------------------------------------
-- 1. 网关：gateway_004（鉴权失败验证）/ gateway_005（主演示）
-- ------------------------------------------------------------
DELETE FROM `mcp_gateway` WHERE `gateway_id` IN ('gateway_004', 'gateway_005');

INSERT INTO `mcp_gateway` (`gateway_id`, `gateway_name`, `gateway_desc`, `version`, `status`, `auth`)
VALUES
    ('gateway_004', '鉴权失败验证网关', 'auth=1 强校验，用错误 api_key 验证鉴权失败分支', '1.0.0', 1, 1),
    ('gateway_005', 'LLM对接测试网关', 'auth=0 不校验，启用，用于 LLM 对接测试主链路', '1.0.0', 1, 0);

-- ------------------------------------------------------------
-- 2. 鉴权 Token：gateway_005 绑定有效 token（过期 2099，限流 36000s）
-- ------------------------------------------------------------
DELETE FROM `mcp_gateway_auth` WHERE `gateway_id` IN ('gateway_004', 'gateway_005');

INSERT INTO `mcp_gateway_auth` (`gateway_id`, `api_key`, `rate_limit`, `expire_time`, `status`)
VALUES
    ('gateway_005', 'f7c3b9e2a1d84f6e9b0c3a5d7e8f1a2b', 36000, '2099-02-25 23:59:59', 1);

-- ------------------------------------------------------------
-- 3. 工具配置：gateway_005 → tool 15（protocol_id=25225178，http）
-- ------------------------------------------------------------
DELETE FROM `mcp_gateway_tool` WHERE `gateway_id` IN ('gateway_004', 'gateway_005');

INSERT INTO `mcp_gateway_tool` (`gateway_id`, `tool_id`, `tool_name`, `tool_type`, `tool_description`, `tool_version`, `protocol_id`, `protocol_type`)
VALUES
    ('gateway_005', 15, 'JavaSDKMCPClient_getCompanyEmployee', 'function', '获取公司雇员信息', '1.0.0', 25225178, 'http');

-- ------------------------------------------------------------
-- 4. HTTP 协议配置（protocol_id=25225178，8 条）
--    下游业务服务：http://localhost:8701/api/v1/mcp/*
-- ------------------------------------------------------------
DELETE FROM `mcp_protocol_http` WHERE `protocol_id` = 25225178;

INSERT INTO `mcp_protocol_http` (`protocol_id`, `http_url`, `http_method`, `http_headers`, `timeout`, `retry_times`, `status`)
VALUES
    (25225178, 'http://localhost:8701/api/v1/mcp/get_company_employee', 'POST', '{"Content-Type": "application/json"}', 30000, 0, 1),
    (25225179, 'http://localhost:8701/api/v1/mcp/query-by-id', 'GET', '{"Content-Type": "application/json"}', 30000, 0, 1),
    (25225180, 'http://localhost:8701/api/v1/mcp/query-by-name', 'GET', '{"Content-Type": "application/json"}', 30000, 0, 1),
    (25225181, 'http://localhost:8701/api/v1/mcp/department/list', 'GET', '{"Content-Type": "application/json"}', 30000, 0, 1),
    (25225182, 'http://localhost:8701/api/v1/mcp/position/list', 'GET', '{"Content-Type": "application/json"}', 30000, 0, 1),
    (25225183, 'http://localhost:8701/api/v1/mcp/salary/query', 'POST', '{"Content-Type": "application/json"}', 30000, 0, 1),
    (25225184, 'http://localhost:8701/api/v1/mcp/attendance/stat', 'GET', '{"Content-Type": "application/json"}', 30000, 0, 1),
    (25225185, 'http://localhost:8701/api/v1/mcp/employee/count', 'GET', '{"Content-Type": "application/json"}', 30000, 0, 1);

-- ------------------------------------------------------------
-- 5. 协议映射（protocol_id=25225178，29 条，覆盖嵌套入参）
--    请求参数结构：
--      xxxRequest01 (object)
--        ├─ city (string)           城市（中文转拼音，北京:beijing）
--        ├─ company (object)
--        │    ├─ name (string)      公司名（中文转拼音，字节跳动:jd/bytedance）
--        │    └─ type (string)      公司类型
--        └─ address (string)        办公地址
--      xxxRequest02 (object)
--        ├─ employeeCount (string)  雇员数量
--        └─ position (string)       岗位
-- ------------------------------------------------------------
DELETE FROM `mcp_protocol_mapping` WHERE `protocol_id` = 25225178;

INSERT INTO `mcp_protocol_mapping` (`protocol_id`, `mapping_type`, `parent_path`, `field_name`, `mcp_path`, `mcp_type`, `mcp_desc`, `is_required`, `sort_order`)
VALUES
    -- xxxRequest01 根节点
    (25225178, 'request', NULL,             'xxxRequest01',            'xxxRequest01',                 'object',  NULL, 1, 1),
    (25225178, 'request', 'xxxRequest01',   'city',                    'xxxRequest01.city',            'string',  '城市名称,如果是中文汉字请先转换为汉语拼音,例如北京:beijing', 1, 1),
    (25225178, 'request', 'xxxRequest01',   'company',                 'xxxRequest01.company',         'object',  '公司信息,如果是中文汉字请先转换为汉语拼音,例如字节跳动:bytedance', 1, 2),
    (25225178, 'request', 'xxxRequest01.company', 'name',             'xxxRequest01.company.name',    'string',  '公司名称', 1, 1),
    (25225178, 'request', 'xxxRequest01.company', 'type',             'xxxRequest01.company.type',    'string',  '公司类型', 1, 2),
    (25225178, 'request', 'xxxRequest01',   'address',                 'xxxRequest01.address',         'string',  '办公地址', 0, 3),
    -- xxxRequest02 根节点
    (25225178, 'request', NULL,             'xxxRequest02',            'xxxRequest02',                 'object',  NULL, 1, 2),
    (25225178, 'request', 'xxxRequest02',   'employeeCount',           'xxxRequest02.employeeCount',   'string',  '雇员数量', 1, 1),
    (25225178, 'request', 'xxxRequest02',   'position',                'xxxRequest02.position',        'string',  '岗位名称', 0, 2),
    -- response 映射
    (25225178, 'response', NULL,            'result',                  'result',                       'object',  '返回结果', 0, 1),
    (25225178, 'response', 'result',        'code',                    'result.code',                  'string',  '状态码', 0, 1),
    (25225178, 'response', 'result',        'message',                 'result.message',               'string',  '提示信息', 0, 2),
    (25225178, 'response', 'result',        'data',                    'result.data',                  'object',  '业务数据', 0, 3),
    (25225178, 'response', 'result.data',   'employeeList',            'result.data.employeeList',     'array',   '员工列表', 0, 1),
    (25225178, 'response', 'result.data.employeeList', 'name',         'result.data.employeeList.name', 'string', '员工姓名', 0, 1),
    (25225178, 'response', 'result.data.employeeList', 'age',          'result.data.employeeList.age', 'number', '员工年龄', 0, 2),
    (25225178, 'response', 'result.data.employeeList', 'department',   'result.data.employeeList.department', 'string', '部门', 0, 3),
    (25225178, 'response', 'result.data.employeeList', 'position',     'result.data.employeeList.position', 'string', '岗位', 0, 4),
    (25225178, 'response', 'result.data.employeeList', 'salary',       'result.data.employeeList.salary', 'number', '薪资', 0, 5),
    (25225178, 'response', 'result.data.employeeList', 'entryDate',    'result.data.employeeList.entryDate', 'string', '入职日期', 0, 6),
    (25225178, 'response', 'result.data',   'total',                   'result.data.total',            'number',  '员工总数', 0, 2),
    (25225178, 'response', 'result.data',   'page',                    'result.data.page',             'number',  '当前页码', 0, 3),
    (25225178, 'response', 'result.data',   'size',                    'result.data.size',             'number',  '每页条数', 0, 4);

-- ************************************************************
-- 验证用例（手动 POST /api-gateway/admin/test_call_gateway）：
--
-- 用例1 - 主链路（gateway_005，LLM 自主调用工具）：
--   {"gatewayId":"gateway_005","message":"查询北京字节跳动的员工信息","authApiKey":"f7c3b9e2a1d84f6e9b0c3a5d7e8f1a2b","timeout":60000,"reload":true}
--
-- 用例2 - 鉴权失败（gateway_004 强校验 + 错误 key）：
--   {"gatewayId":"gateway_004","message":"查询员工信息","authApiKey":"wrong_key","timeout":60000}
--
-- 用例3 - 缓存复用（reload=false，复用已构建的 ChatModel）：
--   {"gatewayId":"gateway_005","message":"查询上海阿里巴巴的员工信息","authApiKey":"f7c3b9e2a1d84f6e9b0c3a5d7e8f1a2b","reload":false}
-- ************************************************************
