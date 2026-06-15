import { useGatewayList } from '../api/hooks/useGatewayApi';
import { gatewayColumns } from '../api/gateway-badges';
import { PageHeader } from '../components/ui/PageHeader';
import { Card } from '../components/ui/Card';
import { Table } from '../components/ui/Table';
import { Spinner } from '../components/ui/Spinner';

/** KPI 指标卡片 */
function KPI({ label, value, loading }: { label: string; value: number; loading?: boolean }) {
  return (
    <div className="bg-[var(--bg-surface)] border border-[var(--border-default)] rounded-lg p-5">
      <div className="text-[11px] font-semibold uppercase tracking-wider text-[var(--text-tertiary)]">
        {label}
      </div>
      <div className="mt-2 text-3xl font-bold text-[var(--text-primary)] leading-none">
        {loading ? <span className="inline-block w-10 h-7 bg-[var(--bg-sunken)] rounded animate-pulse" /> : value}
      </div>
    </div>
  );
}

export default function Dashboard() {
  const { data: list = [], isLoading, isError, refetch, error } = useGatewayList();

  const total       = list.length;
  const strong      = list.filter((g) => g.auth === 'STRONG_VERIFIED').length;
  const notVerified = list.filter((g) => g.auth === 'NOT_VERIFIED').length;
  const enabled     = list.filter((g) => g.status === 'ENABLE').length;

  return (
    <div>
      <PageHeader
        title="控制台"
        subtitle="网关总览与最近状态"
        actions={isLoading ? <Spinner size="sm" /> : null}
      />

      <div className="grid grid-cols-4 gap-4 mb-6">
        <KPI label="网关总数" value={total}       loading={isLoading} />
        <KPI label="启用"     value={enabled}     loading={isLoading} />
        <KPI label="强校验"   value={strong}      loading={isLoading} />
        <KPI label="不校验"   value={notVerified} loading={isLoading} />
      </div>

      <Card title="最近网关" subtitle="前 10 条" padding="none">
        <Table
          columns={gatewayColumns}
          data={list.slice(0, 10)}
          rowKey={(g) => g.gatewayId}
          loading={isLoading}
          error={isError ? (error as Error)?.message ?? '加载失败' : null}
          onRetry={() => refetch()}
          emptyTitle="暂无网关"
          emptyDesc="请先到「基础配置」创建一个网关"
        />
      </Card>
    </div>
  );
}
