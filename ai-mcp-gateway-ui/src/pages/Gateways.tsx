import { RefreshCw } from 'lucide-react';
import { useGatewayList } from '../api/hooks/useGatewayApi';
import { gatewayColumns } from '../api/gateway-badges';
import { PageHeader } from '../components/ui/PageHeader';
import { Card } from '../components/ui/Card';
import { Table } from '../components/ui/Table';
import { Button } from '../components/ui/Button';

export default function Gateways() {
  const { data: list = [], isLoading, isError, refetch, error, isFetching } = useGatewayList();

  return (
    <div>
      <PageHeader
        title="网关列表"
        subtitle="已注册的所有网关"
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

      <Card padding="none">
        <Table
          columns={gatewayColumns}
          data={list}
          rowKey={(g) => g.gatewayId}
          loading={isLoading}
          error={isError ? (error as Error)?.message ?? '加载失败' : null}
          onRetry={() => refetch()}
          emptyTitle="暂无网关"
          emptyDesc="还没有任何网关,请到「基础配置」创建"
        />
      </Card>
    </div>
  );
}
