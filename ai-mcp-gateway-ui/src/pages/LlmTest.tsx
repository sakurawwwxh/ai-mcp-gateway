import { useEffect, useState } from 'react';
import { useForm, useWatch } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Send, FlaskConical } from 'lucide-react';
import { useTestCallGateway, useGatewayList, useAuthList } from '../api/hooks/useGatewayApi';
import { GatewayLLMRequestSchema, type GatewayLLMRequestInput } from '../api/schemas/gateway';
import { PageHeader } from '../components/ui/PageHeader';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Select } from '../components/ui/Select';
import { Textarea } from '../components/ui/Textarea';
import { FormSection } from '../components/ui/FormSection';
import { Button } from '../components/ui/Button';
import { Spinner } from '../components/ui/Spinner';
import { EmptyState } from '../components/ui/EmptyState';
import { ErrorState } from '../components/ui/ErrorState';
import { CopyButton } from '../components/ui/CopyButton';
import { useToast } from '../lib/toast';
import { ApiError } from '../api/client';

const EMPTY: GatewayLLMRequestInput = {
  gatewayId: '',
  message: '',
  authApiKey: '',
  timeout: 60000,
  reload: false,
};

export default function LlmTest() {
  const toast = useToast();
  const test = useTestCallGateway();
  const gateways = useGatewayList();
  const auths = useAuthList();
  const [result, setResult] = useState<string | null>(null);
  const [errMsg, setErrMsg] = useState<string | null>(null);

  const { register, handleSubmit, formState: { errors }, control, setValue } = useForm<GatewayLLMRequestInput>({
    resolver: zodResolver(GatewayLLMRequestSchema),
    defaultValues: EMPTY,
  });
  const selectedGatewayId = useWatch({ control, name: 'gatewayId' });

  const gatewayOpts = (gateways.data ?? []).map((g) => ({
    value: g.gatewayId,
    label: `${g.gatewayId} · ${g.name}`,
  }));

  // 按已选网关过滤其 auth token，供下拉选择
  const authOpts = (auths.data ?? [])
    .filter((a) => a.gatewayId === selectedGatewayId && a.apiKey)
    .map((a) => ({
      value: a.apiKey!,
      label: a.expireTime ? `${a.apiKey} · 过期 ${a.expireTime.slice(0, 10)}` : `${a.apiKey} · 永久`,
    }));

  // 切换网关时清空已选 token，避免带上不属于新网关的 key
  useEffect(() => {
    setValue('authApiKey', '');
  }, [selectedGatewayId, setValue]);

  async function onSubmit(values: GatewayLLMRequestInput) {
    setErrMsg(null);
    setResult(null);
    try {
      const res = await test.mutateAsync(values);
      setResult(res.content);
      toast.success('LLM 测试调用完成');
    } catch (e) {
      const msg = e instanceof ApiError ? e.message : '调用失败';
      setErrMsg(msg);
      toast.error(msg);
    }
  }

  return (
    <div className="space-y-4">
      <PageHeader
        title="LLM 测试网关"
        subtitle="通过 ChatModel + MCP 协议回调，验证 LLM 能自主调用网关工具"
        actions={null}
      />

      <Card title="测试参数" subtitle="gatewayId / message / authApiKey / timeout / reload">
        <form onSubmit={handleSubmit(onSubmit)}>
          <FormSection>
            <Select
              label="Gateway ID"
              hint="从已配置的网关中选择，避免输入不存在的网关"
              placeholder={gateways.isLoading ? '加载网关列表…' : '请选择网关'}
              options={gatewayOpts}
              disabled={gateways.isLoading}
              error={errors.gatewayId?.message ?? (gateways.isError ? '网关列表加载失败' : undefined)}
              {...register('gatewayId')}
            />
            <Textarea
              label="测试消息"
              rows={3}
              hint="向 LLM 提问的自然语言，由 LLM 自主决定是否调用网关工具"
              placeholder="查询北京字节跳动的员工信息"
              error={errors.message?.message}
              {...register('message')}
            />
            <Select
              label="Auth API Key"
              hint={selectedGatewayId && authOpts.length === 0
                ? '该网关未配置 token（无鉴权网关可不选）'
                : '从该网关已配置的 token 中选择，拼到 SSE ?api_key='}
              placeholder={!selectedGatewayId ? '请先选择网关' : authOpts.length === 0 ? '无可用 token' : '请选择 token'}
              options={authOpts}
              disabled={!selectedGatewayId || auths.isLoading}
              error={errors.authApiKey?.message ?? (auths.isError ? '鉴权列表加载失败' : undefined)}
              {...register('authApiKey')}
            />
            <div className="grid grid-cols-2 gap-4">
              <Input
                label="超时(ms)"
                monospace
                type="number"
                hint="1000-300000"
                error={errors.timeout?.message}
                {...register('timeout')}
              />
              <label className="flex items-end gap-2 pb-2 text-sm text-[var(--text-primary)]">
                <input type="checkbox" className="h-4 w-4" {...register('reload')} />
                reload（强制重建 ChatModel 缓存）
              </label>
            </div>
            <div className="flex gap-2 pt-2">
              <Button type="submit" loading={test.isPending} leftIcon={<Send size={14} />}>
                发起测试
              </Button>
            </div>
          </FormSection>
        </form>
      </Card>

      <Card
        title="LLM 回复"
        subtitle="content"
        actions={result ? <CopyButton value={result} /> : null}
      >
        {test.isPending && (
          <div className="flex items-center justify-center gap-2 py-10 text-sm text-[var(--text-tertiary)]">
            <Spinner size="sm" /> 调用中，LLM 推理 + 工具回调可能需要数秒…
          </div>
        )}
        {!test.isPending && errMsg && (
          <ErrorState title="调用失败" desc={errMsg} onRetry={() => setErrMsg(null)} />
        )}
        {!test.isPending && !errMsg && result && (
          <pre className="whitespace-pre-wrap break-words rounded-md bg-[var(--bg-sunken)] p-4 font-mono text-sm text-[var(--text-primary)]">
            {result}
          </pre>
        )}
        {!test.isPending && !errMsg && !result && (
          <EmptyState
            icon={<FlaskConical size={20} />}
            title="尚未测试"
            desc="填写参数后点击「发起测试」"
          />
        )}
      </Card>
    </div>
  );
}
