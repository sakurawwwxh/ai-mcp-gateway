import { forwardRef, type TextareaHTMLAttributes } from 'react';

interface Props extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string;
  error?: string;
  hint?: string;
}

const BASE = 'block w-full rounded-md text-sm transition-colors focus:outline-none focus:ring-[3px] focus:ring-[rgba(37,99,235,0.25)] disabled:opacity-50 disabled:bg-[var(--bg-sunken)]';

export const Textarea = forwardRef<HTMLTextAreaElement, Props>(function Textarea(
  { label, error, hint, className = '', id, rows = 4, ...rest },
  ref,
) {
  const tid = id ?? `ta-${Math.random().toString(36).slice(2, 9)}`;
  return (
    <div>
      {label && (
        <label htmlFor={tid} className="block text-[13px] font-medium text-[var(--text-primary)] mb-1.5">
          {label}
        </label>
      )}
      <textarea
        ref={ref}
        id={tid}
        rows={rows}
        aria-invalid={!!error}
        className={`${BASE} px-3 py-2 resize-y ${
          error
            ? 'border border-[var(--danger)] bg-[var(--bg-surface)] text-[var(--text-primary)]'
            : 'border border-[var(--border-default)] bg-[var(--bg-surface)] text-[var(--text-primary)] placeholder:text-[var(--text-tertiary)]'
        } ${className}`}
        {...rest}
      />
      {error && <p className="mt-1 text-xs text-[var(--danger)]">{error}</p>}
      {!error && hint && <p className="mt-1 text-xs text-[var(--text-tertiary)]">{hint}</p>}
    </div>
  );
});
