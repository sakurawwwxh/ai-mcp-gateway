import { useState } from 'react';
import { X, ChevronDown, ChevronRight } from 'lucide-react';
import { useToolByGateway, useProtocolByGateway } from '../api/hooks/useGatewayApi';
import type { GatewayConfigDTO, GatewayProtocolDTO } from '../api/schemas/gateway';
import { Badge } from './ui/Badge';
import { Spinner } from './ui/Spinner';

interface Props {
  gateway: GatewayConfigDTO | null;
  onClose: () => void;
}

/** 网关详情 — 关联工具 + 协议 mappings 折叠 */
export function GatewayToolsDialog({ gateway, onClose }: Props) {
  const tools     = useToolByGateway(gateway?.gatewayId ?? '');
  const protocols = useProtocolByGateway(gateway?.gatewayId ?? '');
  const [expanded, setExpanded] = useState<Set<number>>(new Set());

  if (!gateway) return null;

  // protocolId → protocol 索引(展开映射用)
  const protoMap = new Map<number, GatewayProtocolDTO>();
  (protocols.data ?? []).forEach((p) => p.protocolId != null && protoMap.set(p.protocolId, p));

  function toggle(toolId: number) {
    setExpanded((prev) => {
      const n = new Set(prev);
      n.has(toolId) ? n.delete(toolId) : n.add(toolId);
      return n;
    });
  }

  return (
    <div
      className="fixed inset-0 z-50 bg-black/40 flex items-center justify-center"
      onClick={onClose}
    >
      <div
        className="bg-[var(--bg-surface)] rounded-lg shadow-xl w-[860px] max-h-[80vh] flex flex-col"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="px-6 py-4 border-b border-[var(--border-default)] flex items-center justify-between">
          <div>
            <h3 className="text-lg font-semibold text-[var(--text-primary)]">网关「{gateway.name}」关联工具</h3>
            <p className="text-xs text-[var(--text-tertiary)] mt-0.5 font-mono">{gateway.gatewayId}</p>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="text-[var(--text-tertiary)] hover:text-[var(--text-primary)]"
            aria-label="关闭"
          >
            <X size={20} />
          </button>
        </div>

        <div className="flex-1 overflow-auto p-6 space-y-2">
          {(tools.isLoading || protocols.isLoading) && (
            <div className="flex items-center gap-2 text-sm text-[var(--text-secondary)]">
              <Spinner size="sm" /> 加载中…
            </div>
          )}
          {tools.isError && (
            <div className="text-sm text-[var(--danger)]">
              加载失败:{(tools.error as Error)?.message}
            </div>
          )}
          {!tools.isLoading && (tools.data?.length ?? 0) === 0 && (
            <p className="text-sm text-[var(--text-secondary)] text-center py-8">该网关暂无关联工具</p>
          )}

          {(tools.data ?? []).map((t) => {
            const proto = t.protocolId != null ? protoMap.get(t.protocolId) : undefined;
            const open = expanded.has(t.toolId);
            return (
              <div key={t.toolId} className="border border-[var(--border-default)] rounded-md overflow-hidden">
                <button
                  type="button"
                  onClick={() => proto && toggle(t.toolId)}
                  className="w-full flex items-center gap-3 px-4 py-3 hover:bg-[var(--bg-sunken)] text-left disabled:cursor-default"
                  disabled={!proto}
                >
                  {proto
                    ? (open ? <ChevronDown size={16} /> : <ChevronRight size={16} />)
                    : <span className="w-4" />}
                  <span className="font-mono text-xs text-[var(--text-secondary)] w-20 shrink-0">#{t.toolId}</span>
                  <span className="font-medium flex-1 text-[var(--text-primary)]">{t.toolName}</span>
                  <Badge variant={t.toolType === 'function' ? 'info' : 'neutral'}>{t.toolType}</Badge>
                  {proto && <Badge variant="success">{proto.httpMethod}</Badge>}
                  <code className="text-xs text-[var(--text-tertiary)] max-w-[280px] truncate">
                    {proto?.httpUrl ?? '无协议'}
                  </code>
                </button>
                {open && proto && (
                  <div className="px-4 py-3 border-t border-[var(--border-default)] bg-[var(--bg-sunken)]">
                    <p className="text-xs text-[var(--text-secondary)] mb-2">
                      参数映射 ({proto.mappings.length} 条)
                    </p>
                    {proto.mappings.length === 0 ? (
                      <p className="text-xs text-[var(--text-tertiary)]">无映射</p>
                    ) : (
                      <table className="w-full text-xs">
                        <thead>
                          <tr className="text-left text-[var(--text-secondary)]">
                            <th className="py-1">类型</th>
                            <th>父路径</th>
                            <th>字段</th>
                            <th>MCP 路径</th>
                            <th>MCP 类型</th>
                            <th>必填</th>
                          </tr>
                        </thead>
                        <tbody>
                          {proto.mappings.map((m, i) => (
                            <tr key={i} className="border-t border-[var(--border-default)]">
                              <td className="py-1"><Badge variant="neutral">{m.mappingType}</Badge></td>
                              <td className="font-mono">{m.parentPath || '—'}</td>
                              <td className="font-mono">{m.fieldName}</td>
                              <td className="font-mono">{m.mcpPath}</td>
                              <td>{m.mcpType}</td>
                              <td>{m.isRequired === 1 ? '是' : '否'}</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    )}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
