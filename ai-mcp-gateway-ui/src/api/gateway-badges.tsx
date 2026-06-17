import { Wrench, ShieldCheck, Link2, Pencil, type LucideIcon } from 'lucide-react';
import { Badge } from '../components/ui/Badge';
import type { Column } from '../components/ui/Table';
import type { GatewayConfigDTO } from './schemas/gateway';

export function authBadge(auth: string) {
  if (auth === 'STRONG_VERIFIED') return <Badge variant="info">强校验</Badge>;
  if (auth === 'NOT_VERIFIED')   return <Badge variant="neutral">不校验</Badge>;
  return <Badge variant="neutral">{auth || '—'}</Badge>;
}

export function statusBadge(status: string) {
  if (status === 'ENABLE')  return <Badge variant="success">启用</Badge>;
  if (status === 'DISABLE') return <Badge variant="warning">禁用</Badge>;
  return <Badge variant="neutral">{status || '—'}</Badge>;
}

export interface GatewayActionHandlers {
  onViewTools?: (g: GatewayConfigDTO) => void;
  onViewAuth?:  (g: GatewayConfigDTO) => void;
  onCopySse?:   (g: GatewayConfigDTO) => void;
  onEdit?:      (g: GatewayConfigDTO) => void;
}

type Tone = 'blue' | 'emerald' | 'amber' | 'violet';
const TONE_CLS: Record<Tone, string> = {
  blue:    'text-blue-600 hover:bg-blue-50',
  emerald: 'text-emerald-600 hover:bg-emerald-50',
  amber:   'text-amber-600 hover:bg-amber-50',
  violet:  'text-violet-600 hover:bg-violet-50',
};

function IconBtn({ onClick, Icon, title, tone }: {
  onClick: () => void; Icon: LucideIcon; title: string; tone: Tone;
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      title={title}
      aria-label={title}
      className={`inline-flex items-center justify-center w-7 h-7 rounded ${TONE_CLS[tone]}`}
    >
      <Icon size={14} />
    </button>
  );
}

const baseColumns: Column<GatewayConfigDTO>[] = [
  { key: 'id',     header: 'ID',   width: '140px', render: (g) => <span className="font-mono text-[var(--text-secondary)]">{g.gatewayId}</span> },
  { key: 'name',   header: '名称',                 render: (g) => <span className="font-medium">{g.name}</span> },
  { key: 'desc',   header: '描述',                 render: (g) => <span className="text-[var(--text-secondary)]">{g.desc || '—'}</span> },
  { key: 'ver',    header: '版本', width: '90px', render: (g) => <span className="font-mono text-xs text-[var(--text-secondary)]">{g.version}</span> },
  { key: 'auth',   header: '认证', width: '90px', render: (g) => authBadge(g.auth) },
  { key: 'status', header: '状态', width: '90px', render: (g) => statusBadge(g.status) },
];

/** 保留旧导出供 Dashboard 等已有调用方使用 (无操作列) */
export const gatewayColumns = baseColumns;

/** 带操作列的工厂函数；handlers 全空时回退为 baseColumns (保持旧行为) */
export function makeGatewayColumns(h: GatewayActionHandlers = {}): Column<GatewayConfigDTO>[] {
  const hasHandlers = h.onViewTools || h.onViewAuth || h.onCopySse || h.onEdit;
  if (!hasHandlers) return baseColumns;
  return [
    ...baseColumns,
    {
      key: 'act',
      header: '操作',
      width: '200px',
      align: 'center',
      render: (g) => (
        <div className="flex items-center justify-center gap-1">
          {h.onViewTools && <IconBtn onClick={() => h.onViewTools!(g)} Icon={Wrench}     title="查看工具"     tone="emerald" />}
          {h.onViewAuth  && <IconBtn onClick={() => h.onViewAuth!(g)}  Icon={ShieldCheck} title="查看认证"     tone="amber" />}
          {h.onCopySse   && <IconBtn onClick={() => h.onCopySse!(g)}   Icon={Link2}      title="复制 SSE 地址" tone="violet" />}
          {h.onEdit      && <IconBtn onClick={() => h.onEdit!(g)}      Icon={Pencil}     title="编辑"         tone="blue" />}
        </div>
      ),
    },
  ];
}
