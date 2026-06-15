import { createContext, useCallback, useContext, useState, useRef, type ReactNode } from 'react';
import { CheckCircle2, XCircle, Info, X } from 'lucide-react';

type ToastVariant = 'success' | 'error' | 'info';

interface Toast {
  id: number;
  variant: ToastVariant;
  message: string;
}

interface ToastApi {
  success: (msg: string) => void;
  error: (msg: string) => void;
  info: (msg: string) => void;
}

const Ctx = createContext<ToastApi | null>(null);

const ICONS: Record<ToastVariant, JSX.Element> = {
  success: <CheckCircle2 size={16} className="text-emerald-600" />,
  error:   <XCircle    size={16} className="text-rose-600" />,
  info:    <Info       size={16} className="text-sky-600" />,
};

const BAR: Record<ToastVariant, string> = {
  success: 'border-l-emerald-500',
  error:   'border-l-rose-500',
  info:    'border-l-sky-500',
};

export function ToastProvider({ children }: { children: ReactNode }) {
  const [items, setItems] = useState<Toast[]>([]);
  const counter = useRef(0);

  const push = useCallback((variant: ToastVariant, message: string) => {
    const id = ++counter.current;
    setItems((prev) => [...prev, { id, variant, message }]);
    setTimeout(() => {
      setItems((prev) => prev.filter((t) => t.id !== id));
    }, 3000);
  }, []);

  const api: ToastApi = {
    success: (m) => push('success', m),
    error:   (m) => push('error',   m),
    info:    (m) => push('info',    m),
  };

  return (
    <Ctx.Provider value={api}>
      {children}
      <div className="fixed bottom-6 right-6 z-50 flex flex-col-reverse gap-2 pointer-events-none">
        {items.map((t) => (
          <div
            key={t.id}
            className={`pointer-events-auto flex items-center gap-2 pl-3 pr-2 py-2.5 min-w-[260px] max-w-sm bg-[var(--bg-surface)] border border-[var(--border-default)] border-l-4 ${BAR[t.variant]} rounded-md shadow-[0_4px_12px_rgba(15,23,42,0.08)] animate-in fade-in slide-in-from-bottom-2`}
            role="status"
          >
            {ICONS[t.variant]}
            <span className="flex-1 text-sm text-[var(--text-primary)]">{t.message}</span>
            <button
              onClick={() => setItems((prev) => prev.filter((x) => x.id !== t.id))}
              className="text-[var(--text-tertiary)] hover:text-[var(--text-primary)]"
              aria-label="关闭"
            >
              <X size={14} />
            </button>
          </div>
        ))}
      </div>
    </Ctx.Provider>
  );
}

export function useToast(): ToastApi {
  const ctx = useContext(Ctx);
  if (!ctx) throw new Error('useToast must be used within ToastProvider');
  return ctx;
}
