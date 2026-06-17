import { useEffect, type ReactNode } from 'react';
import { X } from 'lucide-react';

interface Props {
  open: boolean;
  title: string;
  subtitle?: string;
  onClose: () => void;
  width?: 'sm' | 'md' | 'lg' | 'xl';
  children: ReactNode;
  footer?: ReactNode;
}

const WIDTH: Record<NonNullable<Props['width']>, string> = {
  sm: 'w-[480px]',
  md: 'w-[640px]',
  lg: 'w-[860px]',
  xl: 'w-[1024px]',
};

export function Modal({ open, title, subtitle, onClose, width = 'md', children, footer }: Props) {
  useEffect(() => {
    if (!open) return;
    const onEsc = (e: KeyboardEvent) => { if (e.key === 'Escape') onClose(); };
    window.addEventListener('keydown', onEsc);
    return () => window.removeEventListener('keydown', onEsc);
  }, [open, onClose]);

  useEffect(() => {
    if (!open) return;
    const prev = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
    return () => { document.body.style.overflow = prev; };
  }, [open]);

  if (!open) return null;

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4"
      onClick={onClose}
    >
      <div
        className={`bg-[var(--bg-surface)] rounded-lg shadow-xl ${WIDTH[width]} max-h-[90vh] flex flex-col`}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="px-6 py-4 border-b border-[var(--border-default)] flex items-start justify-between gap-4">
          <div>
            <h3 className="text-base font-semibold text-[var(--text-primary)] leading-tight">{title}</h3>
            {subtitle && <p className="text-xs text-[var(--text-tertiary)] mt-0.5">{subtitle}</p>}
          </div>
          <button
            type="button"
            onClick={onClose}
            className="text-[var(--text-tertiary)] hover:text-[var(--text-primary)] shrink-0"
            aria-label="关闭"
          >
            <X size={20} />
          </button>
        </div>
        <div className="flex-1 overflow-auto p-6">{children}</div>
        {footer && (
          <div className="px-6 py-4 border-t border-[var(--border-default)] flex items-center justify-end gap-2 bg-[var(--bg-sunken)] rounded-b-lg">
            {footer}
          </div>
        )}
      </div>
    </div>
  );
}
