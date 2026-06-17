import { useState, useRef, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Trash2, RefreshCw, Search, Pencil, Dice5, Plus } from 'lucide-react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  useSaveGatewayTool,
  useToolPage,
  useDeleteTool,
} from '../api/hooks/useGatewayApi';
import { GatewayToolConfigSchema, type GatewayToolConfigInput } from '../api/schemas/gateway';
import { PageHeader } from '../components/ui/PageHeader';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Textarea } from '../components/ui/Textarea';
import { Select } from '../components/ui/Select';
import { Button } from '../components/ui/Button';
import { FormSection } from '../components/ui/FormSection';
import { Table, type Column } from '../components/ui/Table';
import { Badge } from '../components/ui/Badge';
import { Pagination } from '../components/ui/Pagination';
import { ConfirmDialog } from '../components/ui/ConfirmDialog';
import { Modal } from '../components/ui/Modal';
import { useUnsavedGuard } from '../lib/useUnsavedGuard';
import { useToast } from '../lib/toast';
import { ApiError } from '../api/client';
import type { GatewayToolConfigDTO } from '../api/schemas/gateway';

const TYPE_OPTS = [
  { value: 'function',  label: 'function (函数)' },
  { value: 'resource',  label: 'resource (资源)' },
];
const PROTO_OPTS = [
  { value: 'HTTP',     label: 'HTTP' },
  { value: 'DUBBO',    label: 'DUBBO' },
  { value: 'RABBITMQ', label: 'RABBITMQ' },
];

const toolColumns: Column<GatewayToolConfigDTO>[] = [
  { key: 'gw',     header: '网关', width: '120px', render: (t) => <span className="font-mono text-xs">{t.gatewayId}</span> },
  { key: 'tid',    header: 'Tool ID', width: '110px', render: (t) => <span className="font-mono text-xs">{t.toolId}</span> },
  { key: 'name',   header: '工具名称', render: (t) => <span className="font-medium">{t.toolName}</span> },
  { key: 'type',   header: '类型', width: '90px', render: (t) => <Badge variant={t.toolType === 'function' ? 'info' : 'neutral'}>{t.toolType}</Badge> },
  { key: 'ver',    header: '版本', width: '80px', render: (t) => <span className="font-mono text-xs text-[var(--text-secondary)]">{t.toolVersion}</span> },
  { key: 'proto',  header: '协议', width: '90px', render: (t) => <Badge variant="neutral">{t.protocolType ?? '—'}</Badge> },
  { key: 'desc',   header: '描述', render: (t) => <span className="text-[var(--text-secondary)] line-clamp-1">{t.toolDescription || '—'}</span> },
];

const EMPTY: GatewayToolConfigInput = {
  gatewayId: '', toolId: '' as any, toolName: '', toolType: 'function',
  toolDescription: '', toolVersion: '1.0.0', protocolId: '' as any, protocolType: 'HTTP',
};

function generateToolId(): number {
  return Math.floor(10_000_000 + Math.random() * 90_000_000);
}

export default function Tools() {
  const toast = useToast();
  const [params, setParams] = useSearchParams();
  const page  = Number(params.get('page')  ?? 1) || 1;
  const rows  = Number(params.get('rows')  ?? 20) || 20;
  const [gatewayFilter, setGatewayFilter] = useState(params.get('gatewayId') ?? '');
  const [toolNameFilter, setToolNameFilter] = useState(params.get('toolName') ?? '');
  const [toolIdFilter, setToolIdFilter] = useState(params.get('toolId') ?? '');
  const [search, setSearch] = useState({
    gatewayId: params.get('gatewayId') ?? '',
    toolName: params.get('toolName') ?? '',
    toolId: params.get('toolId') ?? '',
  });
  const [pendingDelete, setPendingDelete] = useState<GatewayToolConfigDTO | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<GatewayToolConfigDTO | null>(null);
  const formRef = useRef<HTMLFormElement>(null);

  useEffect(() => {
    setSearch({
      gatewayId: params.get('gatewayId') ?? '',
      toolName: params.get('toolName') ?? '',
      toolId: params.get('toolId') ?? '',
    });
  }, [params]);

  const { data, isLoading, isError, refetch, error, isFetching } = useToolPage({
    page,
    rows,
    gatewayId: search.gatewayId,
    toolName: search.toolName,
    toolId: search.toolId,
  });

  const list = data?.data ?? [];
  const total = data?.total ?? 0;

  const syncUrl = (next: Partial<{ page: number; rows: number; gatewayId: string; toolName: string; toolId: string }>) => {
    const p = new URLSearchParams(params);
    const setOrDel = (k: string, v: string | number | undefined, def: string | number) => {
      if (v === undefined || v === '' || v === def) p.delete(k); else p.set(k, String(v));
    };
    setOrDel('page',     next.page,                            1);
    setOrDel('rows',     next.rows,                            20);
    setOrDel('gatewayId', next.gatewayId,                     '');
    setOrDel('toolName',  next.toolName,                      '');
    setOrDel('toolId',    next.toolId,                        '');
    setParams(p, { replace: true });
  };

  const onSearch = () => syncUrl({
    page: 1,
    gatewayId: gatewayFilter.trim(),
    toolName: toolNameFilter.trim(),
    toolId: toolIdFilter.trim(),
  });
  const onReset = () => {
    setGatewayFilter('');
    setToolNameFilter('');
    setToolIdFilter('');
    syncUrl({ page: 1, gatewayId: '', toolName: '', toolId: '' });
  };

  const deleteTool = useDeleteTool();

  async function confirmDelete() {
    if (!pendingDelete) return;
    try {
      const ok = await deleteTool.mutateAsync({ gatewayId: pendingDelete.gatewayId, toolId: pendingDelete.toolId });
      if (ok) toast.success('删除成功');
      else toast.error('删除失败');
    } catch (e) {
      toast.error(e instanceof ApiError ? e.message : '删除失败');
    } finally {
      setPendingDelete(null);
    }
  }

  const columnsWithAction: Column<GatewayToolConfigDTO>[] = [
    ...toolColumns,
    {
      key: 'act',
      header: '操作',
      width: '110px',
      align: 'center',
      render: (t) => (
        <div className="flex items-center justify-center gap-1">
          <button
            type="button"
            onClick={() => { setEditing(t); setFormOpen(true); }}
            className="inline-flex items-center justify-center w-7 h-7 rounded text-blue-600 hover:bg-blue-50"
            aria-label="编辑"
            title="编辑"
          >
            <Pencil size={15} />
          </button>
          <button
            type="button"
            onClick={() => setPendingDelete(t)}
            className="inline-flex items-center justify-center w-7 h-7 rounded text-red-600 hover:bg-red-50"
            aria-label="删除"
            title="删除"
          >
            <Trash2 size={15} />
          </button>
        </div>
      ),
    },
  ];

  const { register, handleSubmit, formState: { errors, isDirty }, reset, setValue, watch } = useForm<GatewayToolConfigInput>({
    resolver: zodResolver(GatewayToolConfigSchema),
    defaultValues: EMPTY,
  });
  const protocolType = watch('protocolType');
  const save = useSaveGatewayTool();
  const blocker = useUnsavedGuard(formOpen && isDirty);

  function openCreate() {
    setEditing(null);
    reset({ ...EMPTY, toolId: generateToolId() as any });
    setFormOpen(true);
  }

  function openEdit(t: GatewayToolConfigDTO) {
    setEditing(t);
    reset({
      gatewayId: t.gatewayId,
      toolId: t.toolId as any,
      toolName: t.toolName,
      toolType: t.toolType,
      toolDescription: t.toolDescription ?? '',
      toolVersion: t.toolVersion,
      protocolId: t.protocolId ?? ('' as any),
      protocolType: t.protocolType ?? 'HTTP',
    });
  }

  function closeForm() {
    setFormOpen(false);
    setEditing(null);
    reset(EMPTY);
  }

  async function onSubmit(values: GatewayToolConfigInput) {
    try {
      await save.mutateAsync(values);
      toast.success(`工具已保存: ${values.toolName}`);
      closeForm();
      refetch();
    } catch (e) {
      toast.error(e instanceof ApiError ? e.message : '保存失败');
    }
  }

  return (
    <div className="space-y-4">
      <PageHeader
        title="工具配置"
        subtitle="工具列表 + 新增"
        actions={
          <div className="flex items-center gap-2">
            <Button
              variant="primary"
              size="sm"
              onClick={openCreate}
              leftIcon={<Plus size={14} />}
            >
              新增工具
            </Button>
            <Button
              variant="secondary"
              size="sm"
              onClick={() => refetch()}
              loading={isFetching}
              leftIcon={<RefreshCw size={14} />}
            >
              刷新
            </Button>
          </div>
        }
      />

      <Card>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-3 items-end">
          <Input
            label="网关 ID"
            placeholder="精确匹配"
            value={gatewayFilter}
            onChange={(e) => setGatewayFilter(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && onSearch()}
          />
          <Input
            label="工具名称"
            placeholder="支持模糊匹配"
            value={toolNameFilter}
            onChange={(e) => setToolNameFilter(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && onSearch()}
          />
          <Input
            label="Tool ID"
            type="number"
            placeholder="精确匹配"
            value={toolIdFilter}
            onChange={(e) => setToolIdFilter(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && onSearch()}
          />
          <div className="flex gap-2">
            <Button onClick={onSearch} leftIcon={<Search size={14} />}>搜索</Button>
            <Button variant="secondary" onClick={onReset}>重置</Button>
          </div>
        </div>
      </Card>

      <Card padding="none">
        <Table
          columns={columnsWithAction}
          data={list}
          rowKey={(t) => `${t.gatewayId}-${t.toolId}`}
          loading={isLoading}
          error={isError ? (error as Error)?.message ?? '加载失败' : null}
          onRetry={() => refetch()}
          emptyTitle="暂无工具"
          emptyDesc="还没有任何工具"
          emptyAction={
            <Button size="sm" leftIcon={<Plus size={14} />} onClick={openCreate}>
              新增第一条
            </Button>
          }
        />
        <Pagination page={page} rows={rows} total={total} onChange={(p) => syncUrl({ page: p })} onRowsChange={(r) => syncUrl({ page: 1, rows: r })} />
      </Card>

      <Modal
        open={formOpen}
        title={editing ? `修改工具 (ID=${editing.toolId})` : '新增工具'}
        subtitle="归属网关 + 工具元数据"
        width="lg"
        onClose={closeForm}
        footer={
          <>
            <Button variant="secondary" onClick={closeForm}>取消</Button>
            <Button
              onClick={() => formRef.current?.requestSubmit()}
              loading={save.isPending}
            >
              {editing ? '保存修改' : '保存'}
            </Button>
          </>
        }
      >
        <form ref={formRef} onSubmit={handleSubmit(onSubmit)}>
          <FormSection>
            <div className="grid grid-cols-2 gap-4">
              <Input
                label="Gateway ID"
                monospace
                {...register('gatewayId')}
                error={errors.gatewayId?.message}
                placeholder="user-gw"
                disabled={!!editing}
                hint={editing ? '编辑模式下不可修改' : undefined}
              />
              <div className="flex items-end gap-2">
                <div className="flex-1">
                  <Input
                    label="Tool ID"
                    type="number"
                    monospace
                    {...register('toolId')}
                    error={errors.toolId?.message}
                    placeholder="1001"
                    disabled={!!editing}
                    hint={editing ? '全局唯一,不可修改' : '8 位数字'}
                  />
                </div>
                {!editing && (
                  <Button
                    type="button"
                    variant="secondary"
                    size="sm"
                    onClick={() => setValue('toolId', generateToolId() as any)}
                    leftIcon={<Dice5 size={14} />}
                  >
                    生成
                  </Button>
                )}
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <Input
                label="工具名称"
                {...register('toolName')}
                error={errors.toolName?.message}
                placeholder="search_user"
              />
              <Select
                label="工具类型"
                options={TYPE_OPTS}
                {...register('toolType')}
                error={errors.toolType?.message}
              />
            </div>
            <Textarea
              label="工具描述"
              rows={3}
              {...register('toolDescription')}
              error={errors.toolDescription?.message}
              placeholder="可选,工具用途说明"
            />
            <div className="grid grid-cols-3 gap-4">
              <Input
                label="工具版本"
                monospace
                {...register('toolVersion')}
                error={errors.toolVersion?.message}
                placeholder="1.0.0"
              />
              <Input
                label="协议 ID"
                type="number"
                monospace
                {...register('protocolId')}
                error={errors.protocolId?.message}
                placeholder="选填"
              />
              <Select
                label="协议类型"
                options={PROTO_OPTS}
                {...register('protocolType')}
                error={errors.protocolType?.message}
              />
            </div>
            {protocolType && protocolType !== 'HTTP' && (
              <p className="text-xs text-amber-600 bg-amber-50 border border-amber-200 rounded px-3 py-2">
                {protocolType} 协议当前仅 HTTP 完整支持,其他类型保存后可能不可用
              </p>
            )}
          </FormSection>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!pendingDelete}
        title="删除工具"
        message={`确认删除工具「${pendingDelete?.toolName ?? ''}」（Gateway=${pendingDelete?.gatewayId}, ToolId=${pendingDelete?.toolId}）？该操作不可恢复。`}
        confirmText="删除"
        danger
        onConfirm={confirmDelete}
        onCancel={() => setPendingDelete(null)}
      />

      <ConfirmDialog
        open={blocker.state === 'blocked'}
        title="未保存的修改"
        message="当前表单有未保存的修改,确定离开吗?"
        confirmText="离开"
        cancelText="留在此页"
        danger
        onConfirm={() => blocker.proceed?.()}
        onCancel={() => blocker.reset?.()}
      />
    </div>
  );
}
