import { RefreshCw, Search } from 'lucide-react';
import { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useGatewayPage } from '../api/hooks/useGatewayApi';
import { makeGatewayColumns } from '../api/gateway-badges';
import { PageHeader } from '../components/ui/PageHeader';
import { Card } from '../components/ui/Card';
import { Table } from '../components/ui/Table';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Pagination } from '../components/ui/Pagination';
import { GatewayToolsDialog } from '../components/GatewayToolsDialog';
import { GatewayAuthDialog } from '../components/GatewayAuthDialog';
import { useToast } from '../lib/toast';
import type { GatewayConfigDTO } from '../api/schemas/gateway';

export default function Gateways() {
  const toast = useToast();
  const nav = useNavigate();
  const [params, setParams] = useSearchParams();

  const page  = Number(params.get('page')  ?? 1) || 1;
  const rows  = Number(params.get('rows')  ?? 20) || 20;
  const [gatewayId, setGatewayId] = useState(params.get('gatewayId') ?? '');
  const [gatewayName, setGatewayName] = useState(params.get('gatewayName') ?? '');
  const [search, setSearch] = useState({
    gatewayId: params.get('gatewayId') ?? '',
    gatewayName: params.get('gatewayName') ?? '',
  });
  const [viewToolsOf, setViewToolsOf] = useState<GatewayConfigDTO | null>(null);
  const [viewAuthOf, setViewAuthOf] = useState<GatewayConfigDTO | null>(null);

  // URL → 搜索值同步(浏览器后退/分享链接)
  useEffect(() => {
    setSearch({
      gatewayId: params.get('gatewayId') ?? '',
      gatewayName: params.get('gatewayName') ?? '',
    });
  }, [params]);

  const { data, isLoading, isError, refetch, error, isFetching } = useGatewayPage({
    page,
    rows,
    gatewayId: search.gatewayId,
    gatewayName: search.gatewayName,
  });

  const list = data?.data ?? [];
  const total = data?.total ?? 0;

  const syncUrl = (next: Partial<{ page: number; rows: number; gatewayId: string; gatewayName: string }>) => {
    const p = new URLSearchParams(params);
    if (next.page !== undefined)      next.page  > 1 ? p.set('page', String(next.page))  : p.delete('page');
    if (next.rows !== undefined)      next.rows !== 20 ? p.set('rows', String(next.rows)) : p.delete('rows');
    if (next.gatewayId   !== undefined) next.gatewayId   ? p.set('gatewayId',   next.gatewayId)   : p.delete('gatewayId');
    if (next.gatewayName !== undefined) next.gatewayName ? p.set('gatewayName', next.gatewayName) : p.delete('gatewayName');
    setParams(p, { replace: true });
  };

  const onSearch = () => syncUrl({ page: 1, gatewayId: gatewayId.trim(), gatewayName: gatewayName.trim() });
  const onReset = () => {
    setGatewayId('');
    setGatewayName('');
    syncUrl({ page: 1, gatewayId: '', gatewayName: '' });
  };

  async function onCopySse(g: GatewayConfigDTO) {
    const base = window.location.origin;
    const sse = `${base}/api-gateway/mcp/${g.gatewayId}/sse`;
    try {
      await navigator.clipboard.writeText(sse);
      toast.success(`SSE 地址已复制\n${sse}`);
    } catch {
      // 旧浏览器 fallback
      const ta = document.createElement('textarea');
      ta.value = sse;
      document.body.appendChild(ta);
      ta.select();
      try {
        document.execCommand('copy');
        toast.success('SSE 地址已复制');
      } catch {
        toast.error('复制失败,请手动复制');
      } finally {
        document.body.removeChild(ta);
      }
    }
  }

  function onEdit(g: GatewayConfigDTO) {
    nav(`/admin/configs?gatewayId=${encodeURIComponent(g.gatewayId)}`);
  }

  const columns = makeGatewayColumns({
    onViewTools: setViewToolsOf,
    onViewAuth: setViewAuthOf,
    onCopySse,
    onEdit,
  });

  return (
    <div className="space-y-4">
      <PageHeader
        title="网关列表"
        subtitle="分页 + 模糊搜索"
        actions={
          <Button
            variant="secondary"
            size="sm"
            onClick={() => refetch()}
            loading={isFetching}
            leftIcon={<RefreshCw size={14} />}
          >
            刷新
          </Button>
        }
      />

      <Card>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-3 items-end">
          <Input
            label="网关 ID"
            placeholder="支持模糊匹配"
            value={gatewayId}
            onChange={(e) => setGatewayId(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && onSearch()}
          />
          <Input
            label="网关名称"
            placeholder="支持模糊匹配"
            value={gatewayName}
            onChange={(e) => setGatewayName(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && onSearch()}
          />
          <div className="flex gap-2">
            <Button onClick={onSearch} leftIcon={<Search size={14} />}>
              搜索
            </Button>
            <Button variant="secondary" onClick={onReset}>
              重置
            </Button>
          </div>
        </div>
      </Card>

      <Card padding="none">
        <Table
          columns={columns}
          data={list}
          rowKey={(g) => g.gatewayId}
          loading={isLoading}
          error={isError ? (error as Error)?.message ?? '加载失败' : null}
          onRetry={() => refetch()}
          emptyTitle="暂无网关"
          emptyDesc="还没有任何网关,请到「基础配置」创建"
        />
        <Pagination page={page} rows={rows} total={total} onChange={(p) => syncUrl({ page: p })} onRowsChange={(r) => syncUrl({ page: 1, rows: r })} />
      </Card>

      <GatewayToolsDialog gateway={viewToolsOf} onClose={() => setViewToolsOf(null)} />
      <GatewayAuthDialog  gateway={viewAuthOf}  onClose={() => setViewAuthOf(null)} />
    </div>
  );
}
