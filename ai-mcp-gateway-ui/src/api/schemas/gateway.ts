import { z } from 'zod';

/** 后端枚举 code (0/1) 辅助 */
const intEnum = (codes: readonly number[]) =>
  z.number().int().refine((n) => codes.includes(n), { message: '枚举值非法' });

const longCoerce = z.union([z.string(), z.number()]).transform((v, ctx) => {
  const n = typeof v === 'number' ? v : Number(v);
  if (!Number.isFinite(n) || !Number.isInteger(n) || n < 0) {
    ctx.addIssue({ code: z.ZodIssueCode.custom, message: '必须为非负整数' });
    return z.NEVER;
  }
  return n;
});

const intCoerce = z.union([z.string(), z.number()]).transform((v, ctx) => {
  const n = typeof v === 'number' ? v : Number(v);
  if (!Number.isFinite(n) || !Number.isInteger(n)) {
    ctx.addIssue({ code: z.ZodIssueCode.custom, message: '必须为整数' });
    return z.NEVER;
  }
  return n;
});

/* === POST 请求体 (内层) — 与后端 DTO Integer code 对齐 === */

export const GatewayConfigSchema = z.object({
  gatewayId: z.string().regex(/^[a-z][a-z0-9-]{2,30}$/, '标识:小写字母/数字/中划线,3-31 字符'),
  name: z.string().min(2, '名称至少 2 字符').max(40, '名称最多 40 字符'),
  desc: z.string().max(200).optional().default(''),
  version: z.string().min(1).max(20).default('1.0.0'),
  auth: intEnum([0, 1]).default(1),
  status: intEnum([0, 1]).default(1),
});
export type GatewayConfigInput = z.infer<typeof GatewayConfigSchema>;

export const GatewayToolConfigSchema = z.object({
  gatewayId: z.string().min(1, '必填'),
  toolId: longCoerce.refine((n) => n > 0, { message: '必须大于 0' }),
  toolName: z.string().min(2, '至少 2 字符').max(80),
  toolType: z.string().min(1),
  toolDescription: z.string().max(500).optional().default(''),
  toolVersion: z.string().min(1).max(20).default('1.0.0'),
  protocolId: z.union([z.literal(''), longCoerce]).transform((v) => (v === '' ? undefined : v)).optional(),
  protocolType: z.string().min(1).default('HTTP'),
});
export type GatewayToolConfigInput = z.infer<typeof GatewayToolConfigSchema>;

export const ProtocolMappingSchema = z.object({
  mappingType: z.string().min(1),
  parentPath: z.string().optional().default(''),
  fieldName: z.string().min(1),
  mcpPath: z.string().min(1),
  mcpType: z.string().min(1),
  mcpDesc: z.string().optional().default(''),
  isRequired: intEnum([0, 1]).default(0),
  sortOrder: intCoerce.default(0),
});
export type ProtocolMappingInput = z.infer<typeof ProtocolMappingSchema>;

export const GatewayProtocolSchema = z.object({
  gatewayId: z.string().min(1),
  http: z.object({
    httpUrl: z.string().url('请输入有效 URL'),
    httpMethod: z.enum(['GET', 'POST', 'PUT', 'DELETE']).default('GET'),
    httpHeaders: z.string().default('{}'),
    timeout: intCoerce.refine((n) => n >= 100 && n <= 60_000, '100-60000ms').default(5000),
  }),
  mapping: z.array(ProtocolMappingSchema).default([]),
});
export type GatewayProtocolInput = z.infer<typeof GatewayProtocolSchema>;

export const GatewayAuthSchema = z.object({
  gatewayId: z.string().min(1),
  rateLimit: intCoerce.refine((n) => n >= 1, '至少 1').default(100),
  // datetime-local 输出 YYYY-MM-DDTHH:mm, 后端期望 yyyy-MM-dd'T'HH:mm:ss
  expireTime: z.string().min(1, '请选择过期时间').transform((v) => {
    if (v.length === 16) return v + ':00';
    return v;
  }),
});
export type GatewayAuthInput = z.infer<typeof GatewayAuthSchema>;

/* === 列表响应 (后端返回 enum name 字符串) === */

export const GatewayConfigDTOSchema = z.object({
  gatewayId: z.string(),
  name: z.string(),
  desc: z.string(),
  version: z.string(),
  auth: z.string(),
  status: z.string(),
});
export type GatewayConfigDTO = z.infer<typeof GatewayConfigDTOSchema>;

export const SaveGatewayResultSchema = z.object({
  success: z.boolean(),
  apiKey: z.string().optional(),
});
export type SaveGatewayResult = z.infer<typeof SaveGatewayResultSchema>;
