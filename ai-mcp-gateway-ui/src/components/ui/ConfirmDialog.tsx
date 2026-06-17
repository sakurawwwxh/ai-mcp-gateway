import { useEffect } from 'react';
import { AlertTriangle, X } from 'lucide-react';
import { Button } from './Button';

interface Props {
  open: boolean;
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  danger?: boolean;
  onConfirm: () => void;
  onCancel: () => void;
}

export function ConfirmDialog({
  open,
  title,
  message,
  confirmText = '确认',
  cancelText = '取消',
  danger = false,
  onConfirm,
  onCancel,
}: Props) {
  useEffect(() => {
    if (!open) return;
    const onEsc = (e: KeyboardEvent) => { if (e.key === 'Escape') onCancel(); };
    window.addEventListener('keydown', onEsc);
    return () => window.removeEventListener('keydown', onEsc);
  }, [open, onCancel]);

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
      <div className="bg-[var(--bg-surface)] rounded-lg shadow-xl w-full max-w-md mx-4">
        <div className="flex items-start justify-between p-4 border-b border-[var(--border-default)]">
          <div className="flex items-start gap-3">
            {danger && (
              <span className="flex-shrink-0 w-9 h-9 rounded-full bg-red-50 flex items-center justify-center">
                <AlertTriangle size={18} className="text-red-600" />
              </span>
            )}
            <h3 className="text-base font-semibold text-[var(--text-primary)] pt-1">{title}</h3>
          </div>
          <button
            type="button"
            onClick={onCancel}
            className="text-[var(--text-tertiary)] hover:text-[var(--text-primary)] p-1"
            aria-label="关闭"
          >
            <X size={18} />
          </button>
        </div>
        <div className="p-4 text-sm text-[var(--text-secondary)] leading-relaxed whitespace-pre-line">
          {message}
        </div>
        <div className="flex items-center justify-end gap-2 p-4 border-t border-[var(--border-default)] bg-[var(--bg-sunken)] rounded-b-lg">
          <Button variant="secondary" size="sm" onClick={onCancel}>
            {cancelText}
          </Button>
          <Button variant={danger ? 'danger' : 'primary'} size="sm" onClick={onConfirm}>
            {confirmText}
          </Button>
        </div>
      </div>
    </div>
  );
}