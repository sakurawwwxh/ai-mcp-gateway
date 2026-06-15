import { AlertCircle } from 'lucide-react';
import { Button } from './Button';

interface Props {
  title: string;
  desc?: string;
  onRetry?: () => void;
}

export function ErrorState({ title, desc, onRetry }: Props) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center">
      <div className="w-12 h-12 rounded-full bg-rose-50 text-[var(--danger)] flex items-center justify-center mb-4">
        <AlertCircle size={20} />
      </div>
      <p className="text-sm font-medium text-[var(--text-primary)]">{title}</p>
      {desc && <p className="text-xs text-[var(--text-tertiary)] mt-1 max-w-sm">{desc}</p>}
      {onRetry && <Button variant="secondary" size="sm" className="mt-4" onClick={onRetry}>重试</Button>}
    </div>
  );
}
