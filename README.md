<div align="center">

<img src="docs/assets/mark.svg" alt="ai-mcp-gateway" width="104" height="104">

# ai-mcp-gateway

面向 AI 客户端的 MCP 网关：用 SSE 长连接 + JSON-RPC 2.0 在客户端与 MCP 服务器之间转发消息。

Java 17 · Spring Boot 3.4.3 · Maven · MyBatis · 端口 `8777` · 上下文 `/api-gateway`

</div>

## 它解决什么

MCP（Model Context Protocol）的客户端要连多个 MCP 服务器，每条连接都得自己处理会话、鉴权和消息格式。这个网关把这段收在一处：客户端只连网关，由网关按 `method` 分发到对应的处理器，再把结果推回去。

## 一条消息怎么走完

```
客户端 ──GET /{gatewayId}/mcp/sse──────────────────►  建立长连接
客户端 ──POST /{gatewayId}/mcp/sse?sessionId=xxx───►  JSON-RPC 请求
                                                          │
                                            SessionMessageService 按 method 路由
                                                          │
                                                   IRequestHandler 实现
                                                          │
        客户端 ◄─────────────SSE 推送响应───────────────┘
```

### 支持的 MCP 方法

| 方法 | 处理器 |
| :--- | :--- |
| `initialize` | `InitializeHandler` |
| `tools/list` | `ToolsListHandler` |
| `tools/call` | `ToolsCallHandler` |
| `resources/list` | `ResourcesListHandler` |

方法名到处理器的映射写在 `SessionMessageHandlerMethodEnum` 里，新增能力就是往枚举里加一项、再加一个 `IRequestHandler` 实现。

### 会话生命周期

用树形策略模式串起来，会话的每一步是一个节点：

```
RootNode → VerifyNode → SessionNode → EndNode
```

`AbstractMcpSessionSupport` 是抽象基类，`DefaultMcpSessionFactory` 负责装配。

### 消息格式

JSON-RPC 2.0 的三种消息体定义在 `McpSchemaVO`：`JSONRPCRequest`、`JSONRPCNotification`、`JSONRPCResponse`。

## 模块结构

DDD 分层，每个模块只依赖它该依赖的：

| 模块 | 职责 |
| :--- | :--- |
| `ai-mcp-gateway-api` | 对外暴露的接口与 DTO |
| `ai-mcp-gateway-app` | 启动入口、配置 |
| `ai-mcp-gateway-domain` | 核心业务逻辑 |
| `ai-mcp-gateway-trigger` | HTTP 控制器 |
| `ai-mcp-gateway-infrastructure` | 数据访问、外部服务 |
| `ai-mcp-gateway-types` | 通用枚举、异常、常量 |
| `ai-mcp-gateway-case` | 用例编排、会话管理 |

## 常用命令

```bash
mvn clean install                                     # 构建全部模块
mvn clean install -DskipTests                         # 跳过测试
mvn spring-boot:run -pl ai-mcp-gateway-app -Pdev      # 本地起服务
mvn test -Dtest=cn.tomato.ai.test.ApiTest -pl ai-mcp-gateway-app
```

开发环境用 MySQL 库 `ai_mcp_gateway`，MyBatis 的 mapper 路径 `classpath:/mybatis/mapper/*.xml`。

## 主要依赖

- Spring AI 1.0.0
- MyBatis 3.0.4
- FastJSON 2.0.28
- Project Reactor
- xfg-wrench-starter-design-framework 3.0.0（策略树模式）

## 给 AI 工具的说明

仓库根目录有 `CLAUDE.md`，记录了常用命令、模块结构和 MCP 协议的处理流程。