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
} from '../schemas/gateway';

export type { GatewayConfigDTO, SaveGatewayResult };

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

/* === 变更 === */

type WrapperKey = 'gatewayConfig' | 'gatewayToolConfig' | 'gatewayProtocol' | 'gatewayAuth';

function useSaveMutation<TIn, TData = SaveGatewayResult>(
  endpoint: string,
  wrapper: WrapperKey,
): UseMutationResult<TData, ApiError, TIn> {
  const qc = useQueryClient();
  return useMutation<TData, ApiError, TIn>({
    mutationFn: async (dto) =>
      unwrap(client.post(endpoint, { [wrapper]: dto })) as Promise<TData>,
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['gateways'] }); },
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
