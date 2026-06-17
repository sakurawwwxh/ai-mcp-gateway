import { useRef, useState } from 'react';
import { Upload, X } from 'lucide-react';
import { useAnalysisProtocol } from '../api/hooks/useGatewayApi';
import type { GatewayProtocolDTO } from '../api/schemas/gateway';
import { Button } from './ui/Button';
import { Textarea } from './ui/Textarea';
import { useToast } from '../lib/toast';
import { ApiError } from '../api/client';

interface Props {
  open: boolean;
  onClose: () => void;
  onPick: (vo: GatewayProtocolDTO) => void;
}

/** 用 Swagger 解析填充当前协议表单(单接口选择) */
export function FillProtocolFromSwaggerDialog({ open, onClose, onPick }: Props) {
  const toast = useToast();
  const [json, setJson] = useState('');
  const [list, setList] = useState<GatewayProtocolDTO[]>([]);
  const mut = useAnalysisProtocol();
  const fileRef = useRef<HTMLInputElement>(null);

  if (!open) return null;

  async function onFile(e: React.ChangeEvent<HTMLInputElement>) {
    const f = e.target.files?.[0];
    if (!f) return;
    if (f.size > 2_000_000) {
      toast.error('文件超过 2MB');
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

  async function parse() {
    if (!json.trim()) {
      toast.error('请粘贴或上传 JSON');
      return;
    }
    try {
      const r = await mut.mutateAsync({ gatewayId: 'preview', openApiJson: json });
      setList(r);
      toast.success(`解析成功,${r.length} 个接口`);
    } catch (e) {
      toast.error(e instanceof ApiError ? e.message : '解析失败');
    }
  }

  function reset() {
    setJson('');
    setList([]);
    onClose();
  }

  return (
    <div
      className="fixed inset-0 z-50 bg-black/40 flex items-center justify-center"
      onClick={reset}
    >
      <div
        className="bg-[var(--bg-surface)] rounded-lg shadow-xl w-[760px] max-h-[80vh] flex flex-col"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="px-6 py-4 border-b border-[var(--border-default)] flex items-center justify-between">
          <div>
            <h3 className="text-lg font-semibold text-[var(--text-primary)]">用 Swagger 解析填充当前协议</h3>
            <p className="text-xs text-[var(--text-tertiary)] mt-1">点击列表中某个接口,字段将填入表单</p>
          </div>
          <button
            type="button"
            onClick={reset}
            className="text-[var(--text-tertiary)] hover:text-[var(--text-primary)]"
            aria-label="关闭"
          >
            <X size={20} />
          </button>
        </div>

        <div className="flex-1 overflow-auto p-6 space-y-4">
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
            <span className="text-xs text-[var(--text-tertiary)]">或粘贴</span>
          </div>

          <Textarea label="Swagger JSON" rows={6} value={json} onChange={(e) => setJson(e.target.value)} />
          <Button onClick={parse} loading={mut.isPending} disabled={!json.trim()}>解析</Button>

          {list.length > 0 && (
            <div className="border border-[var(--border-default)] rounded-md max-h-72 overflow-auto divide-y divide-[var(--border-default)]">
              {list.map((vo, i) => (
                <button
                  key={`${vo.httpMethod}-${vo.httpUrl}-${i}`}
                  type="button"
                  onClick={() => onPick(vo)}
                  className="w-full flex items-center gap-3 px-4 py-2 hover:bg-blue-50 text-left"
                >
                  <span className="text-xs font-mono uppercase text-[var(--brand)] w-14">
                    {vo.httpMethod}
                  </span>
                  <span className="text-sm font-mono flex-1 truncate text-[var(--text-primary)]">
                    {vo.httpUrl}
                  </span>
                  <span className="text-xs text-[var(--text-tertiary)]">{vo.mappings.length} 字段</span>
                </button>
              ))}
            </div>
          )}
        </div>

        <div className="px-6 py-4 border-t border-[var(--border-default)] flex justify-end">
          <Button variant="secondary" onClick={reset}>关闭</Button>
        </div>
      </div>
    </div>
  );
}
