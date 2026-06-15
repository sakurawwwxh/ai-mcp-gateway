import type { ReactNode } from 'react';

interface Props {
  title: string;
  subtitle?: string;
  actions?: ReactNode;
}

export function PageHeader({ title, subtitle, actions }: Props) {
  return (
    <header className="flex items-start justify-between gap-4 pb-6 mb-6 border-b border-[var(--border-default)]">
      <div>
        <h1 className="text-2xl font-semibold text-[var(--text-primary)] leading-tight">{title}</h1>
        {subtitle && <p className="text-sm text-[var(--text-secondary)] mt-1">{subtitle}</p>}
      </div>
      {actions && <div className="flex items-center gap-2">{actions}</div>}
    </header>
  );
}
