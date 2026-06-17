import { Network, Wrench, ShieldCheck, FileCode2, RefreshCw } from 'lucide-react';
import { useGatewayList, useToolList, useAuthList, useProtocolList } from '../api/hooks/useGatewayApi';
import { gatewayColumns } from '../api/gateway-badges';
import { PageHeader } from '../components/ui/PageHeader';
import { Card } from '../components/ui/Card';
import { Table } from '../components/ui/Table';
import { Button } from '../components/ui/Button';
import { Spinner } from '../components/ui/Spinner';
import { useAuthStore } from '../stores/auth';

interface KPIProps {
  label: string;
  value: number;
  loading?: boolean;
  icon: React.ReactNode;
  tone: 'blue' | 'emerald' | 'amber' | 'violet';
}

const TONE: Record<KPIProps['tone'], { bg: string; fg: string }> = {
  blue:    { bg: 'bg-blue-50',   fg: 'text-blue-600' },
  emerald: { bg: 'bg-emerald-50', fg: 'text-emerald-600' },
  amber:   { bg: 'bg-amber-50',   fg: 'text-amber-600' },
  violet:  { bg: 'bg-violet-50',  fg: 'text-violet-600' },
};

function KPI({ label, value, loading, icon, tone }: KPIProps) {
  const t = TONE[tone];
  return (
    <div className="bg-[var(--bg-surface)] border border-[var(--border-default)] rounded-lg p-5 flex items-center gap-4">
      <div className={`flex-shrink-0 w-11 h-11 rounded-lg ${t.bg} ${t.fg} flex items-center justify-center`}>
        {icon}
      </div>
      <div className="flex-1 min-w-0">
        <div className="text-[11px] font-semibold uppercase tracking-wider text-[var(--text-tertiary)]">
          {label}
        </div>
        <div className="mt-1 text-[28px] font-bold text-[var(--text-primary)] leading-none tracking-tight">
          {loading ? (
            <span className="inline-block w-12 h-7 bg-[var(--bg-sunken)] rounded animate-pulse" />
          ) : (
            value
          )}
        </div>
      </div>
    </div>
  );
}

export default function Dashboard() {
  const gateways  = useGatewayList();
  const tools     = useToolList();
  const auths     = useAuthList();
  const protocols = useProtocolList();

  const loading = gateways.isLoading || tools.isLoading || auths.isLoading || protocols.isLoading;

  const list = gateways.data ?? [];

  const refetchAll = () => {
    gateways.refetch();
    tools.refetch();
    auths.refetch();
    protocols.refetch();
  };

  return (
    <div>
      <PageHeader
        title="控制台"
        subtitle="网关 / 工具 / 协议 / 鉴权 总览"
        actions={
          <Button
            variant="secondary"
            size="sm"
            onClick={refetchAll}
            loading={loading}
            leftIcon={<RefreshCw size={14} />}
          >
            刷新
          </Button>
        }
      />

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <KPI label="网关总数" value={gateways.data?.length ?? 0}   loading={gateways.isLoading} icon={<Network size={20} />}    tone="blue" />
        <KPI label="工具总数" value={tools.data?.length ?? 0}      loading={tools.isLoading}    icon={<Wrench size={20} />}     tone="emerald" />
        <KPI label="协议总数" value={protocols.data?.length ?? 0}  loading={protocols.isLoading} icon={<FileCode2 size={20} />}  tone="violet" />
        <KPI label="鉴权总数" value={auths.data?.length ?? 0}      loading={auths.isLoading}    icon={<ShieldCheck size={20} />} tone="amber" />
      </div>

      <Card title="系统状态" subtitle="运行环境" className="mb-4">
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
          <Info label="API Base" value={import.meta.env.VITE_API_BASE || '/api-gateway'} />
          <Info label="前端版本" value={__APP_VERSION__} />
          <Info
            label="今日"
            value={new Date().toLocaleDateString('zh-CN', {
              year: 'numeric', month: 'long', day: 'numeric', weekday: 'long',
            })}
          />
          <Info label="当前用户" value={useAuthStore((s) => s.username) ?? '匿名'} />
        </div>
      </Card>

      <Card title="最近网关" subtitle="前 10 条" padding="none">
        <Table
          columns={gatewayColumns}
          data={list.slice(0, 10)}
          rowKey={(g) => g.gatewayId}
          loading={gateways.isLoading}
          error={gateways.isError ? (gateways.error as Error)?.message ?? '加载失败' : null}
          onRetry={() => gateways.refetch()}
          emptyTitle="暂无网关"
          emptyDesc="请先到「基础配置」创建一个网关"
        />
      </Card>

      {loading && (
        <div className="mt-3 flex items-center gap-2 text-xs text-[var(--text-tertiary)]">
          <Spinner size="sm" /> 加载指标中…
        </div>
      )}
    </div>
  );
}

function Info({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <div className="text-[11px] font-semibold uppercase tracking-wider text-[var(--text-tertiary)]">{label}</div>
      <div className="mt-1 font-mono text-sm text-[var(--text-primary)] break-all">{value}</div>
    </div>
  );
}