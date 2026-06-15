import { describe, it, expect } from 'vitest';
import {
  GatewayConfigSchema,
  GatewayToolConfigSchema,
  GatewayProtocolSchema,
  GatewayAuthSchema,
  GatewayConfigDTOSchema,
} from './gateway';

describe('GatewayConfigSchema', () => {
  it('accepts valid gateway config', () => {
    const r = GatewayConfigSchema.safeParse({
      gatewayId: 'user-gw',
      name: '用户网关',
      desc: '用户域',
      version: '1.0.0',
      auth: 1,
      status: 1,
    });
    expect(r.success).toBe(true);
  });

  it('rejects invalid auth code', () => {
    const r = GatewayConfigSchema.safeParse({
      gatewayId: 'a', name: '名称', version: '1.0.0', auth: 5, status: 1,
    });
    expect(r.success).toBe(false);
  });

  it('rejects invalid status code', () => {
    const r = GatewayConfigSchema.safeParse({
      gatewayId: 'a', name: '名称', version: '1.0.0', auth: 1, status: 9,
    });
    expect(r.success).toBe(false);
  });

  it('rejects bad gatewayId (uppercase)', () => {
    const r = GatewayConfigSchema.safeParse({
      gatewayId: 'User-GW', name: '名称', version: '1.0.0', auth: 1, status: 1,
    });
    expect(r.success).toBe(false);
  });

  it('applies defaults', () => {
    const r = GatewayConfigSchema.parse({
      gatewayId: 'a-b-c', name: '名称', version: '1.0.0', auth: 1, status: 1,
    });
    expect(r.desc).toBe('');
    expect(r.version).toBe('1.0.0');
  });
});

describe('GatewayToolConfigSchema', () => {
  it('coerces toolId string to number', () => {
    const r = GatewayToolConfigSchema.safeParse({
      gatewayId: 'gw1', toolId: '42', toolName: '搜索', toolType: 'function',
      toolVersion: '1.0.0', protocolId: '', protocolType: 'HTTP',
    });
    expect(r.success).toBe(true);
    if (r.success) {
      expect(r.data.toolId).toBe(42);
      expect(r.data.protocolId).toBeUndefined();
    }
  });
});

describe('GatewayProtocolSchema', () => {
  it('validates URL', () => {
    const r = GatewayProtocolSchema.safeParse({
      gatewayId: 'gw1',
      http: { httpUrl: 'not-a-url', httpMethod: 'GET', httpHeaders: '{}', timeout: 5000 },
      mapping: [],
    });
    expect(r.success).toBe(false);
  });

  it('accepts full mapping with isRequired 0/1', () => {
    const r = GatewayProtocolSchema.safeParse({
      gatewayId: 'gw1',
      http: { httpUrl: 'https://api.example.com', httpMethod: 'POST', httpHeaders: '{}', timeout: 3000 },
      mapping: [{
        mappingType: 'query', fieldName: 'id', mcpPath: '/user.id',
        mcpType: 'string', mcpDesc: '用户ID', isRequired: 1, sortOrder: 1,
      }],
    });
    expect(r.success).toBe(true);
  });
});

describe('GatewayAuthSchema', () => {
  it('coerces rateLimit string to number', () => {
    const r = GatewayAuthSchema.safeParse({
      gatewayId: 'gw1', rateLimit: '100', expireTime: '2026-12-31T23:59:59',
    });
    expect(r.success).toBe(true);
    if (r.success) expect(r.data.rateLimit).toBe(100);
  });

  it('rejects rateLimit < 1', () => {
    const r = GatewayAuthSchema.safeParse({
      gatewayId: 'gw1', rateLimit: 0, expireTime: '2026-12-31T23:59:59',
    });
    expect(r.success).toBe(false);
  });
});

describe('GatewayConfigDTOSchema (list response)', () => {
  it('accepts enum name strings from backend', () => {
    const r = GatewayConfigDTOSchema.safeParse({
      gatewayId: 'gw1', name: 'X', desc: '', version: '1.0.0',
      auth: 'ENABLE', status: 'STRONG_VERIFIED',
    });
    expect(r.success).toBe(true);
  });
});
