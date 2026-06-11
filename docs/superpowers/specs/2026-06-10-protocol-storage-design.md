# 协议存储服务 — 设计文档

| 项 | 值 |
|---|---|
| 分支 | `1-16-domain-protocol-storage` |
| 上游 | `1-15` 协议解析 (`IProtocolAnalysis` / `ProtocolAnalysis`) |
| 范围 | 协议存储领域服务（domain service + repository + integration test） |
| 不在范围 | trigger/case/api 三层；DAO 接口；PO；MyBatis mapper XML（均已就绪） |
| 基准 | 1:1 移植参考项目 `ai-mcp-gateway-01` 的 `cn.bugstack.ai.domain.protocol.service.storage` 实现，仅替换包名前缀 `cn.bugstack.ai` → `cn.tomato.ai` |

## 1. 目标与背景

`ProtocolAnalysis`（1-15 落地）负责把 Swagger/OpenAPI JSON 解析为 `List<HTTPProtocolVO>`。当前解析结果只存在于内存，无法持久化，网关重启即丢。本设计补齐写盘能力：把解析产物落库到 `mcp_protocol_http` + `mcp_protocol_mapping` 两张表，并通过 8 位随机数生成逻辑 `protocolId` 关联两表。

不涉及 `mcp_gateway_tool` 的写操作（参考项目同样不做；工具注册是独立 issue）。

## 2. 架构与调用链

```
ProtocolStorageTest (integration test, @SpringBootTest)
    │
    │  StorageCommandEntity{httpProtocolVOS: List<HTTPProtocolVO>}
    ▼
IProtocolStorage.doStorage(cmd)                          [domain 接口]
    │
    ▼
ProtocolStorage.doStorage                                [domain @Service, 纯代理]
    │
    │  commandEntity.getHttpProtocolVOS()
    ▼
IProtocolRepository.saveHttpProtocolAndMapping(voList)   [domain 仓储接口]
    │
    ▼
ProtocolRepository.saveHttpProtocolAndMapping            [infra @Repository, @Transactional]
    │
    │  for each HTTPProtocolVO:
    │    1. protocolId = Long.parseLong(RandomStringUtils.randomNumeric(8))
    │    2. protocolHttpDao.insert(McpProtocolHttpPO)        → mcp_protocol_http
    │    3. for each ProtocolMapping:
    │         protocolMappingDao.insert(McpProtocolMappingPO) → mcp_protocol_mapping
    │
    ▼
List<Long> protocolIds                                    [返回新生成的协议ID列表]
```

**模块依赖方向**：`trigger → case → domain ← infrastructure`，与项目 DDD 规范一致。
- `domain` 依赖 `types`（AppException、ResponseCode）
- `infrastructure` 依赖 `domain`（实现 `IProtocolRepository`）

## 3. 文件清单

### 3.1 改动（已存在空壳）

| 路径 | 状态 | 说明 |
|---|---|---|
| `ai-mcp-gateway-domain/src/main/java/cn/tomato/ai/domain/protocol/service/IProtocolStorage.java` | 改 | 填充方法签名 |
| `ai-mcp-gateway-domain/src/main/java/cn/tomato/ai/domain/protocol/service/storage/ProtocolStorage.java` | 改 | 改为 `@Slf4j @Service` 纯代理 |

### 3.2 新增

| 路径 | 角色 |
|---|---|
| `ai-mcp-gateway-domain/src/main/java/cn/tomato/ai/domain/protocol/model/entity/StorageCommandEntity.java` | 入参 DTO |
| `ai-mcp-gateway-domain/src/main/java/cn/tomato/ai/domain/protocol/model/valobj/enums/ProtocolStatusEnum.java` | 协议状态枚举（ENABLE/DISABLE） |
| `ai-mcp-gateway-domain/src/main/java/cn/tomato/ai/domain/protocol/adapter/repository/IProtocolRepository.java` | 仓储接口（domain 侧端口） |
| `ai-mcp-gateway-infrastructure/src/main/java/cn/tomato/ai/infrastructure/adapter/repository/ProtocolRepository.java` | 仓储实现（@Transactional，循环 insert） |
| `ai-mcp-gateway-app/src/test/java/cn/tomato/ai/test/domain/protocol/ProtocolStorageTest.java` | `@SpringBootTest` 集成测试 |

### 3.3 不动

- `HTTPProtocolVO` 及其内嵌 `ProtocolMapping`（1-15 已就绪，结构与参考一致）
- `IMcpProtocolHttpDao` / `IMcpProtocolMappingDao` / 对应 PO
- `mcp_protocol_http_mapper.xml` / `mcp_protocol_mapping_mapper.xml`（insert 已含 `useGeneratedKeys="true" keyProperty="id"`，但本设计不依赖自增 ID，使用业务 `protocolId` 关联）
- `ai-mcp-gateway-app/src/test/resources/swagger/api-docs-test03.json`（已存在）

## 4. 接口契约

### 4.1 `IProtocolStorage`

```java
package cn.tomato.ai.domain.protocol.service;

import cn.tomato.ai.domain.protocol.model.entity.StorageCommandEntity;
import java.util.List;

public interface IProtocolStorage {
    List<Long> doStorage(StorageCommandEntity commandEntity);
}
```

**入参**：`StorageCommandEntity`，含 `List<HTTPProtocolVO> httpProtocolVOS`。
**返回**：`List<Long>` —— 每个 `HTTPProtocolVO` 对应一个新生成的 8 位 `protocolId`（顺序与输入列表一致）。
**异常**：仓储层抛出的 `Exception` 透传（受 `@Transactional` 保护，整体回滚）。

### 4.2 `IProtocolRepository`

```java
package cn.tomato.ai.domain.protocol.adapter.repository;

import cn.tomato.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import java.util.List;

public interface IProtocolRepository {
    List<Long> saveHttpProtocolAndMapping(List<HTTPProtocolVO> httpProtocolVOS);
}
```

### 4.3 `StorageCommandEntity`

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageCommandEntity {
    /** 协议列表数据 */
    private List<HTTPProtocolVO> httpProtocolVOS;
}
```

**字段命名注意**：使用 `httpProtocolVOS`（带尾随 S），与参考一致，对应 getter `getHttpProtocolVOS()`。

### 4.4 `ProtocolStatusEnum`

```java
package cn.tomato.ai.domain.protocol.model.valobj.enums;

import cn.tomato.ai.types.enums.ResponseCode;
import cn.tomato.ai.types.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

参照 `AuthStatusEnum.AuthConfig` 风格，自带 `get(Integer code)` 工厂方法，code 未匹配抛 `AppException`。

## 5. 实现细节

### 5.1 `ProtocolStorage`（domain @Service，纯代理）

```java
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

无日志、无 try/catch —— 异常透传，由 `@Transactional` 保证回滚。

### 5.2 `ProtocolRepository`（infrastructure @Repository，事务核心）

```java
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

            // 0. 生成 8 位数字协议ID
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

**关键决策**：
- **`protocolId` 生成**：`RandomStringUtils.randomNumeric(8)` 生成 8 位数字字符串，`Long.parseLong` 转 long。与参考完全一致（不引入 ID 生成器/雪花算法，简化设计）。
- **硬编码值**：`retryTimes = 3`（无配置项）；`status = ProtocolStatusEnum.ENABLE.getCode()`（新协议默认启用）。
- **空 mapping 跳过**：`mappings == null || mappings.isEmpty()` 时 `continue`，但 `protocolId` 仍加入返回列表 —— 协议 HTTP 配置仍落库，mapping 可后补。
- **非 batch insert**：每行单 insert，MyBatis mapper 已就绪。
- **不写 `mcp_gateway_tool`**：参考项目同样不写；工具行创建是独立的"工具注册"流程，对应其他 issue。
- **`@Transactional` 位置**：仓储 impl 上（不在 domain service），与 `AuthRepository` 风格一致。`rollbackFor = Exception.class` 覆盖所有异常。

### 5.3 依赖确认

- `commons-lang3` 3.9：父 pom `dependencyManagement` 统一管理，`ai-mcp-gateway-domain` 显式声明，`ai-mcp-gateway-infrastructure` 通过 `ai-mcp-gateway-domain` 传递依赖获得 `RandomStringUtils`，无需额外引入。

## 6. 集成测试

`ProtocolStorageTest`（`@SpringBootTest` + `@RunWith(SpringRunner.class)`）：

```java
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProtocolStorageTest {

    @Value("classpath:swagger/api-docs-test03.json")
    private Resource apiDocs;

    @Resource private IProtocolAnalysis protocolAnalysis;
    @Resource private IProtocolStorage protocolStorage;

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

**断言策略**：参考项目测试不写断言（仅日志打印结果），靠人工核对日志确认。本设计沿用 —— 与参考一致，避免过度设计。

**前置条件**：测试需连真实 MySQL（`application-dev.yml` 中 `ai_mcp_gateway_v2` 库）。本地无 DB 时该测试失败是预期，需补 mock 或跳过。

## 7. 错误处理

| 场景 | 行为 |
|---|---|
| 输入 `httpProtocolVOS == null` | `for` 循环不执行，返回 `new ArrayList<>()`（空列表） |
| 单个 VO 的 `mappings == null` 或空 | 该 VO 的 HTTP 行仍插入，`protocolId` 加入返回列表；不抛错 |
| DB insert 失败（任一行） | `@Transactional` 整体回滚，异常向上抛至 test 层，事务终止 |
| `RandomStringUtils.randomNumeric(8)` 冲突 | 不做去重检查；冲突概率 ~10⁻⁸，可接受（参考同样不处理） |
| 参数 `httpProtocolVOS` 为 null | 不做空校验直接 NPE（与参考一致；调用方负责） |

无新增 `ResponseCode`；如需可在后续 issue 补充（参考项目也未新增）。

## 8. 不在范围（后续 issue）

- **生产调用方**：无 trigger/case/api 入口，仅集成测试。后续"工具注册"或"admin 后台" issue 引入。
- **`mcp_gateway_tool` 写入**：参考项目同样不写。工具行创建是独立流程。
- **批量 insert 优化**：当前 N+1 次 insert。数据量大时改 `<foreach>` 批量插入。
- **`protocolId` 唯一性约束**：当前依赖 8 位随机数无碰撞，DB 无 `UNIQUE` 约束。如加需配合重试。
- **HTTP 协议更新/删除**：`IMcpProtocolHttpDao.updateById` / `deleteById` 已存在但本设计不暴露。

## 9. 验证清单

- [ ] `mvn clean install -DskipTests` 编译通过
- [ ] `ProtocolStorageTest.test_storage` 在 dev 环境执行成功（日志中可见解析列表 + 8 位 `protocolId` 列表）
- [ ] 数据库 `mcp_protocol_http` 新增 1 行（`http_url` / `http_method` / `timeout` / `retry_times=3` / `status=1` 正确）
- [ ] 数据库 `mcp_protocol_mapping` 新增 N 行（`protocol_id` 与上一步一致，`mapping_type` / `mcp_path` / `sort_order` 正确）
- [ ] 任何一行 insert 失败时，前序所有插入回滚（手动模拟验证一次）

## 10. 参考对照

| 参考项目（`ai-mcp-gateway-01`） | 本项目（`ai-mcp-gateway`） | 差异 |
|---|---|---|
| `cn.bugstack.ai.domain.protocol.service.IProtocolStorage` | `cn.tomato.ai.domain.protocol.service.IProtocolStorage` | 包名 |
| `cn.bugstack.ai.domain.protocol.service.storage.ProtocolStorage` | `cn.tomato.ai.domain.protocol.service.storage.ProtocolStorage` | 包名 |
| `cn.bugstack.ai.domain.protocol.model.entity.StorageCommandEntity` | `cn.tomato.ai.domain.protocol.model.entity.StorageCommandEntity` | 包名 |
| `cn.bugstack.ai.domain.protocol.adapter.repository.IProtocolRepository` | `cn.tomato.ai.domain.protocol.adapter.repository.IProtocolRepository` | 包名 |
| `cn.bugstack.ai.domain.protocol.model.valobj.enums.ProtocolStatusEnum` | `cn.tomato.ai.domain.protocol.model.valobj.enums.ProtocolStatusEnum` | 包名 |
| `cn.bugstack.ai.infrastructure.adapter.repository.ProtocolRepository` | `cn.tomato.ai.infrastructure.adapter.repository.ProtocolRepository` | 包名 |
| `cn.bugstack.ai.test.domain.protocol.ProtocolStorageTest` | `cn.tomato.ai.test.domain.protocol.ProtocolStorageTest` | 包名 |

类名、方法名、字段名、事务边界、日志策略、异常处理 —— 全部 1:1 移植，无任何业务/结构调整。
