import { Inbox } from 'lucide-react';
import type { ReactNode } from 'react';

interface Props {
  icon?: ReactNode;
  title: string;
  desc?: string;
  action?: ReactNode;
}

export function EmptyState({ icon, title, desc, action }: Props) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center">
      <div className="w-12 h-12 rounded-full bg-[var(--bg-sunken)] text-[var(--text-tertiary)] flex items-center justify-center mb-4">
        {icon ?? <Inbox size={20} />}
      </div>
      <p className="text-sm font-medium text-[var(--text-primary)]">{title}</p>
      {desc && <p className="text-xs text-[var(--text-tertiary)] mt-1 max-w-sm">{desc}</p>}
      {action && <div className="mt-4">{action}</div>}
    </div>
  );
}
