package cn.tomato.ai.trigger.http;

import cn.tomato.ai.api.dto.GatewayAuthDTO;
import cn.tomato.ai.api.dto.GatewayAuthQueryDTO;
import cn.tomato.ai.api.dto.GatewayConfigDTO;
import cn.tomato.ai.api.dto.GatewayConfigQueryDTO;
import cn.tomato.ai.api.dto.GatewayConfigRequestDTO;
import cn.tomato.ai.api.dto.GatewayConfigResponseDTO;
import cn.tomato.ai.api.dto.GatewayProtocolDTO;
import cn.tomato.ai.api.dto.GatewayProtocolQueryDTO;
import cn.tomato.ai.api.dto.GatewayToolConfigDTO;
import cn.tomato.ai.api.dto.GatewayToolQueryDTO;
import cn.tomato.ai.api.response.Response;
import cn.tomato.ai.api.response.ResponsePage;
import cn.tomato.ai.cases.admin.IAdminAuthService;
import cn.tomato.ai.cases.admin.IAdminGatewayService;
import cn.tomato.ai.cases.admin.IAdminManageService;
import cn.tomato.ai.cases.admin.IAdminProtocolService;
import cn.tomato.ai.domain.admin.model.entity.GatewayAuthConfigEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayAuthPageEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayAuthQueryEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayConfigEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayConfigPageEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayConfigQueryEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayProtocolConfigEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayProtocolPageEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayProtocolQueryEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayToolConfigEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayToolPageEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayToolQueryEntity;
import cn.tomato.ai.domain.auth.model.entity.RegisterCommandEntity;
import cn.tomato.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;
import cn.tomato.ai.domain.gateway.model.entity.GatewayToolConfigCommandEntity;
import cn.tomato.ai.domain.gateway.model.valobj.GatewayConfigVO;
import cn.tomato.ai.domain.gateway.model.valobj.GatewayToolConfigVO;
import cn.tomato.ai.domain.protocol.model.entity.AnalysisCommandEntity;
import cn.tomato.ai.domain.protocol.model.entity.StorageCommandEntity;
import cn.tomato.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import cn.tomato.ai.types.enums.GatewayEnum;
import cn.tomato.ai.types.enums.ResponseCode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理端 Controller
 * HTTP 边界：DTO ↔ CommandEntity / Entity 翻译, 委派给 case 层
 *
 * @author Wxh
 * @date 2026-06-12
 */
@Slf4j
@RestController
@RequestMapping("/admin/")
@CrossOrigin(origins = "*")
public class AdminController {

    @Resource private IAdminGatewayService adminGatewayService;
    @Resource private IAdminProtocolService adminProtocolService;
    @Resource private IAdminAuthService adminAuthService;
    @Resource private IAdminManageService adminManageService;

    @PostMapping("save_gateway_config")
    public Response<GatewayConfigResponseDTO> saveGatewayConfig(@RequestBody GatewayConfigRequestDTO request) {
        try {
            GatewayConfigRequestDTO.GatewayConfig dto = request.getGatewayConfig();
            log.info("保存网关配置开始 gatewayId: {}", dto.getGatewayId());

            GatewayConfigCommandEntity commandEntity = GatewayConfigCommandEntity.builder()
                    .gatewayConfigVO(GatewayConfigVO.builder()
                            .gatewayId(dto.getGatewayId())
                            .gatewayName(dto.getName())
                            .gatewayDesc(dto.getDesc())
                            .version(dto.getVersion())
                            .auth(null != dto.getAuth() ? GatewayEnum.GatewayAuthStatusEnum.getByCode(dto.getAuth()) : null)
                            .status(null != dto.getStatus() ? GatewayEnum.GatewayStatus.get(dto.getStatus()) : null)
                            .build())
                    .build();
            adminGatewayService.saveGatewayConfig(commandEntity);

            log.info("保存网关配置完成 gatewayId: {}", dto.getGatewayId());
            return success();
        } catch (Exception e) {
            log.error("保存网关配置失败", e);
            return unError();
        }
    }

    @PostMapping("save_gateway_tool_config")
    public Response<GatewayConfigResponseDTO> saveGatewayToolConfig(@RequestBody GatewayConfigRequestDTO request) {
        try {
            GatewayConfigRequestDTO.GatewayToolConfig dto = request.getGatewayToolConfig();
            log.info("保存网关工具配置开始 gatewayId: {} toolId: {}", dto.getGatewayId(), dto.getToolId());

            GatewayToolConfigCommandEntity commandEntity = GatewayToolConfigCommandEntity.builder()
                    .gatewayToolConfigVO(GatewayToolConfigVO.builder()
                            .gatewayId(dto.getGatewayId())
                            .toolId(dto.getToolId())
                            .toolName(dto.getToolName())
                            .toolType(dto.getToolType())
                            .toolDescription(dto.getToolDescription())
                            .toolVersion(dto.getToolVersion())
                            .protocolId(dto.getProtocolId())
                            .protocolType(dto.getProtocolType())
                            .build())
                    .build();
            adminGatewayService.saveGatewayToolConfig(commandEntity);

            log.info("保存网关工具配置完成 gatewayId: {}", dto.getGatewayId());
            return success();
        } catch (Exception e) {
            log.error("保存网关工具配置失败", e);
            return unError();
        }
    }

    @PostMapping("save_gateway_protocol")
    public Response<GatewayConfigResponseDTO> saveGatewayProtocol(@RequestBody GatewayConfigRequestDTO request) {
        try {
            GatewayConfigRequestDTO.GatewayProtocol dto = request.getGatewayProtocol();
            log.info("保存网关协议配置开始 gatewayId: {}", dto.getGatewayId());

            StorageCommandEntity commandEntity = new StorageCommandEntity();
            if (dto.getHttp() != null) {
                HTTPProtocolVO vo = new HTTPProtocolVO();
                vo.setGatewayId(dto.getGatewayId());
                GatewayConfigRequestDTO.HTTPProtocol http = dto.getHttp();
                vo.setHttpUrl(http.getHttpUrl());
                vo.setHttpHeaders(http.getHttpHeaders());
                vo.setHttpMethod(http.getHttpMethod());
                vo.setTimeout(http.getTimeout());
                vo.setRetryTimes(http.getRetryTimes());
                vo.setStatus(http.getStatus());
                if (dto.getMapping() != null) {
                    vo.setMappings(dto.getMapping().stream()
                            .map(this::convertMapping)
                            .collect(Collectors.toList()));
                } else {
                    vo.setMappings(Collections.emptyList());
                }
                commandEntity.setHttpProtocolVOS(Collections.singletonList(vo));
            }
            adminProtocolService.saveGatewayProtocol(commandEntity);

            log.info("保存网关协议配置完成 gatewayId: {}", dto.getGatewayId());
            return success();
        } catch (Exception e) {
            log.error("保存网关协议配置失败", e);
            return unError();
        }
    }

    @PostMapping("save_gateway_auth")
    public Response<GatewayConfigResponseDTO> saveGatewayAuth(@RequestBody GatewayConfigRequestDTO request) {
        try {
            GatewayConfigRequestDTO.GatewayAuth dto = request.getGatewayAuth();
            log.info("保存网关auth认证开始 gatewayId: {}", dto.getGatewayId());

            RegisterCommandEntity commandEntity = RegisterCommandEntity.builder()
                    .gatewayId(dto.getGatewayId())
                    .rateLimit(dto.getRateLimit())
                    .expireTime(dto.getExpireTime())
                    .build();
            String apiKey = adminAuthService.saveGatewayAuth(commandEntity);

            log.info("保存网关auth认证完成 gatewayId: {}", dto.getGatewayId());
            return Response.<GatewayConfigResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(GatewayConfigResponseDTO.builder().success(true).apiKey(apiKey).build())
                    .build();
        } catch (Exception e) {
            log.error("保存网关auth认证失败 gatewayId: {}", request.getGatewayAuth().getGatewayId(), e);
            return unError();
        }
    }

    @PostMapping("query_gateway_config_list")
    public Response<List<GatewayConfigDTO>> queryGatewayConfigList() {
        try {
            log.info("查询网关配置列表开始");
            List<GatewayConfigEntity> entities = adminManageService.queryGatewayConfigList();
            List<GatewayConfigDTO> dtoList = entities.stream()
                    .map(this::convert)
                    .collect(Collectors.toList());
            log.info("查询网关配置列表完成 count: {}", dtoList.size());
            return Response.<List<GatewayConfigDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(dtoList)
                    .build();
        } catch (Exception e) {
            log.error("查询网关配置列表失败", e);
            return Response.<List<GatewayConfigDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping("query_gateway_config_page")
    public ResponsePage<List<GatewayConfigDTO>> queryGatewayConfigPage(@RequestBody GatewayConfigQueryDTO query) {
        try {
            log.info("分页查询网关配置开始 page:{} rows:{} gatewayId:{} gatewayName:{}",
                    query.getPage(), query.getRows(), query.getGatewayId(), query.getGatewayName());
            GatewayConfigQueryEntity q = GatewayConfigQueryEntity.builder()
                    .gatewayId(query.getGatewayId())
                    .gatewayName(query.getGatewayName())
                    .page(query.getPage() != null ? query.getPage() : 1)
                    .rows(query.getRows() != null ? query.getRows() : 10)
                    .build();
            GatewayConfigPageEntity pageEntity = adminManageService.queryGatewayConfigPage(q);
            List<GatewayConfigDTO> dtoList = pageEntity.getDataList().stream()
                    .map(this::convert)
                    .collect(Collectors.toList());
            log.info("分页查询网关配置完成 total:{} count:{}", pageEntity.getTotal(), dtoList.size());
            return ResponsePage.<List<GatewayConfigDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(dtoList)
                    .total(pageEntity.getTotal())
                    .build();
        } catch (Exception e) {
            log.error("分页查询网关配置失败", e);
            return ResponsePage.<List<GatewayConfigDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping("query_gateway_tool_list")
    public Response<List<GatewayToolConfigDTO>> queryGatewayToolList() {
        try {
            log.info("查询所有工具开始");
            List<GatewayToolConfigDTO> dtoList = adminManageService.queryGatewayToolList().stream()
                    .map(this::convertTool)
                    .collect(Collectors.toList());
            log.info("查询所有工具完成 count:{}", dtoList.size());
            return Response.<List<GatewayToolConfigDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(dtoList)
                    .build();
        } catch (Exception e) {
            log.error("查询所有工具失败", e);
            return Response.<List<GatewayToolConfigDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping("query_gateway_tool_page")
    public ResponsePage<List<GatewayToolConfigDTO>> queryGatewayToolPage(@RequestBody GatewayToolQueryDTO query) {
        try {
            log.info("分页查询工具开始 page:{} rows:{} gatewayId:{} toolName:{} toolId:{}",
                    query.getPage(), query.getRows(), query.getGatewayId(), query.getToolName(), query.getToolId());
            GatewayToolQueryEntity q = GatewayToolQueryEntity.builder()
                    .gatewayId(query.getGatewayId())
                    .toolName(query.getToolName())
                    .toolId(query.getToolId())
                    .page(query.getPage() != null ? query.getPage() : 1)
                    .rows(query.getRows() != null ? query.getRows() : 10)
                    .build();
            GatewayToolPageEntity pageEntity = adminManageService.queryGatewayToolPage(q);
            List<GatewayToolConfigDTO> dtoList = pageEntity.getDataList().stream()
                    .map(this::convertTool)
                    .collect(Collectors.toList());
            log.info("分页查询工具完成 total:{} count:{}", pageEntity.getTotal(), dtoList.size());
            return ResponsePage.<List<GatewayToolConfigDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(dtoList)
                    .total(pageEntity.getTotal())
                    .build();
        } catch (Exception e) {
            log.error("分页查询工具失败", e);
            return ResponsePage.<List<GatewayToolConfigDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping("query_gateway_tool_list_by_gateway_id")
    public Response<List<GatewayToolConfigDTO>> queryGatewayToolListByGatewayId(@RequestBody java.util.Map<String, String> body) {
        try {
            String gatewayId = body.get("gatewayId");
            log.info("按网关ID查询工具开始 gatewayId:{}", gatewayId);
            List<GatewayToolConfigDTO> dtoList = adminManageService.queryGatewayToolListByGatewayId(gatewayId).stream()
                    .map(this::convertTool)
                    .collect(Collectors.toList());
            log.info("按网关ID查询工具完成 gatewayId:{} count:{}", gatewayId, dtoList.size());
            return Response.<List<GatewayToolConfigDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(dtoList)
                    .build();
        } catch (Exception e) {
            log.error("按网关ID查询工具失败", e);
            return Response.<List<GatewayToolConfigDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping("delete_gateway_tool_config")
    public Response<Boolean> deleteGatewayToolConfig(@RequestBody java.util.Map<String, Object> body) {
        try {
            String gatewayId = (String) body.get("gatewayId");
            Object rawToolId = body.get("toolId");
            Long toolId = rawToolId instanceof Number ? ((Number) rawToolId).longValue() : Long.parseLong(String.valueOf(rawToolId));
            log.info("删除工具开始 gatewayId:{} toolId:{}", gatewayId, toolId);
            boolean ok = adminManageService.deleteGatewayTool(gatewayId, toolId);
            log.info("删除工具完成 gatewayId:{} toolId:{} result:{}", gatewayId, toolId, ok);
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(ok)
                    .build();
        } catch (Exception e) {
            log.error("删除工具失败", e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    @PostMapping("query_gateway_auth_list")
    public Response<List<GatewayAuthDTO>> queryGatewayAuthList() {
        try {
            log.info("查询所有鉴权开始");
            List<GatewayAuthDTO> dtoList = adminManageService.queryGatewayAuthList().stream()
                    .map(this::convertAuth)
                    .collect(Collectors.toList());
            log.info("查询所有鉴权完成 count:{}", dtoList.size());
            return Response.<List<GatewayAuthDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(dtoList)
                    .build();
        } catch (Exception e) {
            log.error("查询所有鉴权失败", e);
            return Response.<List<GatewayAuthDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping("query_gateway_auth_page")
    public ResponsePage<List<GatewayAuthDTO>> queryGatewayAuthPage(@RequestBody GatewayAuthQueryDTO query) {
        try {
            log.info("分页查询鉴权开始 page:{} rows:{} gatewayId:{}",
                    query.getPage(), query.getRows(), query.getGatewayId());
            GatewayAuthQueryEntity q = GatewayAuthQueryEntity.builder()
                    .gatewayId(query.getGatewayId())
                    .page(query.getPage() != null ? query.getPage() : 1)
                    .rows(query.getRows() != null ? query.getRows() : 10)
                    .build();
            GatewayAuthPageEntity pageEntity = adminManageService.queryGatewayAuthPage(q);
            List<GatewayAuthDTO> dtoList = pageEntity.getDataList().stream()
                    .map(this::convertAuth)
                    .collect(Collectors.toList());
            log.info("分页查询鉴权完成 total:{} count:{}", pageEntity.getTotal(), dtoList.size());
            return ResponsePage.<List<GatewayAuthDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(dtoList)
                    .total(pageEntity.getTotal())
                    .build();
        } catch (Exception e) {
            log.error("分页查询鉴权失败", e);
            return ResponsePage.<List<GatewayAuthDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping("delete_gateway_auth")
    public Response<Boolean> deleteGatewayAuth(@RequestBody java.util.Map<String, String> body) {
        try {
            String gatewayId = body.get("gatewayId");
            log.info("删除鉴权开始 gatewayId:{}", gatewayId);
            boolean ok = adminManageService.deleteGatewayAuth(gatewayId);
            log.info("删除鉴权完成 gatewayId:{} result:{}", gatewayId, ok);
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(ok)
                    .build();
        } catch (Exception e) {
            log.error("删除鉴权失败", e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    @PostMapping("query_gateway_protocol_list")
    public Response<List<GatewayProtocolDTO>> queryGatewayProtocolList() {
        try {
            log.info("查询所有协议开始");
            List<GatewayProtocolDTO> dtoList = adminManageService.queryGatewayProtocolList().stream()
                    .map(this::convertProtocol)
                    .collect(Collectors.toList());
            log.info("查询所有协议完成 count:{}", dtoList.size());
            return Response.<List<GatewayProtocolDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(dtoList)
                    .build();
        } catch (Exception e) {
            log.error("查询所有协议失败", e);
            return Response.<List<GatewayProtocolDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping("query_gateway_protocol_page")
    public ResponsePage<List<GatewayProtocolDTO>> queryGatewayProtocolPage(@RequestBody GatewayProtocolQueryDTO query) {
        try {
            log.info("分页查询协议开始 page:{} rows:{} protocolId:{} gatewayId:{} httpUrl:{}",
                    query.getPage(), query.getRows(), query.getProtocolId(), query.getGatewayId(), query.getHttpUrl());
            GatewayProtocolQueryEntity q = GatewayProtocolQueryEntity.builder()
                    .protocolId(query.getProtocolId())
                    .gatewayId(query.getGatewayId())
                    .httpUrl(query.getHttpUrl())
                    .page(query.getPage() != null ? query.getPage() : 1)
                    .rows(query.getRows() != null ? query.getRows() : 10)
                    .build();
            GatewayProtocolPageEntity pageEntity = adminManageService.queryGatewayProtocolPage(q);
            List<GatewayProtocolDTO> dtoList = pageEntity.getDataList().stream()
                    .map(this::convertProtocol)
                    .collect(Collectors.toList());
            log.info("分页查询协议完成 total:{} count:{}", pageEntity.getTotal(), dtoList.size());
            return ResponsePage.<List<GatewayProtocolDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(dtoList)
                    .total(pageEntity.getTotal())
                    .build();
        } catch (Exception e) {
            log.error("分页查询协议失败", e);
            return ResponsePage.<List<GatewayProtocolDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping("query_gateway_protocol_list_by_gateway_id")
    public Response<List<GatewayProtocolDTO>> queryGatewayProtocolListByGatewayId(@RequestBody java.util.Map<String, String> body) {
        try {
            String gatewayId = body.get("gatewayId");
            log.info("按网关ID查询协议开始 gatewayId:{}", gatewayId);
            // 先取工具 → 提取 protocolId → 查协议
            List<Long> protocolIds = adminManageService.queryGatewayToolListByGatewayId(gatewayId).stream()
                    .map(GatewayToolConfigEntity::getProtocolId)
                    .filter(java.util.Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            List<GatewayProtocolDTO> dtoList = adminManageService.queryGatewayProtocolListByProtocolIds(protocolIds).stream()
                    .map(this::convertProtocol)
                    .collect(Collectors.toList());
            log.info("按网关ID查询协议完成 gatewayId:{} count:{}", gatewayId, dtoList.size());
            return Response.<List<GatewayProtocolDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(dtoList)
                    .build();
        } catch (Exception e) {
            log.error("按网关ID查询协议失败", e);
            return Response.<List<GatewayProtocolDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping("delete_gateway_protocol")
    public Response<Boolean> deleteGatewayProtocol(@RequestBody java.util.Map<String, Object> body) {
        try {
            Object rawPid = body.get("protocolId");
            Long protocolId = rawPid instanceof Number ? ((Number) rawPid).longValue() : Long.parseLong(String.valueOf(rawPid));
            log.info("删除协议开始 protocolId:{}", protocolId);
            boolean ok = adminManageService.deleteGatewayProtocol(protocolId);
            log.info("删除协议完成 protocolId:{} result:{}", protocolId, ok);
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(ok)
                    .build();
        } catch (Exception e) {
            log.error("删除协议失败", e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    @PostMapping("analysis_protocol")
    public Response<List<GatewayProtocolDTO>> analysisProtocol(@RequestBody GatewayConfigRequestDTO request) {
        try {
            GatewayConfigRequestDTO.GatewayProtocolImport dto = request.getGatewayProtocolImport();
            log.info("解析 Swagger 协议开始 gatewayId:{} length:{}",
                    dto.getGatewayId(),
                    dto.getOpenApiJson() == null ? 0 : dto.getOpenApiJson().length());

            AnalysisCommandEntity cmd = new AnalysisCommandEntity();
            cmd.setOpenApiJson(dto.getOpenApiJson());
            List<HTTPProtocolVO> vos = adminProtocolService.analysisProtocol(cmd);

            // 注入 gatewayId 到每条 VO（保存前需要）
            if (dto.getGatewayId() != null) {
                for (HTTPProtocolVO vo : vos) {
                    vo.setGatewayId(dto.getGatewayId());
                }
            }

            List<GatewayProtocolDTO> dtoList = vos.stream()
                    .map(this::convertHttpVoToDto)
                    .collect(Collectors.toList());
            log.info("解析 Swagger 协议完成 count:{}", dtoList.size());
            return Response.<List<GatewayProtocolDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(dtoList)
                    .build();
        } catch (Exception e) {
            log.error("解析 Swagger 协议失败", e);
            return Response.<List<GatewayProtocolDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping("import_gateway_protocol")
    public Response<GatewayConfigResponseDTO> importGatewayProtocol(@RequestBody GatewayConfigRequestDTO request) {
        try {
            GatewayConfigRequestDTO.GatewayProtocolImport dto = request.getGatewayProtocolImport();
            log.info("导入网关协议开始 gatewayId:{} endpoints:{}",
                    dto.getGatewayId(),
                    dto.getEndpoints() == null ? 0 : dto.getEndpoints().size());

            // 1. 解析 swagger
            AnalysisCommandEntity acmd = new AnalysisCommandEntity();
            acmd.setOpenApiJson(dto.getOpenApiJson());
            List<HTTPProtocolVO> allVos = adminProtocolService.analysisProtocol(acmd);

            // 2. 过滤 endpoints 选中项（null/empty → 全部导入）
            List<HTTPProtocolVO> selected = allVos.stream()
                    .filter(v -> dto.getEndpoints() == null
                            || dto.getEndpoints().isEmpty()
                            || dto.getEndpoints().contains(v.getHttpUrl()))
                    .collect(Collectors.toList());

            // 3. 注入 gatewayId 到每条 VO
            if (dto.getGatewayId() != null) {
                for (HTTPProtocolVO vo : selected) {
                    vo.setGatewayId(dto.getGatewayId());
                }
            }

            // 4. 批量落库
            StorageCommandEntity scmd = new StorageCommandEntity();
            scmd.setHttpProtocolVOS(selected);
            adminProtocolService.saveGatewayProtocol(scmd);

            log.info("导入网关协议完成 gatewayId:{} saved:{}", dto.getGatewayId(), selected.size());
            return success();
        } catch (Exception e) {
            log.error("导入网关协议失败", e);
            return unError();
        }
    }

    // === DTO ↔ Entity 翻译辅助 ===

    /** HTTPProtocolVO → GatewayProtocolDTO（VO→DTO 翻译，解析预览/导入共用） */
    private GatewayProtocolDTO convertHttpVoToDto(HTTPProtocolVO vo) {
        List<GatewayProtocolDTO.ProtocolMappingDTO> mappings = vo.getMappings() == null
                ? Collections.emptyList()
                : vo.getMappings().stream().map(m -> GatewayProtocolDTO.ProtocolMappingDTO.builder()
                        .mappingType(m.getMappingType())
                        .parentPath(m.getParentPath())
                        .fieldName(m.getFieldName())
                        .mcpPath(m.getMcpPath())
                        .mcpType(m.getMcpType())
                        .mcpDesc(m.getMcpDesc())
                        .isRequired(m.getIsRequired())
                        .sortOrder(m.getSortOrder())
                        .build()).collect(Collectors.toList());
        return GatewayProtocolDTO.builder()
                .httpUrl(vo.getHttpUrl())
                .httpMethod(vo.getHttpMethod())
                .httpHeaders(vo.getHttpHeaders())
                .timeout(vo.getTimeout())
                .retryTimes(vo.getRetryTimes())
                .status(vo.getStatus())
                .mappings(mappings)
                .build();
    }

    private HTTPProtocolVO.ProtocolMapping convertMapping(GatewayConfigRequestDTO.ProtocolMapping src) {
        return HTTPProtocolVO.ProtocolMapping.builder()
                .mappingType(src.getMappingType())
                .parentPath(src.getParentPath())
                .fieldName(src.getFieldName())
                .mcpPath(src.getMcpPath())
                .mcpType(src.getMcpType())
                .mcpDesc(src.getMcpDesc())
                .isRequired(src.getIsRequired())
                .sortOrder(src.getSortOrder())
                .build();
    }

    private GatewayConfigDTO convert(GatewayConfigEntity entity) {
        return GatewayConfigDTO.builder()
                .gatewayId(entity.getGatewayId())
                .name(entity.getName())
                .desc(entity.getDesc())
                .version(entity.getVersion())
                .auth(null != entity.getAuth() ? entity.getAuth().name() : null)
                .status(null != entity.getStatus() ? entity.getStatus().name() : null)
                .build();
    }

    private GatewayToolConfigDTO convertTool(GatewayToolConfigEntity entity) {
        return GatewayToolConfigDTO.builder()
                .gatewayId(entity.getGatewayId())
                .toolId(entity.getToolId())
                .toolName(entity.getToolName())
                .toolType(entity.getToolType())
                .toolDescription(entity.getToolDescription())
                .toolVersion(entity.getToolVersion())
                .protocolId(entity.getProtocolId())
                .protocolType(entity.getProtocolType())
                .build();
    }

    private GatewayAuthDTO convertAuth(GatewayAuthConfigEntity entity) {
        return GatewayAuthDTO.builder()
                .gatewayId(entity.getGatewayId())
                .apiKey(entity.getApiKey())
                .rateLimit(entity.getRateLimit())
                .expireTime(entity.getExpireTime())
                .build();
    }

    private GatewayProtocolDTO convertProtocol(GatewayProtocolConfigEntity entity) {
        List<GatewayProtocolDTO.ProtocolMappingDTO> mappings = entity.getMappings() == null ? Collections.emptyList() :
                entity.getMappings().stream().map(m -> GatewayProtocolDTO.ProtocolMappingDTO.builder()
                        .mappingType(m.getMappingType())
                        .parentPath(m.getParentPath())
                        .fieldName(m.getFieldName())
                        .mcpPath(m.getMcpPath())
                        .mcpType(m.getMcpType())
                        .mcpDesc(m.getMcpDesc())
                        .isRequired(m.getIsRequired())
                        .sortOrder(m.getSortOrder())
                        .build()).collect(Collectors.toList());
        return GatewayProtocolDTO.builder()
                .protocolId(entity.getProtocolId())
                .httpUrl(entity.getHttpUrl())
                .httpMethod(entity.getHttpMethod())
                .httpHeaders(entity.getHttpHeaders())
                .timeout(entity.getTimeout())
                .retryTimes(entity.getRetryTimes())
                .status(entity.getStatus())
                .mappings(mappings)
                .build();
    }

    private Response<GatewayConfigResponseDTO> success() {
        return Response.<GatewayConfigResponseDTO>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info(ResponseCode.SUCCESS.getInfo())
                .data(GatewayConfigResponseDTO.builder().success(true).build())
                .build();
    }

    private Response<GatewayConfigResponseDTO> unError() {
        return Response.<GatewayConfigResponseDTO>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info(ResponseCode.UN_ERROR.getInfo())
                .build();
    }

}
