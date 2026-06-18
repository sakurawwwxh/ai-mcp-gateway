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
    timeout: intCoerce.refine((n) => n >= 100 && n <= 60_000, '100-60000ms').default(30000),
    retryTimes: intCoerce.refine((n) => n >= 0 && n <= 10, '0-10').default(0),
    status: intEnum([0, 1]).default(1),
  }),
  mapping: z.array(ProtocolMappingSchema).default([]),
});
export type GatewayProtocolInput = z.infer<typeof GatewayProtocolSchema>;

export const GatewayAuthSchema = z.object({
  gatewayId: z.string().min(1),
  rateLimit: intCoerce.refine((n) => n >= 1, '至少 1').default(1000),
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

/* === 分页基类（必须先定义，下方 extend 都依赖） === */

export const PageQuerySchema = z.object({
  page: z.number().int().min(1).default(1),
  rows: z.number().int().min(1).max(100).default(10),
});
export type PageQueryInput = z.infer<typeof PageQuerySchema>;

export const GatewayConfigQuerySchema = PageQuerySchema.extend({
  gatewayId: z.string().optional().default(''),
  gatewayName: z.string().optional().default(''),
});
export type GatewayConfigQueryInput = z.infer<typeof GatewayConfigQuerySchema>;

export interface PageResult<T> {
  data: T[];
  total: number;
}

/* === 工具列表 DTO === */

export const GatewayToolConfigDTOSchema = z.object({
  gatewayId: z.string(),
  toolId: z.number(),
  toolName: z.string(),
  toolType: z.string(),
  toolDescription: z.string().nullish(),
  toolVersion: z.string(),
  protocolId: z.number().nullish(),
  protocolType: z.string().nullish(),
});
export type GatewayToolConfigDTO = z.infer<typeof GatewayToolConfigDTOSchema>;

export const GatewayToolQuerySchema = PageQuerySchema.extend({
  gatewayId: z.string().optional().default(''),
  toolName: z.string().optional().default(''),
  toolId: z.string().optional().default(''),
});
export type GatewayToolQueryInput = z.infer<typeof GatewayToolQuerySchema>;

/* === 鉴权列表 DTO === */

export const GatewayAuthDTOSchema = z.object({
  gatewayId: z.string(),
  apiKey: z.string().nullish(),
  rateLimit: z.number(),
  expireTime: z.string().nullish(),
});
export type GatewayAuthDTO = z.infer<typeof GatewayAuthDTOSchema>;

export const GatewayAuthQuerySchema = PageQuerySchema.extend({
  gatewayId: z.string().optional().default(''),
});
export type GatewayAuthQueryInput = z.infer<typeof GatewayAuthQuerySchema>;

/* === 协议列表 DTO === */

export const ProtocolMappingDTOSchema = z.object({
  mappingType: z.string(),
  parentPath: z.string().nullish(),
  fieldName: z.string(),
  mcpPath: z.string(),
  mcpType: z.string(),
  mcpDesc: z.string().nullish(),
  isRequired: z.number(),
  sortOrder: z.number(),
});
export type ProtocolMappingDTO = z.infer<typeof ProtocolMappingDTOSchema>;

export const GatewayProtocolDTOSchema = z.object({
  protocolId: z.number().nullish(),
  httpUrl: z.string(),
  httpMethod: z.string(),
  httpHeaders: z.string().nullish(),
  timeout: z.number().nullish(),
  retryTimes: z.number().nullish(),
  status: z.number().nullish(),
  mappings: z.array(ProtocolMappingDTOSchema).default([]),
});
export type GatewayProtocolDTO = z.infer<typeof GatewayProtocolDTOSchema>;

export const GatewayProtocolQuerySchema = PageQuerySchema.extend({
  protocolId: z.union([z.literal(''), z.coerce.number().int().positive()]).optional(),
  gatewayId: z.string().optional().default(''),
  httpUrl: z.string().optional().default(''),
});
export type GatewayProtocolQueryInput = z.infer<typeof GatewayProtocolQuerySchema>;

/* === Swagger 协议导入 === */

export const GatewayProtocolImportSchema = z.object({
  gatewayId: z.string().min(1, '请填写归属网关'),
  openApiJson: z.string().min(2, '请粘贴 Swagger JSON'),
  endpoints: z.array(z.string()).optional(),
});
export type GatewayProtocolImportInput = z.infer<typeof GatewayProtocolImportSchema>;

/* === LLM 对接测试网关 === */

export const GatewayLLMRequestSchema = z.object({
  gatewayId: z.string().min(1, '请填写网关 ID'),
  message: z.string().min(1, '请输入测试消息'),
  authApiKey: z.string().optional().default(''),
  timeout: intCoerce.refine((n) => n >= 1000 && n <= 300_000, '1000-300000ms').default(60000),
  reload: z.boolean().default(false),
});
export type GatewayLLMRequestInput = z.infer<typeof GatewayLLMRequestSchema>;

export const GatewayLLMResponseSchema = z.object({ content: z.string() });
export type GatewayLLMResponse = z.infer<typeof GatewayLLMResponseSchema>;
