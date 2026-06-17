import { useState, useRef, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { AlertTriangle, X, Trash2, RefreshCw, Search, Pencil, Plus } from 'lucide-react';
import {
  useSaveGatewayAuth,
  useAuthPage,
  useDeleteAuth,
} from '../api/hooks/useGatewayApi';
import { GatewayAuthSchema, type GatewayAuthInput } from '../api/schemas/gateway';

/** 默认过期时间: +1 月 (YYYY-MM-DDTHH:mm) — 跨月防溢出 (1/31→2/28) */
function defaultExpire(): string {
  const d = new Date();
  const day = d.getDate();
  d.setMonth(d.getMonth() + 1);
  if (d.getDate() !== day) d.setDate(0);
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
}
import { PageHeader } from '../components/ui/PageHeader';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Button } from '../components/ui/Button';
import { FormSection } from '../components/ui/FormSection';
import { CopyButton } from '../components/ui/CopyButton';
import { Table, type Column } from '../components/ui/Table';
import { Badge } from '../components/ui/Badge';
import { Pagination } from '../components/ui/Pagination';
import { ConfirmDialog } from '../components/ui/ConfirmDialog';
import { Modal } from '../components/ui/Modal';
import { useUnsavedGuard } from '../lib/useUnsavedGuard';
import { useToast } from '../lib/toast';
import { ApiError } from '../api/client';
import type { GatewayAuthDTO } from '../api/schemas/gateway';

const authColumns: Column<GatewayAuthDTO>[] = [
  { key: 'gw',     header: '网关', width: '140px', render: (a) => <span className="font-mono text-xs">{a.gatewayId}</span> },
  { key: 'key',    header: 'API Key', render: (a) => (
    <div className="flex items-center gap-2">
      <code className="text-xs font-mono text-[var(--text-secondary)]">
        {a.apiKey ? `${a.apiKey.slice(0, 12)}…` : '—'}
      </code>
      {a.apiKey && <CopyButton value={a.apiKey} label="" />}
    </div>
  ) },
  { key: 'rate',   header: '速率', width: '90px', render: (a) => <Badge variant="info">{a.rateLimit ?? '—'} /h</Badge> },
  { key: 'expire', header: '过期时间', width: '180px', render: (a) => <span className="text-xs text-[var(--text-secondary)]">{a.expireTime ?? '永久'}</span> },
];

const EMPTY: GatewayAuthInput = { gatewayId: '', rateLimit: 1000, expireTime: defaultExpire() };

export default function Auth() {
  const toast = useToast();
  const [params, setParams] = useSearchParams();
  const page  = Number(params.get('page')  ?? 1) || 1;
  const rows  = Number(params.get('rows')  ?? 20) || 20;
  const [gatewayFilter, setGatewayFilter] = useState(params.get('gatewayId') ?? '');
  const [search, setSearch] = useState(params.get('gatewayId') ?? '');
  const [pendingDelete, setPendingDelete] = useState<GatewayAuthDTO | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<GatewayAuthDTO | null>(null);
  const [apiKey, setApiKey] = useState<string | null>(null);
  const formRef = useRef<HTMLFormElement>(null);

  useEffect(() => {
    setSearch(params.get('gatewayId') ?? '');
  }, [params]);

  const { data, isLoading, isError, refetch, error, isFetching } = useAuthPage({
    page,
    rows,
    gatewayId: search,
  });

  const list = data?.data ?? [];
  const total = data?.total ?? 0;

  const syncUrl = (next: Partial<{ page: number; rows: number; gatewayId: string }>) => {
    const p = new URLSearchParams(params);
    const setOrDel = (k: string, v: string | number | undefined, def: string | number) => {
      if (v === undefined || v === '' || v === def) p.delete(k); else p.set(k, String(v));
    };
    setOrDel('page',      next.page,      1);
    setOrDel('rows',      next.rows,      20);
    setOrDel('gatewayId', next.gatewayId, '');
    setParams(p, { replace: true });
  };

  const onSearch = () => syncUrl({ page: 1, gatewayId: gatewayFilter.trim() });
  const onReset = () => {
    setGatewayFilter('');
    syncUrl({ page: 1, gatewayId: '' });
  };

  const deleteAuth = useDeleteAuth();

  async function confirmDelete() {
    if (!pendingDelete) return;
    try {
      const ok = await deleteAuth.mutateAsync({ gatewayId: pendingDelete.gatewayId });
      if (ok) toast.success('删除成功');
      else toast.error('删除失败');
    } catch (e) {
      toast.error(e instanceof ApiError ? e.message : '删除失败');
    } finally {
      setPendingDelete(null);
    }
  }

  const columnsWithAction: Column<GatewayAuthDTO>[] = [
    ...authColumns,
    {
      key: 'act',
      header: '操作',
      width: '110px',
      align: 'center',
      render: (a) => (
        <div className="flex items-center justify-center gap-1">
          <button
            type="button"
            onClick={() => { setEditing(a); setFormOpen(true); }}
            className="inline-flex items-center justify-center w-7 h-7 rounded text-blue-600 hover:bg-blue-50"
            aria-label="编辑"
            title="编辑"
          >
            <Pencil size={15} />
          </button>
          <button
            type="button"
            onClick={() => setPendingDelete(a)}
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

  const { register, handleSubmit, formState: { errors, isDirty }, reset } = useForm<GatewayAuthInput>({
    resolver: zodResolver(GatewayAuthSchema),
    defaultValues: EMPTY,
  });
  const save = useSaveGatewayAuth();
  const blocker = useUnsavedGuard(formOpen && isDirty);

  function openCreate() {
    setEditing(null);
    setApiKey(null);
    reset({ ...EMPTY, expireTime: defaultExpire() });
    setFormOpen(true);
  }

  function openEdit(a: GatewayAuthDTO) {
    setEditing(a);
    setApiKey(null);
    reset({
      gatewayId: a.gatewayId,
      rateLimit: a.rateLimit,
      expireTime: a.expireTime ? a.expireTime.substring(0, 16) : '',
    });
    setFormOpen(true);
  }

  function closeForm() {
    setFormOpen(false);
    setEditing(null);
    setApiKey(null);
    reset(EMPTY);
  }

  async function onSubmit(values: GatewayAuthInput) {
    try {
      const result = await save.mutateAsync(values);
      if (!editing && result.apiKey) {
        setApiKey(result.apiKey);
        toast.success('API Key 已生成');
      } else {
        toast.success(editing ? '已更新' : '已保存');
        closeForm();
      }
      refetch();
    } catch (e) {
      toast.error(e instanceof ApiError ? e.message : '保存失败');
    }
  }

  return (
    <div className="space-y-4">
      <PageHeader
        title="认证配置"
        subtitle="鉴权列表 + 新增/生成 API Key"
        actions={
          <div className="flex items-center gap-2">
            <Button
              variant="primary"
              size="sm"
              onClick={openCreate}
              leftIcon={<Plus size={14} />}
            >
              新增鉴权
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
        <div className="grid grid-cols-1 md:grid-cols-3 gap-3 items-end">
          <Input
            label="网关 ID"
            placeholder="精确匹配"
            value={gatewayFilter}
            onChange={(e) => setGatewayFilter(e.target.value)}
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
          rowKey={(a) => a.gatewayId}
          loading={isLoading}
          error={isError ? (error as Error)?.message ?? '加载失败' : null}
          onRetry={() => refetch()}
          emptyTitle="暂无鉴权"
          emptyDesc="还没有任何鉴权配置"
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
        title={editing ? `修改鉴权 (${editing.gatewayId})` : '新增鉴权'}
        subtitle="归属网关 / 速率限制 / 过期时间"
        width="md"
        onClose={closeForm}
        footer={apiKey ? (
          <Button variant="secondary" onClick={closeForm}>我已保存,关闭</Button>
        ) : (
          <>
            <Button variant="secondary" onClick={closeForm}>取消</Button>
            <Button
              onClick={() => formRef.current?.requestSubmit()}
              loading={save.isPending}
            >
              {editing ? '保存修改' : '保存并生成 API Key'}
            </Button>
          </>
        )}
      >
        {apiKey ? (
          <div className="bg-amber-50 border border-amber-200 rounded-lg p-4">
            <div className="flex items-start gap-3">
              <AlertTriangle size={20} className="text-amber-600 mt-0.5 shrink-0" />
              <div className="flex-1">
                <h3 className="text-sm font-semibold text-amber-900">API Key 已生成</h3>
                <p className="text-xs text-amber-700 mt-1">
                  请立即保存。关闭后不再展示,如丢失需重新生成。
                </p>
                <div className="mt-3 flex items-center gap-2 p-3 bg-[var(--bg-surface)] border border-amber-200 rounded-md">
                  <code className="flex-1 font-mono text-sm text-[var(--text-primary)] break-all">{apiKey}</code>
                  <CopyButton value={apiKey} />
                </div>
              </div>
            </div>
          </div>
        ) : (
          <form ref={formRef} onSubmit={handleSubmit(onSubmit)}>
            <FormSection>
              <Input
                label="Gateway ID"
                monospace
                {...register('gatewayId')}
                error={errors.gatewayId?.message}
                placeholder="user-gw"
                disabled={!!editing}
                hint={editing ? '编辑模式下不可修改' : undefined}
              />
              <div className="grid grid-cols-2 gap-4">
                <Input
                  label="速率限制"
                  type="number"
                  monospace
                  hint="次/小时"
                  {...register('rateLimit', { setValueAs: (v) => Number(v) })}
                  error={errors.rateLimit?.message}
                />
                <Input
                  label="过期时间"
                  type="datetime-local"
                  {...register('expireTime')}
                  error={errors.expireTime?.message}
                />
              </div>
            </FormSection>
          </form>
        )}
      </Modal>

      <ConfirmDialog
        open={!!pendingDelete}
        title="删除鉴权"
        message={`确认删除 Gateway「${pendingDelete?.gatewayId ?? ''}」的鉴权配置？该操作不可恢复。`}
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
