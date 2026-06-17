import { useQuery, useMutation, useQueryClient, type UseMutationResult } from '@tanstack/react-query';
import { client, ApiError } from '../client';
import { ENDPOINTS } from '../endpoints';
import type {
  GatewayConfigInput,
  GatewayToolConfigInput,
  GatewayProtocolInput,
  GatewayAuthInput,
  GatewayConfigDTO,
  SaveGatewayResult,
  GatewayConfigQueryInput,
  PageResult,
  GatewayToolConfigDTO,
  GatewayToolQueryInput,
  GatewayAuthDTO,
  GatewayAuthQueryInput,
  GatewayProtocolDTO,
  GatewayProtocolQueryInput,
} from '../schemas/gateway';

export type { GatewayConfigDTO, SaveGatewayResult, GatewayToolConfigDTO, GatewayAuthDTO, GatewayProtocolDTO };

/** 解包: interceptor 已返回 res.data.data，这里直接返回结果 */
async function unwrap<T>(p: Promise<T>): Promise<T> {
  return p;
}

/* === 查询 === */

export function useGatewayList() {
  return useQuery<GatewayConfigDTO[], ApiError>({
    queryKey: ['gateways'],
    queryFn: () => unwrap(client.post(ENDPOINTS.queryGatewayConfigList, {})),
  });
}

export function useGatewayPage(query: GatewayConfigQueryInput) {
  return useQuery<PageResult<GatewayConfigDTO>, ApiError>({
    queryKey: ['gateways', 'page', query],
    queryFn: () => unwrap(client.post(ENDPOINTS.queryGatewayConfigPage, query)),
  });
}

/* === 工具 === */

export function useToolList() {
  return useQuery<GatewayToolConfigDTO[], ApiError>({
    queryKey: ['tools'],
    queryFn: () => unwrap(client.post(ENDPOINTS.queryGatewayToolList, {})),
  });
}

export function useToolPage(query: GatewayToolQueryInput) {
  return useQuery<PageResult<GatewayToolConfigDTO>, ApiError>({
    queryKey: ['tools', 'page', query],
    queryFn: () => unwrap(client.post(ENDPOINTS.queryGatewayToolPage, query)),
  });
}

export function useToolByGateway(gatewayId: string) {
  return useQuery<GatewayToolConfigDTO[], ApiError>({
    queryKey: ['tools', 'byGateway', gatewayId],
    queryFn: () => unwrap(client.post(ENDPOINTS.queryGatewayToolByGatewayId, { gatewayId })),
    enabled: !!gatewayId,
  });
}

export function useDeleteTool() {
  const qc = useQueryClient();
  return useMutation<boolean, ApiError, { gatewayId: string; toolId: number }>({
    mutationFn: (v) => unwrap(client.post(ENDPOINTS.deleteGatewayTool, v)),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['tools'] }); },
  });
}

/* === 鉴权 === */

export function useAuthList() {
  return useQuery<GatewayAuthDTO[], ApiError>({
    queryKey: ['auths'],
    queryFn: () => unwrap(client.post(ENDPOINTS.queryGatewayAuthList, {})),
  });
}

export function useAuthPage(query: GatewayAuthQueryInput) {
  return useQuery<PageResult<GatewayAuthDTO>, ApiError>({
    queryKey: ['auths', 'page', query],
    queryFn: () => unwrap(client.post(ENDPOINTS.queryGatewayAuthPage, query)),
  });
}

export function useDeleteAuth() {
  const qc = useQueryClient();
  return useMutation<boolean, ApiError, { gatewayId: string }>({
    mutationFn: (v) => unwrap(client.post(ENDPOINTS.deleteGatewayAuth, v)),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['auths'] }); },
  });
}

/* === 协议 === */

export function useProtocolList() {
  return useQuery<GatewayProtocolDTO[], ApiError>({
    queryKey: ['protocols'],
    queryFn: () => unwrap(client.post(ENDPOINTS.queryGatewayProtocolList, {})),
  });
}

export function useProtocolPage(query: GatewayProtocolQueryInput) {
  return useQuery<PageResult<GatewayProtocolDTO>, ApiError>({
    queryKey: ['protocols', 'page', query],
    queryFn: () => unwrap(client.post(ENDPOINTS.queryGatewayProtocolPage, query)),
  });
}

export function useProtocolByGateway(gatewayId: string) {
  return useQuery<GatewayProtocolDTO[], ApiError>({
    queryKey: ['protocols', 'byGateway', gatewayId],
    queryFn: () => unwrap(client.post(ENDPOINTS.queryGatewayProtocolByGatewayId, { gatewayId })),
    enabled: !!gatewayId,
  });
}

export function useDeleteProtocol() {
  const qc = useQueryClient();
  return useMutation<boolean, ApiError, { protocolId: number }>({
    mutationFn: (v) => unwrap(client.post(ENDPOINTS.deleteGatewayProtocol, v)),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['protocols'] }); },
  });
}

/* === 变更 === */

type WrapperKey = 'gatewayConfig' | 'gatewayToolConfig' | 'gatewayProtocol' | 'gatewayAuth';

const INVALIDATE_MAP: Record<WrapperKey, string[]> = {
  gatewayConfig:     ['gateways'],
  gatewayToolConfig: ['tools'],
  gatewayProtocol:   ['protocols'],
  gatewayAuth:       ['auths'],
};

function useSaveMutation<TIn, TData = SaveGatewayResult>(
  endpoint: string,
  wrapper: WrapperKey,
): UseMutationResult<TData, ApiError, TIn> {
  const qc = useQueryClient();
  const keys = INVALIDATE_MAP[wrapper];
  return useMutation<TData, ApiError, TIn>({
    mutationFn: async (dto) =>
      unwrap(client.post(endpoint, { [wrapper]: dto })) as Promise<TData>,
    onSuccess: () => { keys.forEach((k) => qc.invalidateQueries({ queryKey: [k] })); },
  });
}

export function useSaveGatewayConfig() {
  return useSaveMutation<GatewayConfigInput>(ENDPOINTS.saveGatewayConfig, 'gatewayConfig');
}

export function useSaveGatewayTool() {
  return useSaveMutation<GatewayToolConfigInput>(ENDPOINTS.saveGatewayToolConfig, 'gatewayToolConfig');
}

export function useSaveGatewayProtocol() {
  return useSaveMutation<GatewayProtocolInput>(ENDPOINTS.saveGatewayProtocol, 'gatewayProtocol');
}

export function useSaveGatewayAuth() {
  return useSaveMutation<GatewayAuthInput>(ENDPOINTS.saveGatewayAuth, 'gatewayAuth');
}

/* === Swagger 协议导入 === */

/** 解析 Swagger，预览接口列表（不落库） */
export function useAnalysisProtocol() {
  return useMutation<GatewayProtocolDTO[], ApiError, { gatewayId: string; openApiJson: string }>({
    mutationFn: (v) => unwrap(client.post(ENDPOINTS.analysisProtocol, { gatewayProtocolImport: v })),
  });
}

/** 批量导入选中接口 */
export function useImportProtocol() {
  const qc = useQueryClient();
  return useMutation<SaveGatewayResult, ApiError, { gatewayId: string; openApiJson: string; endpoints: string[] }>({
    mutationFn: (v) => unwrap(client.post(ENDPOINTS.importGatewayProtocol, { gatewayProtocolImport: v })),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['protocols'] }); },
  });
}
