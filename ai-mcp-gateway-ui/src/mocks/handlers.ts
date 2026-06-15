import { http, HttpResponse } from 'msw';
import { mockGateways } from './fixtures/gateways';

const ok = <T,>(data: T) => HttpResponse.json({ code: '0000', info: '成功', data });

export const handlers = [
  /* 1) 列表 */
  http.post('/api-gateway/admin/query_gateway_config_list', () => ok(mockGateways)),

  /* 2) 网关基础配置 */
  http.post('/api-gateway/admin/save_gateway_config', async ({ request }) => {
    const body = (await request.json()) as { gatewayConfig?: any };
    const g = body?.gatewayConfig;
    if (g) {
      const idx = mockGateways.findIndex((x) => x.gatewayId === g.gatewayId);
      const dto = {
        gatewayId: g.gatewayId,
        name: g.name ?? '',
        desc: g.desc ?? '',
        version: g.version ?? '1.0.0',
        auth: g.auth === 1 ? 'STRONG_VERIFIED' : 'NOT_VERIFIED',
        status: g.status === 1 ? 'ENABLE' : 'DISABLE',
      };
      if (idx >= 0) mockGateways[idx] = dto; else mockGateways.push(dto);
    }
    return ok({ success: true });
  }),

  /* 3) 工具配置 */
  http.post('/api-gateway/admin/save_gateway_tool_config', () => ok({ success: true })),

  /* 4) 协议配置 */
  http.post('/api-gateway/admin/save_gateway_protocol', () => ok({ success: true })),

  /* 5) 认证配置 — 返回模拟 apiKey */
  http.post('/api-gateway/admin/save_gateway_auth', () => {
    const apiKey = 'mock_key_' + Array.from(crypto.getRandomValues(new Uint8Array(12)))
      .map((b) => b.toString(16).padStart(2, '0')).join('');
    return ok({ success: true, apiKey });
  }),
];
