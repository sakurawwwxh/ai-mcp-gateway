import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useSaveGatewayConfig } from '../api/hooks/useGatewayApi';
import { GatewayConfigSchema, type GatewayConfigInput } from '../api/schemas/gateway';
import { PageHeader } from '../components/ui/PageHeader';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Textarea } from '../components/ui/Textarea';
import { Select } from '../components/ui/Select';
import { Button } from '../components/ui/Button';
import { FormSection } from '../components/ui/FormSection';
import { useToast } from '../lib/toast';
import { ApiError } from '../api/client';

const AUTH_OPTS = [
  { value: 1, label: '强校验 (1)' },
  { value: 0, label: '不校验 (0)' },
];
const STATUS_OPTS = [
  { value: 1, label: '启用 (1)' },
  { value: 0, label: '禁用 (0)' },
];

export default function Configs() {
  const toast = useToast();
  const { register, handleSubmit, formState: { errors }, reset } = useForm<GatewayConfigInput>({
    resolver: zodResolver(GatewayConfigSchema),
    defaultValues: { gatewayId: '', name: '', desc: '', version: '1.0.0', auth: 1, status: 1 },
  });
  const save = useSaveGatewayConfig();

  async function onSubmit(values: GatewayConfigInput) {
    try {
      await save.mutateAsync(values);
      toast.success(`配置已保存: ${values.gatewayId}`);
    } catch (e) {
      const msg = e instanceof ApiError ? e.message : '保存失败';
      toast.error(msg);
    }
  }

  return (
    <div>
      <PageHeader title="基础配置" subtitle="网关核心参数" />

      <form onSubmit={handleSubmit(onSubmit)} className="max-w-2xl">
        <Card title="网关信息" subtitle="标识、名称、版本与状态">
          <FormSection>
            <Input
              label="Gateway ID"
              hint="唯一标识,小写字母/数字/中划线,3-31 字符"
              monospace
              {...register('gatewayId')}
              error={errors.gatewayId?.message}
              placeholder="user-gw"
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

          <div className="mt-6 flex items-center gap-2 justify-end">
            <Button type="button" variant="secondary" onClick={() => reset()}>重置</Button>
            <Button type="submit" loading={save.isPending}>保存</Button>
          </div>
        </Card>
      </form>
    </div>
  );
}
