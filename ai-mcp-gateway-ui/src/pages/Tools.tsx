import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useSaveGatewayTool } from '../api/hooks/useGatewayApi';
import { GatewayToolConfigSchema, type GatewayToolConfigInput } from '../api/schemas/gateway';
import { PageHeader } from '../components/ui/PageHeader';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Textarea } from '../components/ui/Textarea';
import { Select } from '../components/ui/Select';
import { Button } from '../components/ui/Button';
import { FormSection } from '../components/ui/FormSection';
import { useToast } from '../lib/toast';
import { ApiError } from '../api/client';

const TYPE_OPTS = [
  { value: 'function',  label: 'function (函数)' },
  { value: 'resource',  label: 'resource (资源)' },
];
const PROTO_OPTS = [
  { value: 'HTTP', label: 'HTTP' },
  { value: 'RPC',  label: 'RPC' },
];

export default function Tools() {
  const toast = useToast();
  const { register, handleSubmit, formState: { errors }, reset } = useForm<GatewayToolConfigInput>({
    resolver: zodResolver(GatewayToolConfigSchema),
    defaultValues: {
      gatewayId: '', toolId: '' as any, toolName: '', toolType: 'function',
      toolDescription: '', toolVersion: '1.0.0', protocolId: '' as any, protocolType: 'HTTP',
    },
  });
  const save = useSaveGatewayTool();

  async function onSubmit(values: GatewayToolConfigInput) {
    try {
      await save.mutateAsync(values);
      toast.success(`工具已保存: ${values.toolName}`);
    } catch (e) {
      toast.error(e instanceof ApiError ? e.message : '保存失败');
    }
  }

  return (
    <div>
      <PageHeader title="工具配置" subtitle="注册网关下的 MCP 工具" />

      <form onSubmit={handleSubmit(onSubmit)} className="max-w-2xl">
        <Card title="工具信息" subtitle="归属网关 + 工具元数据">
          <FormSection>
            <div className="grid grid-cols-2 gap-4">
              <Input
                label="Gateway ID"
                monospace
                {...register('gatewayId')}
                error={errors.gatewayId?.message}
                placeholder="user-gw"
              />
              <Input
                label="Tool ID"
                type="number"
                monospace
                {...register('toolId')}
                error={errors.toolId?.message}
                placeholder="1001"
              />
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
          </FormSection>

          <div className="mt-6 flex items-center gap-2 justify-end">
            <Button type="button" variant="secondary" onClick={() => reset()}>重置</Button>
            <Button type="submit" loading={save.isPending}>保存</Button>
          </div>
        </Card>
      </form>
    </div>
  );
}
