import type { ReactNode } from 'react';

interface Props {
  title?: string;
  desc?: string;
  children: ReactNode;
  className?: string;
}

export function FormSection({ title, desc, children, className = '' }: Props) {
  return (
    <div className={`pt-6 first:pt-0 ${className}`}>
      {title && (
        <div className="mb-4 pb-3 border-b border-[var(--border-default)]">
          <h3 className="text-sm font-semibold text-[var(--text-primary)]">{title}</h3>
          {desc && <p className="text-xs text-[var(--text-tertiary)] mt-0.5">{desc}</p>}
        </div>
      )}
      <div className="space-y-4">{children}</div>
    </div>
  );
}
