import type { ReactNode } from 'react';

interface Props {
  title?: string;
  subtitle?: string;
  actions?: ReactNode;
  padding?: 'none' | 'sm' | 'md' | 'lg';
  className?: string;
  children: ReactNode;
}

const PAD = { none: '', sm: 'p-4', md: 'p-6', lg: 'p-8' } as const;

export function Card({ title, subtitle, actions, padding = 'md', className = '', children }: Props) {
  return (
    <section
      className={`bg-[var(--bg-surface)] border border-[var(--border-default)] rounded-lg shadow-[0_1px_2px_rgba(15,23,42,0.04),0_1px_3px_rgba(15,23,42,0.06)] ${className}`}
    >
      {(title || actions) && (
        <header className="flex items-start justify-between gap-4 px-6 py-4 border-b border-[var(--border-default)]">
          <div>
            {title && <h2 className="text-[18px] font-semibold text-[var(--text-primary)] leading-tight">{title}</h2>}
            {subtitle && <p className="text-xs text-[var(--text-tertiary)] mt-0.5">{subtitle}</p>}
          </div>
          {actions && <div className="flex items-center gap-2">{actions}</div>}
        </header>
      )}
      <div className={PAD[padding]}>{children}</div>
    </section>
  );
}
