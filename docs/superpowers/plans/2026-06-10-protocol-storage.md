# 协议存储服务 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现 `IProtocolStorage` 领域服务，把 `IProtocolAnalysis` 解析出的 `List<HTTPProtocolVO>` 落库到 `mcp_protocol_http` + `mcp_protocol_mapping` 两张表，并提供 `@SpringBootTest` 集成测试验证整条 analysis → storage 流水线。

**Architecture:** 严格 DDD 三层 — domain 暴露 `IProtocolStorage` + 纯代理 `ProtocolStorage`；infrastructure 暴露 `IProtocolRepository` 实现 `ProtocolRepository`，用 `@Transactional(rollbackFor = Exception.class)` 包整循环。1:1 移植参考 `ai-mcp-gateway-01` 同名模块，包名前缀 `cn.bugstack.ai` → `cn.tomato.ai`。

**Tech Stack:** Java 17、Spring Boot 3.4.3、MyBatis 3.0.4、MySQL、commons-lang3 3.9（`RandomStringUtils`）、Lombok、JUnit 4 + Spring Test。

---

## 文件结构

| 文件 | 角色 | 操作 |
|---|---|---|
| `ai-mcp-gateway-domain/.../model/valobj/enums/ProtocolStatusEnum.java` | 协议状态枚举（ENABLE/DISABLE） | 新建 |
| `ai-mcp-gateway-domain/.../model/entity/StorageCommandEntity.java` | 入参 DTO | 新建 |
| `ai-mcp-gateway-domain/.../adapter/repository/IProtocolRepository.java` | 仓储接口（domain 端口） | 新建 |
| `ai-mcp-gateway-domain/.../service/IProtocolStorage.java` | 存储服务接口 | 改 |
| `ai-mcp-gateway-domain/.../service/storage/ProtocolStorage.java` | 存储服务实现 | 改 |
| `ai-mcp-gateway-infrastructure/.../adapter/repository/ProtocolRepository.java` | 仓储实现（@Transactional） | 新建 |
| `ai-mcp-gateway-app/src/test/java/cn/tomato/ai/test/domain/protocol/ProtocolStorageTest.java` | 集成测试 | 新建 |

不在范围：`HTTPProtocolVO`、DAO 接口、PO、MyBatis mapper XML、swagger test JSON（均已就绪）。

---

## Task 1: 添加协议状态枚举（ProtocolStatusEnum）

**Files:**
- Create: `D:\IdeaProjects\ai-mcp-gateway\ai-mcp-gateway-domain\src\main\java\cn\tomato\ai\domain\protocol\model\valobj\enums\ProtocolStatusEnum.java`

- [ ] **Step 1: 创建枚举文件**

完整文件内容：

```java
package cn.tomato.ai.domain.protocol.model.valobj.enums;

import cn.tomato.ai.types.enums.ResponseCode;
import cn.tomato.ai.types.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 协议状态枚举
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/6/10 08:30
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ProtocolStatusEnum {

    ENABLE(1, "启用"),
    DISABLE(0, "禁用"),

    ;
    private Integer code;
    private String info;

    public static ProtocolStatusEnum get(Integer code) {
        if (code == null) return null;
        for (ProtocolStatusEnum val : values()) {
            if (val.code.equals(code)) {
                return val;
            }
        }
        throw new AppException(ResponseCode.ENUM_NOT_FOUND.getCode(), ResponseCode.ENUM_NOT_FOUND.getInfo());
    }
}
```

- [ ] **Step 2: 编译 domain 模块验证**

```bash
cd "D:\IdeaProjects\ai-mcp-gateway"
mvn clean compile -pl ai-mcp-gateway-domain -am
```

预期：`BUILD SUCCESS`，无错误。

- [ ] **Step 3: 提交**

```bash
cd "D:\IdeaProjects\ai-mcp-gateway"
git add ai-mcp-gateway-domain/src/main/java/cn/tomato/ai/domain/protocol/model/valobj/enums/ProtocolStatusEnum.java
git commit -m "feat(1-16): add ProtocolStatusEnum"
```

---

## Task 2: 添加入参 DTO（StorageCommandEntity）

**Files:**
- Create: `D:\IdeaProjects\ai-mcp-gateway\ai-mcp-gateway-domain\src\main\java\cn\tomato\ai\domain\protocol\model\entity\StorageCommandEntity.java`

- [ ] **Step 1: 创建 DTO 文件**

完整文件内容：

```java
package cn.tomato.ai.domain.protocol.model.entity;

import cn.tomato.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 存储协议实体
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/6/10 08:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageCommandEntity {

    /** 协议列表数据 */
    private List<HTTPProtocolVO> httpProtocolVOS;

}
```

注意：字段名 `httpProtocolVOS` 带尾随 S（与参考一致），对应 getter `getHttpProtocolVOS()`。

- [ ] **Step 2: 编译 domain 模块验证**

```bash
cd "D:\IdeaProjects\ai-mcp-gateway"
mvn clean compile -pl ai-mcp-gateway-domain -am
```

预期：`BUILD SUCCESS`。

- [ ] **Step 3: 提交**

```bash
cd "D:\IdeaProjects\ai-mcp-gateway"
git add ai-mcp-gateway-domain/src/main/java/cn/tomato/ai/domain/protocol/model/entity/StorageCommandEntity.java
git commit -m "feat(1-16): add StorageCommandEntity"
```

---

## Task 3: 添加仓储接口（IProtocolRepository）

**Files:**
- Create: `D:\IdeaProjects\ai-mcp-gateway\ai-mcp-gateway-domain\src\main\java\cn\tomato\ai\domain\protocol\adapter\repository\IProtocolRepository.java`

- [ ] **Step 1: 创建接口文件**

完整文件内容：

```java
package cn.tomato.ai.domain.protocol.adapter.repository;

import cn.tomato.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;

import java.util.List;

/**
 * 协议仓储服务接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/6/10 08:40
 */
public interface IProtocolRepository {

    List<Long> saveHttpProtocolAndMapping(List<HTTPProtocolVO> httpProtocolVOS);

}
```

- [ ] **Step 2: 编译验证**

```bash
cd "D:\IdeaProjects\ai-mcp-gateway"
mvn clean compile -pl ai-mcp-gateway-domain -am
```

预期：`BUILD SUCCESS`。

- [ ] **Step 3: 提交**

```bash
cd "D:\IdeaProjects\ai-mcp-gateway"
git add ai-mcp-gateway-domain/src/main/java/cn/tomato/ai/domain/protocol/adapter/repository/IProtocolRepository.java
git commit -m "feat(1-16): add IProtocolRepository interface"
```

---

## Task 4: 填充存储服务接口（IProtocolStorage）

**Files:**
- Modify: `D:\IdeaProjects\ai-mcp-gateway\ai-mcp-gateway-domain\src\main\java\cn\tomato\ai\domain\protocol\service\IProtocolStorage.java`

- [ ] **Step 1: 改写接口文件为完整签名**

将空接口替换为：

```java
package cn.tomato.ai.domain.protocol.service;

import cn.tomato.ai.domain.protocol.model.entity.StorageCommandEntity;

import java.util.List;

/**
 * 协议存储接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/6/10 08:45
 */
public interface IProtocolStorage {

    List<Long> doStorage(StorageCommandEntity commandEntity);

}
```

- [ ] **Step 2: 编译验证**

```bash
cd "D:\IdeaProjects\ai-mcp-gateway"
mvn clean compile -pl ai-mcp-gateway-domain -am
```

预期：`BUILD SUCCESS`。

- [ ] **Step 3: 提交**

```bash
cd "D:\IdeaProjects\ai-mcp-gateway"
git add ai-mcp-gateway-domain/src/main/java/cn/tomato/ai/domain/protocol/service/IProtocolStorage.java
git commit -m "feat(1-16): define IProtocolStorage.doStorage contract"
```

---

## Task 5: 实现存储服务（ProtocolStorage，纯代理）

**Files:**
- Modify: `D:\IdeaProjects\ai-mcp-gateway\ai-mcp-gateway-domain\src\main\java\cn\tomato\ai\domain\protocol\service\storage\ProtocolStorage.java`

- [ ] **Step 1: 改写实现类**

将空壳替换为：

```java
package cn.tomato.ai.domain.protocol.service.storage;

import cn.tomato.ai.domain.protocol.adapter.repository.IProtocolRepository;
import cn.tomato.ai.domain.protocol.model.entity.StorageCommandEntity;
import cn.tomato.ai.domain.protocol.service.IProtocolStorage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 协议存储实现 — 纯代理
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/6/10 08:50
 */
@Slf4j
@Service
public class ProtocolStorage implements IProtocolStorage {

    @Resource
    private IProtocolRepository repository;

    @Override
    public List<Long> doStorage(StorageCommandEntity commandEntity) {
        return repository.saveHttpProtocolAndMapping(commandEntity.getHttpProtocolVOS());
    }
}
```

- [ ] **Step 2: 编译验证（注意：impl 引用了 `IProtocolRepository`，但 infra 模块尚未提供实现类，编译应仍通过因为接口已在 domain 定义）**

```bash
cd "D:\IdeaProjects\ai-mcp-gateway"
mvn clean compile -pl ai-mcp-gateway-domain -am
```

预期：`BUILD SUCCESS`（Spring 容器运行时才会检查 bean 注入，编译期不查）。

- [ ] **Step 3: 提交**

```bash
cd "D:\IdeaProjects\ai-mcp-gateway"
git add ai-mcp-gateway-domain/src/main/java/cn/tomato/ai/domain/protocol/service/storage/ProtocolStorage.java
git commit -m "feat(1-16): implement ProtocolStorage as repository delegate"
```

---

## Task 6: 实现仓储（ProtocolRepository，@Transactional 核心）

**Files:**
- Create: `D:\IdeaProjects\ai-mcp-gateway\ai-mcp-gateway-infrastructure\src\main\java\cn\tomato\ai\infrastructure\adapter\repository\ProtocolRepository.java`

- [ ] **Step 1: 创建仓储实现文件**

完整文件内容：

```java
package cn.tomato.ai.infrastructure.adapter.repository;

import cn.tomato.ai.domain.protocol.adapter.repository.IProtocolRepository;
import cn.tomato.ai.domain.protocol.model.valobj.enums.ProtocolStatusEnum;
import cn.tomato.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import cn.tomato.ai.infrastructure.dao.IMcpProtocolHttpDao;
import cn.tomato.ai.infrastructure.dao.IMcpProtocolMappingDao;
import cn.tomato.ai.infrastructure.dao.po.McpProtocolHttpPO;
import cn.tomato.ai.infrastructure.dao.po.McpProtocolMappingPO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 协议仓储实现
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/6/10 08:55
 */
@Slf4j
@Repository
public class ProtocolRepository implements IProtocolRepository {

    @Resource
    private IMcpProtocolHttpDao protocolHttpDao;

    @Resource
    private IMcpProtocolMappingDao protocolMappingDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<Long> saveHttpProtocolAndMapping(List<HTTPProtocolVO> httpProtocolVOS) {
        List<Long> protocolIdList = new ArrayList<>();

        for (HTTPProtocolVO httpProtocolVO : httpProtocolVOS) {

            // 0. 生成协议ID，八位数字
            long protocolId = Long.parseLong(RandomStringUtils.randomNumeric(8));

            // 1. 保存 HTTP 协议配置
            McpProtocolHttpPO mcpProtocolHttpPO = McpProtocolHttpPO.builder()
                    .protocolId(protocolId)
                    .httpUrl(httpProtocolVO.getHttpUrl())
                    .httpMethod(httpProtocolVO.getHttpMethod())
                    .httpHeaders(httpProtocolVO.getHttpHeaders())
                    .timeout(httpProtocolVO.getTimeout())
                    .retryTimes(3)
                    .status(ProtocolStatusEnum.ENABLE.getCode())
                    .build();
            protocolHttpDao.insert(mcpProtocolHttpPO);

            // 2. 保存协议映射配置
            List<HTTPProtocolVO.ProtocolMapping> mappings = httpProtocolVO.getMappings();
            if (null == mappings || mappings.isEmpty()) continue;

            for (HTTPProtocolVO.ProtocolMapping mapping : mappings) {
                McpProtocolMappingPO mcpProtocolMappingPO = McpProtocolMappingPO.builder()
                        .protocolId(protocolId)
                        .mappingType(mapping.getMappingType())
                        .parentPath(mapping.getParentPath())
                        .fieldName(mapping.getFieldName())
                        .mcpPath(mapping.getMcpPath())
                        .mcpType(mapping.getMcpType())
                        .mcpDesc(mapping.getMcpDesc())
                        .isRequired(mapping.getIsRequired())
                        .sortOrder(mapping.getSortOrder())
                        .build();
                protocolMappingDao.insert(mcpProtocolMappingPO);
            }

            protocolIdList.add(protocolId);
        }

        return protocolIdList;
    }
}
```

- [ ] **Step 2: 编译整个项目验证（包含 infra 模块）**

```bash
cd "D:\IdeaProjects\ai-mcp-gateway"
mvn clean install -DskipTests
```

预期：`BUILD SUCCESS`。所有模块（含 `ai-mcp-gateway-infrastructure`）编译通过，Spring bean 装配自检通过（启动时如有 `NoSuchBeanDefinitionException` 会在测试阶段暴露）。

- [ ] **Step 3: 提交**

```bash
cd "D:\IdeaProjects\ai-mcp-gateway"
git add ai-mcp-gateway-infrastructure/src/main/java/cn/tomato/ai/infrastructure/adapter/repository/ProtocolRepository.java
git commit -m "feat(1-16): implement ProtocolRepository with @Transactional write"
```

---

## Task 7: 添加集成测试（ProtocolStorageTest）

**Files:**
- Create: `D:\IdeaProjects\ai-mcp-gateway\ai-mcp-gateway-app\src\test\java\cn\tomato\ai\test\domain\protocol\ProtocolStorageTest.java`

- [ ] **Step 1: 创建测试文件**

完整文件内容：

```java
package cn.tomato.ai.test.domain.protocol;

import cn.tomato.ai.domain.protocol.model.entity.AnalysisCommandEntity;
import cn.tomato.ai.domain.protocol.model.entity.StorageCommandEntity;
import cn.tomato.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import cn.tomato.ai.domain.protocol.service.IProtocolAnalysis;
import cn.tomato.ai.domain.protocol.service.IProtocolStorage;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 协议存储集成测试 — 串联 analysis → storage
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/6/10 09:00
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProtocolStorageTest {

    @Value("classpath:swagger/api-docs-test03.json")
    private Resource apiDocs;

    @Resource
    private IProtocolAnalysis protocolAnalysis;

    @Resource
    private IProtocolStorage protocolStorage;

    @Test
    public void test_storage() throws IOException {
        // 1. 协议解析
        String json = new String(
                FileCopyUtils.copyToByteArray(apiDocs.getInputStream()),
                StandardCharsets.UTF_8);
        List<String> endpoints = Arrays.asList("/api/v1/mcp/get_company_employee");

        AnalysisCommandEntity commandEntity = AnalysisCommandEntity.builder()
                .openApiJson(json)
                .endpoints(endpoints)
                .build();

        List<HTTPProtocolVO> httpProtocolVOS = protocolAnalysis.doAnalysis(commandEntity);
        log.info("解析协议:{}", JSON.toJSONString(httpProtocolVOS));

        // 2. 协议存储
        List<Long> protocolIdList = protocolStorage.doStorage(
                StorageCommandEntity.builder()
                        .httpProtocolVOS(httpProtocolVOS)
                        .build());
        log.info("存储协议:{}", JSON.toJSONString(protocolIdList));
    }
}
```

- [ ] **Step 2: 编译测试模块**

```bash
cd "D:\IdeaProjects\ai-mcp-gateway"
mvn test-compile -pl ai-mcp-gateway-app -am
```

预期：`BUILD SUCCESS`。

- [ ] **Step 3: 提交测试文件**

```bash
cd "D:\IdeaProjects\ai-mcp-gateway"
git add ai-mcp-gateway-app/src/test/java/cn/tomato/ai/test/domain/protocol/ProtocolStorageTest.java
git commit -m "test(1-16): add ProtocolStorageTest integration test"
```

---

## Task 8: 端到端验证

**Files:** 无新增/修改（验证任务）

- [ ] **Step 1: 全量构建（跳过测试，确认编译与装配无错）**

```bash
cd "D:\IdeaProjects\ai-mcp-gateway"
mvn clean install -DskipTests
```

预期：`BUILD SUCCESS`，所有 7 个模块编译通过。

- [ ] **Step 2: 运行集成测试（需 dev 环境 MySQL 可用，参见 `application-dev.yml` 中 `ai_mcp_gateway_v2`）**

```bash
cd "D:\IdeaProjects\ai-mcp-gateway"
mvn test -Dtest=cn.tomato.ai.test.domain.protocol.ProtocolStorageTest -pl ai-mcp-gateway-app
```

预期：
- 测试通过（绿色）
- 控制台日志中可见：
  - `解析协议: [...]` —— 含 1 个 `HTTPProtocolVO`（含 `httpUrl` / `httpMethod` / `timeout` / `mappings` 列表）
  - `存储协议: [<8位数字>]` —— 1 个新生成的 `protocolId`

- [ ] **Step 3: 验证数据库落库（手动 SQL 检查，可选）**

```sql
USE ai_mcp_gateway_v2;
SELECT * FROM mcp_protocol_http ORDER BY id DESC LIMIT 1;
SELECT * FROM mcp_protocol_mapping ORDER BY id DESC LIMIT 20;
```

预期：
- `mcp_protocol_http` 新增 1 行：`http_url` / `http_method` / `timeout` 非空，`retry_times=3`，`status=1`，`protocol_id` 为 8 位数字
- `mcp_protocol_mapping` 新增 N 行（N = 该 VO 的 mapping 数量），`protocol_id` 与上一步一致

- [ ] **Step 4: 确认提交历史**

```bash
cd "D:\IdeaProjects\ai-mcp-gateway"
git log --oneline -8
```

预期：可见 7 个 `feat(1-16)` / `test(1-16)` 提交（在 `d6f2b27 docs: 1-16 协议存储服务设计文档` 之上）。

---

## 验证清单（与设计文档 §9 对齐）

- [ ] `mvn clean install -DskipTests` 编译通过
- [ ] `ProtocolStorageTest.test_storage` 在 dev 环境执行成功
- [ ] `mcp_protocol_http` 新增 1 行（`retry_times=3` / `status=1` 正确）
- [ ] `mcp_protocol_mapping` 新增 N 行（`protocol_id` 与 HTTP 行一致）
- [ ] git 提交历史完整，7 个 commit 按顺序排列

## 不在范围（重申）

- trigger / case / api 三层入口
- `mcp_gateway_tool` 表写入
- `protocolId` 去重检查
- 批量 insert 优化（foreach）
- HTTP 协议 update / delete 操作

---

**Plan complete and saved to `docs/superpowers/plans/2026-06-10-protocol-storage.md`. Two execution options:**

1. **Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration
2. **Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

Which approach?
