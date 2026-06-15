import type { ReactNode } from 'react';

type Variant = 'success' | 'warning' | 'danger' | 'info' | 'neutral';

interface Props {
  variant?: Variant;
  children: ReactNode;
  className?: string;
}

const STYLE: Record<Variant, { bg: string; text: string; border: string }> = {
  success: { bg: 'bg-emerald-50', text: 'text-emerald-700', border: 'border-emerald-200' },
  warning: { bg: 'bg-amber-50',  text: 'text-amber-700',  border: 'border-amber-200' },
  danger:  { bg: 'bg-rose-50',   text: 'text-rose-700',   border: 'border-rose-200' },
  info:    { bg: 'bg-sky-50',    text: 'text-sky-700',    border: 'border-sky-200' },
  neutral: { bg: 'bg-slate-100', text: 'text-slate-600',  border: 'border-slate-200' },
};

export function Badge({ variant = 'neutral', children, className = '' }: Props) {
  const s = STYLE[variant];
  return (
    <span className={`inline-flex items-center gap-1 px-2 py-0.5 text-[11px] font-medium rounded border ${s.bg} ${s.text} ${s.border} ${className}`}>
      {children}
    </span>
  );
}
