import { useState, useRef, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useForm, useFieldArray } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Trash2, Plus, Info, RefreshCw, Search, ChevronDown, ChevronRight, Upload, Sparkles } from 'lucide-react';
import {
  useSaveGatewayProtocol,
  useProtocolPage,
  useDeleteProtocol,
} from '../api/hooks/useGatewayApi';
import { GatewayProtocolSchema, type GatewayProtocolInput } from '../api/schemas/gateway';
import type { GatewayProtocolDTO } from '../api/schemas/gateway';
import { PageHeader } from '../components/ui/PageHeader';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Select } from '../components/ui/Select';
import { Button } from '../components/ui/Button';
import { FormSection } from '../components/ui/FormSection';
import { Table, type Column } from '../components/ui/Table';
import { Badge } from '../components/ui/Badge';
import { Pagination } from '../components/ui/Pagination';
import { ConfirmDialog } from '../components/ui/ConfirmDialog';
import { Modal } from '../components/ui/Modal';
import { useUnsavedGuard } from '../lib/useUnsavedGuard';
import { ImportProtocolDialog } from '../components/ImportProtocolDialog';
import { FillProtocolFromSwaggerDialog } from '../components/FillProtocolFromSwaggerDialog';
import { useToast } from '../lib/toast';
import { ApiError } from '../api/client';

const METHOD_OPTS = [
  { value: 'GET', label: 'GET' }, { value: 'POST', label: 'POST' },
  { value: 'PUT', label: 'PUT' }, { value: 'DELETE', label: 'DELETE' },
];
const MAPPING_TYPE_OPTS = [
  { value: 'request',  label: 'request (请求参数映射)' },
  { value: 'response', label: 'response (响应数据映射)' },
];

const baseColumns: Column<GatewayProtocolDTO>[] = [
  { key: 'id',   header: 'Protocol ID', width: '110px', render: (p) => <span className="font-mono text-xs">{p.protocolId}</span> },
  { key: 'url',  header: 'URL',                       render: (p) => <code className="text-xs font-mono text-[var(--text-secondary)] line-clamp-1">{p.httpUrl}</code> },
  { key: 'm',    header: '方法', width: '80px',       render: (p) => <Badge variant="info">{p.httpMethod}</Badge> },
  { key: 'to',   header: '超时', width: '90px',       render: (p) => <span className="text-xs">{p.timeout ? `${p.timeout}ms` : '—'}</span> },
  { key: 'rt',   header: '重试', width: '70px',       render: (p) => <span className="text-xs">{p.retryTimes ?? 0}</span> },
  { key: 'st',   header: '状态', width: '80px',       render: (p) => p.status === 1
                                                          ? <Badge variant="success">启用</Badge>
                                                          : <Badge variant="warning">禁用</Badge> },
  { key: 'mp',   header: '映射数', width: '80px',     render: (p) => <Badge variant="neutral">{p.mappings.length}</Badge> },
];

const EMPTY: GatewayProtocolInput = {
  gatewayId: '',
  http: { httpUrl: '', httpMethod: 'GET', httpHeaders: '{}', timeout: 30000, retryTimes: 0, status: 1 },
  mapping: [],
};

export default function Protocols() {
  const toast = useToast();
  const [params, setParams] = useSearchParams();
  const page  = Number(params.get('page')  ?? 1) || 1;
  const rows  = Number(params.get('rows')  ?? 20) || 20;
  const [gatewayFilter, setGatewayFilter] = useState(params.get('gatewayId') ?? '');
  const [urlFilter, setUrlFilter] = useState(params.get('httpUrl') ?? '');
  const [search, setSearch] = useState({
    gatewayId: params.get('gatewayId') ?? '',
    httpUrl: params.get('httpUrl') ?? '',
  });
  const [pendingDelete, setPendingDelete] = useState<GatewayProtocolDTO | null>(null);
  const [expanded, setExpanded] = useState<Set<number>>(new Set());
  const [importOpen, setImportOpen] = useState(false);
  const [formOpen, setFormOpen] = useState(false);
  const [parseOpen, setParseOpen] = useState(false);
  const formRef = useRef<HTMLFormElement>(null);

  useEffect(() => {
    setSearch({
      gatewayId: params.get('gatewayId') ?? '',
      httpUrl: params.get('httpUrl') ?? '',
    });
  }, [params]);

  const { data, isLoading, isError, refetch, error, isFetching } = useProtocolPage({
    page,
    rows,
    gatewayId: search.gatewayId,
    httpUrl: search.httpUrl,
  });

  const list = data?.data ?? [];
  const total = data?.total ?? 0;

  const syncUrl = (next: Partial<{ page: number; rows: number; gatewayId: string; httpUrl: string }>) => {
    const p = new URLSearchParams(params);
    const setOrDel = (k: string, v: string | number | undefined, def: string | number) => {
      if (v === undefined || v === '' || v === def) p.delete(k); else p.set(k, String(v));
    };
    setOrDel('page',      next.page,      1);
    setOrDel('rows',      next.rows,      20);
    setOrDel('gatewayId', next.gatewayId, '');
    setOrDel('httpUrl',   next.httpUrl,   '');
    setParams(p, { replace: true });
  };

  const onSearch = () => syncUrl({ page: 1, gatewayId: gatewayFilter.trim(), httpUrl: urlFilter.trim() });
  const onReset = () => {
    setGatewayFilter('');
    setUrlFilter('');
    syncUrl({ page: 1, gatewayId: '', httpUrl: '' });
  };

  const deleteProtocol = useDeleteProtocol();

  async function confirmDelete() {
    if (!pendingDelete || pendingDelete.protocolId == null) return;
    try {
      const ok = await deleteProtocol.mutateAsync({ protocolId: pendingDelete.protocolId });
      if (ok) toast.success('删除成功');
      else toast.error('删除失败');
    } catch (e) {
      toast.error(e instanceof ApiError ? e.message : '删除失败');
    } finally {
      setPendingDelete(null);
    }
  }

  const toggleExpand = (id: number) => {
    setExpanded((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id); else next.add(id);
      return next;
    });
  };

  const columns: Column<GatewayProtocolDTO>[] = [
    {
      key: 'exp',
      header: '',
      width: '32px',
      render: (p) => {
        if (p.protocolId == null) return null;
        const open = expanded.has(p.protocolId);
        return (
          <button
            type="button"
            onClick={() => toggleExpand(p.protocolId!)}
            className="text-[var(--text-tertiary)] hover:text-[var(--text-primary)]"
            aria-label={open ? '收起' : '展开'}
          >
            {open ? <ChevronDown size={14} /> : <ChevronRight size={14} />}
          </button>
        );
      },
    },
    ...baseColumns,
    {
      key: 'act',
      header: '操作',
      width: '80px',
      align: 'center',
      render: (p) => (
        <button
          type="button"
          onClick={() => setPendingDelete(p)}
          className="inline-flex items-center justify-center w-7 h-7 rounded text-red-600 hover:bg-red-50"
          aria-label="删除"
        >
          <Trash2 size={15} />
        </button>
      ),
    },
  ];

  const { control, register, handleSubmit, formState: { errors, isDirty }, reset, watch, getValues } = useForm<GatewayProtocolInput>({
    resolver: zodResolver(GatewayProtocolSchema),
    defaultValues: EMPTY,
  });
  const { fields, append, remove } = useFieldArray({ control, name: 'mapping' });
  const save = useSaveGatewayProtocol();
  const url = watch('http.httpUrl');
  const blocker = useUnsavedGuard(formOpen && isDirty);

  function openCreate() {
    reset(EMPTY);
    setFormOpen(true);
  }

  function closeForm() {
    setFormOpen(false);
    reset(EMPTY);
  }

  async function onSubmit(values: GatewayProtocolInput) {
    try {
      await save.mutateAsync(values);
      toast.success(`协议已保存: ${values.gatewayId}`);
      closeForm();
      refetch();
    } catch (e) {
      toast.error(e instanceof ApiError ? e.message : '保存失败');
    }
  }

  return (
    <div className="space-y-4">
      <PageHeader
        title="协议配置"
        subtitle="HTTP 协议 + 参数映射规则"
        actions={
          <div className="flex items-center gap-2">
            <div className="hidden lg:flex items-center gap-2 px-3 py-1.5 rounded-md bg-sky-50 text-sky-700 border border-sky-200 text-xs">
              <Info size={14} /> 当前仅支持 HTTP,RPC 暂未开放
            </div>
            <Button
              variant="secondary"
              size="sm"
              onClick={() => setImportOpen(true)}
              leftIcon={<Upload size={14} />}
            >
              导入协议
            </Button>
            <Button
              variant="primary"
              size="sm"
              onClick={openCreate}
              leftIcon={<Plus size={14} />}
            >
              新增协议
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
            label="网关 ID（经工具表关联）"
            placeholder="精确匹配"
            value={gatewayFilter}
            onChange={(e) => setGatewayFilter(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && onSearch()}
          />
          <Input
            label="HTTP URL"
            placeholder="支持模糊匹配"
            value={urlFilter}
            onChange={(e) => setUrlFilter(e.target.value)}
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
          columns={columns}
          data={list}
          rowKey={(p) => String(p.protocolId)}
          loading={isLoading}
          error={isError ? (error as Error)?.message ?? '加载失败' : null}
          onRetry={() => refetch()}
          emptyTitle="暂无协议"
          emptyDesc="还没有任何协议"
          emptyAction={
            <Button size="sm" leftIcon={<Plus size={14} />} onClick={openCreate}>
              新增第一条
            </Button>
          }
        />
        <Pagination page={page} rows={rows} total={total} onChange={(p) => syncUrl({ page: p })} onRowsChange={(r) => syncUrl({ page: 1, rows: r })} />
      </Card>

      {expanded.size > 0 && (
        <Card title="映射明细" subtitle="展开行可查看对应映射规则">
          {list.filter((p) => p.protocolId != null && expanded.has(p.protocolId)).map((p) => (
            <div key={p.protocolId} className="mb-4 last:mb-0">
              <div className="flex items-center gap-2 mb-2 text-xs text-[var(--text-secondary)]">
                <span className="font-mono">#{p.protocolId}</span>
                <span>·</span>
                <code className="truncate max-w-[400px]">{p.httpUrl}</code>
              </div>
              {p.mappings.length === 0 ? (
                <p className="text-xs text-[var(--text-tertiary)]">该协议无映射规则</p>
              ) : (
                <table className="w-full text-xs border border-[var(--border-default)] rounded-md overflow-hidden">
                  <thead className="bg-[var(--bg-sunken)]">
                    <tr>
                      <th className="px-2 py-1.5 text-left font-medium">类型</th>
                      <th className="px-2 py-1.5 text-left font-medium">父路径</th>
                      <th className="px-2 py-1.5 text-left font-medium">字段名</th>
                      <th className="px-2 py-1.5 text-left font-medium">MCP 路径</th>
                      <th className="px-2 py-1.5 text-left font-medium">类型</th>
                      <th className="px-2 py-1.5 text-left font-medium">必填</th>
                      <th className="px-2 py-1.5 text-left font-medium">顺序</th>
                    </tr>
                  </thead>
                  <tbody>
                    {p.mappings.map((m, i) => (
                      <tr key={i} className="border-t border-[var(--border-default)]">
                        <td className="px-2 py-1.5"><Badge variant="neutral">{m.mappingType}</Badge></td>
                        <td className="px-2 py-1.5 font-mono">{m.parentPath || '—'}</td>
                        <td className="px-2 py-1.5 font-mono">{m.fieldName}</td>
                        <td className="px-2 py-1.5 font-mono">{m.mcpPath}</td>
                        <td className="px-2 py-1.5">{m.mcpType}</td>
                        <td className="px-2 py-1.5">{m.isRequired === 1 ? '是' : '否'}</td>
                        <td className="px-2 py-1.5">{m.sortOrder}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          ))}
        </Card>
      )}

      <Modal
        open={formOpen}
        title="新增协议"
        subtitle="HTTP 协议 + 参数映射规则"
        width="xl"
        onClose={closeForm}
        footer={
          <>
            <Button variant="secondary" onClick={() => setParseOpen(true)} leftIcon={<Sparkles size={14} />}>
              用 Swagger 解析填充
            </Button>
            <div className="flex-1" />
            <Button variant="secondary" onClick={closeForm}>取消</Button>
            <Button
              onClick={() => formRef.current?.requestSubmit()}
              loading={save.isPending}
            >
              保存
            </Button>
          </>
        }
      >
        <form ref={formRef} onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          <Card title="HTTP 协议" subtitle="目标 URL / 方法 / 超时" padding="sm">
            <FormSection>
              <Input
                label="Gateway ID"
                monospace
                {...register('gatewayId')}
                error={errors.gatewayId?.message}
                placeholder="user-gw"
              />
              <Input
                label="URL"
                monospace
                {...register('http.httpUrl')}
                error={errors.http?.httpUrl?.message}
                placeholder="https://api.example.com/users"
              />
              {(() => {
                if (!url) return null;
                try {
                  const u = new URL(url);
                  return <p className="text-xs text-[var(--text-tertiary)] mt-1 font-mono">→ {u.pathname}{u.search}</p>;
                } catch {
                  return <p className="text-xs text-amber-600 mt-1">URL 格式无效</p>;
                }
              })()}
              <div className="grid grid-cols-3 gap-4">
                <Select
                  label="方法"
                  options={METHOD_OPTS}
                  {...register('http.httpMethod')}
                  error={errors.http?.httpMethod?.message}
                />
                <Input
                  label="超时 (ms)"
                  type="number"
                  monospace
                  {...register('http.timeout', { setValueAs: (v) => Number(v) })}
                  error={errors.http?.timeout?.message}
                />
                <Input
                  label="重试次数"
                  type="number"
                  monospace
                  hint="0-10 次"
                  {...register('http.retryTimes', { setValueAs: (v) => Number(v) })}
                  error={errors.http?.retryTimes?.message}
                />
              </div>
              <Select
                label="状态"
                options={[
                  { value: 1, label: '启用' },
                  { value: 0, label: '禁用' },
                ]}
                {...register('http.status', { setValueAs: (v) => Number(v) })}
                error={errors.http?.status?.message}
              />
              <Input
                label="请求头 (JSON 字符串)"
                hint='例如 {"Content-Type":"application/json"}'
                monospace
                {...register('http.httpHeaders')}
                error={errors.http?.httpHeaders?.message}
              />
            </FormSection>
          </Card>

          <Card
            title="参数映射"
            subtitle="外部字段 → MCP 路径的转换规则"
            padding="sm"
            actions={
              <Button
                type="button"
                variant="secondary"
                size="sm"
                leftIcon={<Plus size={14} />}
                onClick={() => append({
                  mappingType: 'request', parentPath: '', fieldName: '', mcpPath: '', mcpType: 'string',
                  mcpDesc: '', isRequired: 0, sortOrder: fields.length,
                })}
              >
                添加映射
              </Button>
            }
          >
            {fields.length === 0 ? (
              <p className="text-sm text-[var(--text-tertiary)] py-4 text-center">
                暂无映射,点击右上角「添加映射」
              </p>
            ) : (
              <div className="space-y-3">
                {fields.map((f, i) => (
                  <div key={f.id} className="p-3 border border-[var(--border-default)] rounded-md bg-[var(--bg-sunken)]/40">
                    <div className="grid grid-cols-12 gap-3">
                      <div className="col-span-3">
                        <Select
                          label="类型"
                          options={MAPPING_TYPE_OPTS}
                          {...register(`mapping.${i}.mappingType`)}
                          error={errors.mapping?.[i]?.mappingType?.message}
                        />
                      </div>
                      <div className="col-span-3">
                        <Input
                          label="父路径"
                          hint="嵌套字段路径,如 data.user"
                          monospace
                          {...register(`mapping.${i}.parentPath`)}
                          error={errors.mapping?.[i]?.parentPath?.message}
                        />
                      </div>
                      <div className="col-span-5">
                        <Input
                          label="外部字段名"
                          monospace
                          {...register(`mapping.${i}.fieldName`)}
                          error={errors.mapping?.[i]?.fieldName?.message}
                        />
                      </div>
                      <div className="col-span-4">
                        <Input
                          label="MCP 路径"
                          monospace
                          {...register(`mapping.${i}.mcpPath`)}
                          error={errors.mapping?.[i]?.mcpPath?.message}
                          placeholder="/user.id"
                        />
                      </div>
                      <div className="col-span-1 flex items-end">
                        <Button
                          type="button"
                          variant="ghost"
                          size="sm"
                          onClick={() => remove(i)}
                          aria-label="删除"
                          className="text-[var(--danger)]"
                        >
                          <Trash2 size={14} />
                        </Button>
                      </div>
                      <div className="col-span-3">
                        <Input
                          label="MCP 类型"
                          monospace
                          {...register(`mapping.${i}.mcpType`)}
                          error={errors.mapping?.[i]?.mcpType?.message}
                          placeholder="string"
                        />
                      </div>
                      <div className="col-span-4">
                        <Input
                          label="MCP 描述"
                          {...register(`mapping.${i}.mcpDesc`)}
                          error={errors.mapping?.[i]?.mcpDesc?.message}
                        />
                      </div>
                      <div className="col-span-2">
                        <Select
                          label="必填"
                          options={[{ value: 1, label: '是' }, { value: 0, label: '否' }]}
                          {...register(`mapping.${i}.isRequired`, { setValueAs: (v) => Number(v) })}
                          error={errors.mapping?.[i]?.isRequired?.message}
                        />
                      </div>
                      <div className="col-span-2">
                        <Input
                          label="顺序"
                          type="number"
                          monospace
                          {...register(`mapping.${i}.sortOrder`, { setValueAs: (v) => Number(v) })}
                          error={errors.mapping?.[i]?.sortOrder?.message}
                        />
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </Card>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!pendingDelete}
        title="删除协议"
        message={`确认删除 Protocol ID「${pendingDelete?.protocolId ?? ''}」的协议（${pendingDelete?.httpMethod} ${pendingDelete?.httpUrl}）？该操作不可恢复。`}
        confirmText="删除"
        danger
        onConfirm={confirmDelete}
        onCancel={() => setPendingDelete(null)}
      />

      <ImportProtocolDialog
        open={importOpen}
        onClose={() => setImportOpen(false)}
        onSuccess={() => {
          setImportOpen(false);
          refetch();
        }}
      />

      <FillProtocolFromSwaggerDialog
        open={parseOpen}
        onClose={() => setParseOpen(false)}
        onPick={(vo) => {
          reset({
            gatewayId: getValues('gatewayId'),
            http: {
              httpUrl: vo.httpUrl,
              httpMethod: vo.httpMethod.toUpperCase() as 'GET' | 'POST' | 'PUT' | 'DELETE',
              httpHeaders: vo.httpHeaders || '{}',
              timeout: vo.timeout ?? 30000,
              retryTimes: vo.retryTimes ?? 0,
              status: vo.status ?? 1,
            },
            mapping: vo.mappings.map((m) => ({
              mappingType: m.mappingType,
              parentPath: m.parentPath ?? '',
              fieldName: m.fieldName,
              mcpPath: m.mcpPath,
              mcpType: m.mcpType,
              mcpDesc: m.mcpDesc ?? '',
              isRequired: m.isRequired,
              sortOrder: m.sortOrder,
            })),
          });
          setParseOpen(false);
          toast.success('表单已填充');
        }}
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
