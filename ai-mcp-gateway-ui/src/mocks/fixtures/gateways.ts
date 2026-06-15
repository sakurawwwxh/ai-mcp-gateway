import type { GatewayConfigDTO } from '../../api/hooks/useGatewayApi';

export const mockGateways: GatewayConfigDTO[] = [
  { gatewayId: 'user-gw', name: '用户网关', desc: '用户域统一入口', version: 'v1.2.0', auth: 'ENABLE', status: 'STRONG_VERIFIED' },
  { gatewayId: 'order-gw', name: '订单网关', desc: '订单服务聚合', version: 'v1.0.3', auth: 'ENABLE', status: 'NOT_VERIFIED' },
  { gatewayId: 'payment-gw', name: '支付网关', desc: '支付通道抽象', version: 'v2.0.0', auth: 'ENABLE', status: 'STRONG_VERIFIED' },
  { gatewayId: 'inventory-gw', name: '库存网关', desc: '库存查询与扣减', version: 'v1.5.2', auth: 'DISABLE', status: 'NOT_VERIFIED' },
  { gatewayId: 'notify-gw', name: '通知网关', desc: '短信/邮件/推送', version: 'v0.9.1', auth: 'DISABLE', status: 'NOT_VERIFIED' },
];
