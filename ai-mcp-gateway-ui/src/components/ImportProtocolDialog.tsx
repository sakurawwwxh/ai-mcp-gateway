import { useEffect, useRef, useState } from 'react';
import { Upload } from 'lucide-react';
import { useAnalysisProtocol, useImportProtocol } from '../api/hooks/useGatewayApi';
import type { GatewayProtocolDTO } from '../api/schemas/gateway';
import { Button } from './ui/Button';
import { Textarea } from './ui/Textarea';
import { useToast } from '../lib/toast';
import { ApiError } from '../api/client';

interface Props {
  open: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

/**
 * Swagger / OpenAPI 协议导入弹窗
 * 流程：粘贴 JSON → 解析（不落库） → 勾选接口 → 批量导入（落库）
 */
export function ImportProtocolDialog({ open, onClose, onSuccess }: Props) {
  const toast = useToast();
  const [gatewayId, setGatewayId] = useState('');
  const [json, setJson] = useState('');
  const [preview, setPreview] = useState<GatewayProtocolDTO[]>([]);
  const [selected, setSelected] = useState<Set<string>>(new Set());
  const analysis = useAnalysisProtocol();
  const importMut = useImportProtocol();
  const fileRef = useRef<HTMLInputElement>(null);

  async function onFile(e: React.ChangeEvent<HTMLInputElement>) {
    const f = e.target.files?.[0];
    if (!f) return;
    if (f.size > 2_000_000) {
      toast.error('文件超过 2MB,请压缩或精简');
      e.target.value = '';
      return;
    }
    const text = await f.text();
    try {
      JSON.parse(text);
      setJson(text);
      toast.success(`已加载 ${f.name}`);
    } catch {
      toast.error('文件不是有效 JSON');
    } finally {
      e.target.value = '';
    }
  }

  // 打开时重置
  useEffect(() => {
    if (!open) {
      setGatewayId('');
      setJson('');
      setPreview([]);
      setSelected(new Set());
    }
  }, [open]);

  if (!open) return null;

  async function onParse() {
    if (!gatewayId.trim()) {
      toast.error('请填写归属网关');
      return;
    }
    try {
      const list = await analysis.mutateAsync({ gatewayId: gatewayId.trim(), openApiJson: json });
      setPreview(list);
      setSelected(new Set(list.map((p) => p.httpUrl)));
      toast.success(`解析成功，共 ${list.length} 个接口`);
    } catch (e) {
      toast.error(e instanceof ApiError ? e.message : '解析失败');
    }
  }

  async function onImport() {
    if (selected.size === 0) {
      toast.error('请至少选择一个接口');
      return;
    }
    try {
      await importMut.mutateAsync({
        gatewayId: gatewayId.trim(),
        openApiJson: json,
        endpoints: Array.from(selected),
      });
      toast.success(`已导入 ${selected.size} 个接口`);
      onSuccess();
    } catch (e) {
      toast.error(e instanceof ApiError ? e.message : '导入失败');
    }
  }

  function toggle(url: string) {
    const next = new Set(selected);
    if (next.has(url)) next.delete(url); else next.add(url);
    setSelected(next);
  }

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-[var(--bg-surface)] rounded-lg w-[860px] max-h-[85vh] flex flex-col shadow-xl">
        <div className="px-6 py-4 border-b border-[var(--border-default)] flex items-center justify-between">
          <h3 className="text-lg font-semibold text-[var(--text-primary)]">导入 Swagger 协议</h3>
          <button
            type="button"
            onClick={onClose}
            className="text-[var(--text-tertiary)] hover:text-[var(--text-primary)] text-xl leading-none"
            aria-label="关闭"
          >
            ×
          </button>
        </div>

        <div className="flex-1 overflow-auto p-6 space-y-4">
          <div>
            <label className="block text-[13px] font-medium text-[var(--text-primary)] mb-1.5">
              归属网关 <span className="text-[var(--danger)]">*</span>
            </label>
            <input
              value={gatewayId}
              onChange={(e) => setGatewayId(e.target.value)}
              placeholder="例：weather-gateway"
              className="block w-full h-10 px-3 rounded-md text-sm border border-[var(--border-default)] bg-[var(--bg-surface)] text-[var(--text-primary)] focus:outline-none focus:ring-[3px] focus:ring-[rgba(37,99,235,0.25)]"
            />
          </div>

          <div className="flex items-center gap-2">
            <input
              ref={fileRef}
              type="file"
              accept=".json,application/json"
              className="hidden"
              onChange={onFile}
            />
            <Button
              type="button"
              variant="secondary"
              size="sm"
              leftIcon={<Upload size={14} />}
              onClick={() => fileRef.current?.click()}
            >
              上传 JSON 文件
            </Button>
            <span className="text-xs text-[var(--text-tertiary)]">或在下方粘贴 Swagger / OpenAPI JSON</span>
          </div>

          <Textarea
            label="Swagger / OpenAPI JSON"
            rows={8}
            value={json}
            onChange={(e) => setJson(e.target.value)}
            placeholder='粘贴 OpenAPI 3.0 JSON，例如 {"openapi":"3.0.0",...}'
          />

          <Button onClick={onParse} loading={analysis.isPending} disabled={!json.trim() || !gatewayId.trim()}>
            解析接口
          </Button>

          {preview.length > 0 && (
            <div className="border border-[var(--border-default)] rounded-md overflow-hidden">
              <div className="px-4 py-2 bg-[var(--bg-sunken)] border-b border-[var(--border-default)] flex items-center justify-between">
                <span className="text-sm font-medium text-[var(--text-primary)]">
                  共 {preview.length} 个接口，已选 {selected.size}
                </span>
                <div className="flex gap-3">
                  <button
                    type="button"
                    className="text-xs text-[var(--brand)] hover:underline"
                    onClick={() => setSelected(new Set(preview.map((p) => p.httpUrl)))}
                  >
                    全选
                  </button>
                  <button
                    type="button"
                    className="text-xs text-[var(--text-secondary)] hover:underline"
                    onClick={() => setSelected(new Set())}
                  >
                    清空
                  </button>
                </div>
              </div>
              <div className="max-h-72 overflow-auto divide-y divide-[var(--border-default)]">
                {preview.map((p) => (
                  <label
                    key={p.httpUrl}
                    className="flex items-center gap-3 px-4 py-2 hover:bg-[var(--bg-sunken)] cursor-pointer"
                  >
                    <input
                      type="checkbox"
                      checked={selected.has(p.httpUrl)}
                      onChange={() => toggle(p.httpUrl)}
                    />
                    <span className="text-xs font-mono uppercase text-[var(--brand)] w-14">
                      {p.httpMethod}
                    </span>
                    <span className="text-sm font-mono flex-1 truncate text-[var(--text-primary)]">
                      {p.httpUrl}
                    </span>
                    <span className="text-xs text-[var(--text-tertiary)]">
                      {p.mappings.length} 字段
                    </span>
                  </label>
                ))}
              </div>
            </div>
          )}
        </div>

        <div className="px-6 py-4 border-t border-[var(--border-default)] flex justify-end gap-2">
          <Button variant="secondary" onClick={onClose}>取消</Button>
          <Button onClick={onImport} loading={importMut.isPending} disabled={selected.size === 0}>
            导入选中 ({selected.size})
          </Button>
        </div>
      </div>
    </div>
  );
}
