import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { AlertTriangle, X } from 'lucide-react';
import { useSaveGatewayAuth } from '../api/hooks/useGatewayApi';
import { GatewayAuthSchema, type GatewayAuthInput } from '../api/schemas/gateway';
import { PageHeader } from '../components/ui/PageHeader';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Button } from '../components/ui/Button';
import { FormSection } from '../components/ui/FormSection';
import { CopyButton } from '../components/ui/CopyButton';
import { useToast } from '../lib/toast';
import { ApiError } from '../api/client';

export default function Auth() {
  const toast = useToast();
  const { register, handleSubmit, formState: { errors }, reset } = useForm<GatewayAuthInput>({
    resolver: zodResolver(GatewayAuthSchema),
    defaultValues: { gatewayId: '', rateLimit: 100, expireTime: '' },
  });
  const save = useSaveGatewayAuth();
  const [apiKey, setApiKey] = useState<string | null>(null);

  async function onSubmit(values: GatewayAuthInput) {
    try {
      const result = await save.mutateAsync(values);
      if (result.apiKey) {
        setApiKey(result.apiKey);
        toast.success('API Key 已生成');
      } else {
        toast.success('已保存');
      }
    } catch (e) {
      toast.error(e instanceof ApiError ? e.message : '保存失败');
    }
  }

  return (
    <div>
      <PageHeader title="认证配置" subtitle="限流策略 + API Key 生成" />

      <form onSubmit={handleSubmit(onSubmit)} className="max-w-2xl space-y-6">
        <Card title="认证参数" subtitle="归属网关 / 速率限制 / 过期时间">
          <FormSection>
            <Input
              label="Gateway ID"
              monospace
              {...register('gatewayId')}
              error={errors.gatewayId?.message}
              placeholder="user-gw"
            />
            <div className="grid grid-cols-2 gap-4">
              <Input
                label="速率限制"
                type="number"
                monospace
                hint="次/秒"
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

          <div className="mt-6 flex items-center gap-2 justify-end">
            <Button type="button" variant="secondary" onClick={() => { reset(); setApiKey(null); }}>重置</Button>
            <Button type="submit" loading={save.isPending}>保存并生成 API Key</Button>
          </div>
        </Card>

        {apiKey && (
          <div className="bg-amber-50 border border-amber-200 rounded-lg p-5">
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
                <div className="mt-3 flex justify-end">
                  <Button
                    type="button"
                    variant="ghost"
                    size="sm"
                    onClick={() => setApiKey(null)}
                    leftIcon={<X size={14} />}
                  >
                    我已保存,关闭
                  </Button>
                </div>
              </div>
            </div>
          </div>
        )}
      </form>
    </div>
  );
}
