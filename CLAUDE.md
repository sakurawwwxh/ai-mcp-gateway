# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个 MCP (Model Context Protocol) 网关服务，基于 Java 17 + Spring Boot 3.4.3 构建，用于处理 AI 客户端与服务器之间的通信。核心功能是通过 SSE (Server-Sent Events) 建立长连接，并处理 JSON-RPC 2.0 格式的 MCP 协议消息。

## 常用命令

```bash
# 构建整个项目
mvn clean install

# 构建并跳过测试
mvn clean install -DskipTests

# 运行单个测试类
mvn test -Dtest=cn.tomato.ai.test.ApiTest -pl ai-mcp-gateway-app

# 运行单个测试方法
mvn test -Dtest=cn.tomato.ai.test.ApiTest#testMethod -pl ai-mcp-gateway-app

# 启动应用（开发环境）
mvn spring-boot:run -pl ai-mcp-gateway-app -Pdev

# 打包
mvn clean package -Pdev
```

## 模块结构

项目采用 DDD (领域驱动设计) 分层架构：

```
ai-mcp-gateway/
├── ai-mcp-gateway-api/          # API 定义层 - 对外暴露的接口和 DTO
├── ai-mcp-gateway-app/          # 应用层 - 启动入口、配置
├── ai-mcp-gateway-domain/       # 领域层 - 核心业务逻辑
├── ai-mcp-gateway-trigger/      # 触发器层 - HTTP 控制器
├── ai-mcp-gateway-infrastructure/ # 基础设施层 - 数据访问、外部服务
├── ai-mcp-gateway-types/        # 类型层 - 通用枚举、异常、常量
└── ai-mcp-gateway-case/         # 用例层 - 服务编排、会话管理
```

## 核心架构

### MCP 协议处理流程

1. **SSE 连接建立**: 客户端通过 `GET /{gatewayId}/mcp/sse` 建立长连接
2. **消息接收**: 客户端通过 `POST /{gatewayId}/mcp/sse?sessionId=xxx` 发送 JSON-RPC 消息
3. **消息分发**: `SessionMessageService` 根据 `method` 字段路由到对应的 Handler
4. **响应返回**: 处理结果通过 SSE 推送给客户端

### 消息处理策略模式

使用枚举 + 策略模式处理不同类型的 MCP 请求：

- `SessionMessageHandlerMethodEnum`: 定义方法名到 Handler 的映射
- `IRequestHandler`: 请求处理器接口
- 实现类: `InitializeHandler`, `ToolsListHandler`, `ToolsCallHandler`, `ResourcesListHandler`

### 会话管理

使用树形策略模式管理会话生命周期：
- `AbstractMcpSessionSupport`: 会话抽象基类
- `DefaultMcpSessionFactory`: 会话工厂
- 节点: `RootNode` -> `VerifyNode` -> `SessionNode` -> `EndNode`

## 关键配置

### 数据库配置 (application-dev.yml)

- MySQL 数据库: `ai_mcp_gateway`
- MyBatis mapper 路径: `classpath:/mybatis/mapper/*.xml`
- MyBatis 配置: `classpath:mybatis/config/mybatis-config.xml`

### 服务端口

- 开发环境: 8777
- 上下文路径: `/api-gateway`

## JSON-RPC 消息格式

项目实现了 MCP 协议的 JSON-RPC 2.0 消息格式，定义在 `McpSchemaVO` 中：

- `JSONRPCRequest`: 请求消息（包含 method 和 id）
- `JSONRPCNotification`: 通知消息（包含 method，无 id）
- `JSONRPCResponse`: 响应消息（包含 result 或 error）

支持的 MCP 方法：
- `initialize`: 初始化连接
- `tools/list`: 获取工具列表
- `tools/call`: 调用工具
- `resources/list`: 获取资源列表

## 测试

测试类位于 `ai-mcp-gateway-app/src/test/java/cn/tomato/ai/test/` 目录下，主要包括：
- `ApiTest.java`: API 测试
- `infrastructure/dao/`: DAO 层测试

## 依赖说明

主要依赖：
- Spring AI 1.0.0: AI 能力集成
- MyBatis 3.0.4: 数据库 ORM
- FastJSON 2.0.28: JSON 处理
- Project Reactor: 响应式编程支持
- xfg-wrench-starter-design-framework 3.0.0: 设计模式框架（策略树模式）
