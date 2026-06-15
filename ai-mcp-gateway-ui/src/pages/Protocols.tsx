import { useForm, useFieldArray, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Trash2, Plus, Info } from 'lucide-react';
import { useSaveGatewayProtocol } from '../api/hooks/useGatewayApi';
import { GatewayProtocolSchema, type GatewayProtocolInput } from '../api/schemas/gateway';
import { PageHeader } from '../components/ui/PageHeader';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Select } from '../components/ui/Select';
import { Button } from '../components/ui/Button';
import { FormSection } from '../components/ui/FormSection';
import { useToast } from '../lib/toast';
import { ApiError } from '../api/client';

const METHOD_OPTS = [
  { value: 'GET', label: 'GET' }, { value: 'POST', label: 'POST' },
  { value: 'PUT', label: 'PUT' }, { value: 'DELETE', label: 'DELETE' },
];
const MAPPING_TYPE_OPTS = [
  { value: 'query',  label: 'query (查询参数)' },
  { value: 'header', label: 'header (请求头)' },
  { value: 'body',   label: 'body (请求体)' },
  { value: 'path',   label: 'path (路径参数)' },
];

export default function Protocols() {
  const toast = useToast();
  const { control, register, handleSubmit, formState: { errors }, reset } = useForm<GatewayProtocolInput>({
    resolver: zodResolver(GatewayProtocolSchema),
    defaultValues: {
      gatewayId: '',
      http: { httpUrl: '', httpMethod: 'GET', httpHeaders: '{}', timeout: 5000 },
      mapping: [],
    },
  });
  const { fields, append, remove } = useFieldArray({ control, name: 'mapping' });
  const save = useSaveGatewayProtocol();

  async function onSubmit(values: GatewayProtocolInput) {
    try {
      await save.mutateAsync(values);
      toast.success(`协议已保存: ${values.gatewayId}`);
    } catch (e) {
      toast.error(e instanceof ApiError ? e.message : '保存失败');
    }
  }

  return (
    <div>
      <PageHeader
        title="协议配置"
        subtitle="HTTP 协议 + 参数映射规则"
        actions={
          <div className="flex items-center gap-2 px-3 py-1.5 rounded-md bg-sky-50 text-sky-700 border border-sky-200 text-xs">
            <Info size={14} /> 当前仅支持 HTTP,RPC 暂未开放
          </div>
        }
      />

      <form onSubmit={handleSubmit(onSubmit)} className="max-w-3xl space-y-6">
        <Card title="HTTP 协议" subtitle="目标 URL / 方法 / 超时">
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
            <div className="grid grid-cols-2 gap-4">
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
            </div>
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
          actions={
            <Button
              type="button"
              variant="secondary"
              size="sm"
              leftIcon={<Plus size={14} />}
              onClick={() => append({
                mappingType: 'query', parentPath: '', fieldName: '', mcpPath: '', mcpType: 'string',
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
            <div className="space-y-4">
              {fields.map((f, i) => (
                <div key={f.id} className="p-4 border border-[var(--border-default)] rounded-md bg-[var(--bg-sunken)]/40">
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

        <div className="flex items-center gap-2 justify-end">
          <Button type="button" variant="secondary" onClick={() => reset()}>重置</Button>
          <Button type="submit" loading={save.isPending}>保存</Button>
        </div>
      </form>
    </div>
  );
}
