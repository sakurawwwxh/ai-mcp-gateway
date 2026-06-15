package cn.tomato.ai.trigger.http;

import cn.tomato.ai.api.dto.GatewayConfigDTO;
import cn.tomato.ai.api.dto.GatewayConfigRequestDTO;
import cn.tomato.ai.api.dto.GatewayConfigResponseDTO;
import cn.tomato.ai.api.response.Response;
import cn.tomato.ai.cases.admin.IAdminAuthService;
import cn.tomato.ai.cases.admin.IAdminGatewayService;
import cn.tomato.ai.cases.admin.IAdminManageService;
import cn.tomato.ai.cases.admin.IAdminProtocolService;
import cn.tomato.ai.domain.admin.model.entity.GatewayConfigEntity;
import cn.tomato.ai.domain.auth.model.entity.RegisterCommandEntity;
import cn.tomato.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;
import cn.tomato.ai.domain.gateway.model.entity.GatewayToolConfigCommandEntity;
import cn.tomato.ai.domain.gateway.model.valobj.GatewayConfigVO;
import cn.tomato.ai.domain.gateway.model.valobj.GatewayToolConfigVO;
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

    // === DTO ↔ Entity 翻译辅助 ===

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
