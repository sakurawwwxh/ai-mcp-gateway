import { X } from 'lucide-react';
import { useAuthList } from '../api/hooks/useGatewayApi';
import type { GatewayConfigDTO } from '../api/schemas/gateway';
import { CopyButton } from './ui/CopyButton';
import { Spinner } from './ui/Spinner';
import { Badge } from './ui/Badge';

interface Props {
  gateway: GatewayConfigDTO | null;
  onClose: () => void;
}

/** 网关详情 — 鉴权配置(apiKey + 复制) */
export function GatewayAuthDialog({ gateway, onClose }: Props) {
  const auths = useAuthList();
  if (!gateway) return null;

  const item = (auths.data ?? []).find((a) => a.gatewayId === gateway.gatewayId);

  return (
    <div
      className="fixed inset-0 z-50 bg-black/40 flex items-center justify-center"
      onClick={onClose}
    >
      <div
        className="bg-[var(--bg-surface)] rounded-lg shadow-xl w-[560px] max-h-[80vh] flex flex-col"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="px-6 py-4 border-b border-[var(--border-default)] flex items-center justify-between">
          <div>
            <h3 className="text-lg font-semibold text-[var(--text-primary)]">网关「{gateway.name}」认证配置</h3>
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

        <div className="p-6 space-y-4">
          {auths.isLoading && <Spinner size="sm" />}
          {!auths.isLoading && !item && (
            <p className="text-sm text-[var(--text-secondary)] text-center py-6">该网关未配置鉴权</p>
          )}
          {item && (
            <>
              <Row label="API Key">
                <div className="flex items-center gap-2 flex-1">
                  <code className="flex-1 font-mono text-xs break-all bg-[var(--bg-sunken)] px-3 py-2 rounded border border-[var(--border-default)]">
                    {item.apiKey || '—'}
                  </code>
                  {item.apiKey && <CopyButton value={item.apiKey} />}
                </div>
              </Row>
              <Row label="速率限制"><Badge variant="info">{item.rateLimit} 次/小时</Badge></Row>
              <Row label="过期时间"><span className="text-sm text-[var(--text-primary)]">{item.expireTime ?? '永久'}</span></Row>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

function Row({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="flex items-start gap-4">
      <div className="w-24 shrink-0 text-sm text-[var(--text-secondary)] pt-1.5">{label}</div>
      <div className="flex-1">{children}</div>
    </div>
  );
}
