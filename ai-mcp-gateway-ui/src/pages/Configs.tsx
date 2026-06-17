import { useEffect, useRef, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Plus, Edit } from 'lucide-react';
import { useUnsavedGuard } from '../lib/useUnsavedGuard';
import { useGatewayList, useSaveGatewayConfig } from '../api/hooks/useGatewayApi';
import { GatewayConfigSchema, type GatewayConfigInput } from '../api/schemas/gateway';
import { PageHeader } from '../components/ui/PageHeader';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Textarea } from '../components/ui/Textarea';
import { Select } from '../components/ui/Select';
import { Button } from '../components/ui/Button';
import { FormSection } from '../components/ui/FormSection';
import { Modal } from '../components/ui/Modal';
import { ConfirmDialog } from '../components/ui/ConfirmDialog';
import { Table, type Column } from '../components/ui/Table';
import { Pagination } from '../components/ui/Pagination';
import { authBadge, statusBadge } from '../api/gateway-badges';
import { useToast } from '../lib/toast';
import { ApiError } from '../api/client';
import type { GatewayConfigDTO } from '../api/schemas/gateway';

const AUTH_OPTS = [
  { value: 1, label: '强校验 (1)' },
  { value: 0, label: '不校验 (0)' },
];
const STATUS_OPTS = [
  { value: 1, label: '启用 (1)' },
  { value: 0, label: '禁用 (0)' },
];

const EMPTY: GatewayConfigInput = {
  gatewayId: '', name: '', desc: '', version: '1.0.0', auth: 1, status: 1,
};

export default function Configs() {
  const toast = useToast();
  const [params, setParams] = useSearchParams();
  const editId = params.get('gatewayId');
  const gateways = useGatewayList();
  const [formOpen, setFormOpen] = useState(false);
  const initialized = useRef(false);
  const formRef = useRef<HTMLFormElement>(null);

  const [page, setPage] = useState(1);
  const [rows] = useState(20);
  const [gatewayIdFilter, setGatewayIdFilter] = useState(params.get('gatewayId') ?? '');
  const [nameFilter, setNameFilter] = useState(params.get('gatewayName') ?? '');
  const [search, setSearch] = useState({
    gatewayId: params.get('gatewayId') ?? '',
    gatewayName: params.get('gatewayName') ?? '',
  });

  useEffect(() => {
    setSearch({
      gatewayId: params.get('gatewayId') ?? '',
      gatewayName: params.get('gatewayName') ?? '',
    });
  }, [params]);

  const { data, isLoading, isError, refetch, error } = gateways;
  const allList = data ?? [];
  const list = allList
    .filter((g) => !search.gatewayId || g.gatewayId.includes(search.gatewayId))
    .filter((g) => !search.gatewayName || g.name.includes(search.gatewayName))
    .slice((page - 1) * rows, page * rows);
  const total = allList.length;

  const { register, handleSubmit, formState: { errors, isDirty }, reset } = useForm<GatewayConfigInput>({
    resolver: zodResolver(GatewayConfigSchema),
    defaultValues: EMPTY,
  });
  const save = useSaveGatewayConfig();
  const blocker = useUnsavedGuard(formOpen && isDirty);

  useEffect(() => {
    if (!editId) {
      initialized.current = false;
      return;
    }
    if (!gateways.data) return;
    if (initialized.current) return;
    const found = gateways.data.find((g) => g.gatewayId === editId);
    if (!found) {
      toast.error(`网关 ${editId} 不存在`);
      return;
    }
    initialized.current = true;
    setFormOpen(true);
    reset({
      gatewayId: found.gatewayId,
      name: found.name,
      desc: found.desc,
      version: found.version,
      auth: found.auth === 'STRONG_VERIFIED' ? 1 : 0,
      status: found.status === 'ENABLE' ? 1 : 0,
    });
  }, [editId, gateways.data, reset]); // toast 故意不依赖,避免引用变化触发二次 reset

  const syncUrl = (next: Partial<{ page: number; rows: number; gatewayId: string; gatewayName: string }>) => {
    const p = new URLSearchParams(params);
    const setOrDel = (k: string, v: string | number | undefined, def: string | number) => {
      if (v === undefined || v === '' || v === def) p.delete(k); else p.set(k, String(v));
    };
    setOrDel('page',         next.page,         1);
    setOrDel('rows',         next.rows,         20);
    setOrDel('gatewayId',    next.gatewayId,    '');
    setOrDel('gatewayName',  next.gatewayName,  '');
    setParams(p, { replace: true });
  };

  const onSearch = () => syncUrl({ page: 1, gatewayId: gatewayIdFilter.trim(), gatewayName: nameFilter.trim() });
  const onReset = () => {
    setGatewayIdFilter('');
    setNameFilter('');
    syncUrl({ page: 1, gatewayId: '', gatewayName: '' });
  };

  function openCreate() {
    setParams({});
    initialized.current = false;
    setFormOpen(true);
    reset(EMPTY);
  }

  function openEdit(g: GatewayConfigDTO) {
    setParams({ gatewayId: g.gatewayId });
    initialized.current = true;
    setFormOpen(true);
    reset({
      gatewayId: g.gatewayId,
      name: g.name,
      desc: g.desc,
      version: g.version,
      auth: g.auth === 'STRONG_VERIFIED' ? 1 : 0,
      status: g.status === 'ENABLE' ? 1 : 0,
    });
  }

  function closeForm() {
    setFormOpen(false);
    if (editId) {
      initialized.current = false;
      setParams({});
    }
    reset(EMPTY);
  }

  async function onSubmit(values: GatewayConfigInput) {
    try {
      await save.mutateAsync(values);
      toast.success(`配置已保存: ${values.gatewayId}`);
      closeForm();
      refetch();
    } catch (e) {
      const msg = e instanceof ApiError ? e.message : '保存失败';
      toast.error(msg);
    }
  }

  const columns: Column<GatewayConfigDTO>[] = [
    { key: 'id',     header: 'ID',   width: '140px', render: (g) => <span className="font-mono text-xs">{g.gatewayId}</span> },
    { key: 'name',   header: '名称',                 render: (g) => <span className="font-medium">{g.name}</span> },
    { key: 'desc',   header: '描述',                 render: (g) => <span className="text-[var(--text-secondary)]">{g.desc || '—'}</span> },
    { key: 'ver',    header: '版本', width: '90px',  render: (g) => <span className="font-mono text-xs text-[var(--text-secondary)]">{g.version}</span> },
    { key: 'auth',   header: '认证', width: '90px',  render: (g) => authBadge(g.auth) },
    { key: 'status', header: '状态', width: '90px',  render: (g) => statusBadge(g.status) },
    {
      key: 'act',
      header: '操作',
      width: '90px',
      align: 'center',
      render: (g) => (
        <button
          type="button"
          onClick={() => openEdit(g)}
          className="inline-flex items-center gap-1 px-2 h-7 rounded text-blue-600 hover:bg-blue-50 text-[13px]"
          aria-label="编辑"
        >
          <Edit size={14} /> 编辑
        </button>
      ),
    },
  ];

  return (
    <div className="space-y-4">
      <PageHeader
        title="基础配置"
        subtitle="网关核心参数"
        actions={
          <Button
            variant="primary"
            size="sm"
            onClick={openCreate}
            leftIcon={<Plus size={14} />}
          >
            新增网关
          </Button>
        }
      />

      <Card>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-3 items-end">
          <Input
            label="网关 ID"
            placeholder="支持模糊匹配"
            value={gatewayIdFilter}
            onChange={(e) => setGatewayIdFilter(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && onSearch()}
          />
          <Input
            label="网关名称"
            placeholder="支持模糊匹配"
            value={nameFilter}
            onChange={(e) => setNameFilter(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && onSearch()}
          />
          <div className="flex gap-2">
            <Button onClick={onSearch}>搜索</Button>
            <Button variant="secondary" onClick={onReset}>重置</Button>
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
          emptyDesc="还没有任何网关配置"
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
        title={editId ? `修改网关 (${editId})` : '新增网关'}
        subtitle="标识、名称、版本与状态"
        width="md"
        onClose={closeForm}
        footer={
          <>
            <Button variant="secondary" onClick={closeForm}>取消</Button>
            <Button
              onClick={() => formRef.current?.requestSubmit()}
              loading={save.isPending}
            >
              {editId ? '保存修改' : '保存'}
            </Button>
          </>
        }
      >
        <form ref={formRef} onSubmit={handleSubmit(onSubmit)}>
          <FormSection>
            <Input
              label="Gateway ID"
              hint={editId ? '编辑模式下不可修改 (业务主键)' : '唯一标识,小写字母/数字/中划线,3-31 字符'}
              monospace
              {...register('gatewayId')}
              error={errors.gatewayId?.message}
              placeholder="user-gw"
              disabled={!!editId}
            />
            <Input
              label="名称"
              {...register('name')}
              error={errors.name?.message}
              placeholder="用户网关"
            />
            <Textarea
              label="描述"
              rows={3}
              {...register('desc')}
              error={errors.desc?.message}
              placeholder="可选,网关用途说明"
            />
            <div className="grid grid-cols-3 gap-4">
              <Input
                label="版本"
                monospace
                {...register('version')}
                error={errors.version?.message}
                placeholder="1.0.0"
              />
              <Select
                label="认证"
                options={AUTH_OPTS}
                {...register('auth', { setValueAs: (v) => Number(v) })}
                error={errors.auth?.message}
              />
              <Select
                label="状态"
                options={STATUS_OPTS}
                {...register('status', { setValueAs: (v) => Number(v) })}
                error={errors.status?.message}
              />
            </div>
          </FormSection>
        </form>
      </Modal>

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
