import { forwardRef, type InputHTMLAttributes, type ReactNode } from 'react';

interface Props extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  hint?: string;
  leftIcon?: ReactNode;
  monospace?: boolean;
}

const BASE = 'block w-full rounded-md text-sm transition-colors focus:outline-none focus:ring-[3px] focus:ring-[rgba(37,99,235,0.25)] disabled:opacity-50 disabled:bg-[var(--bg-sunken)]';

export const Input = forwardRef<HTMLInputElement, Props>(function Input(
  { label, error, hint, leftIcon, monospace, className = '', id, ...rest },
  ref,
) {
  const inputId = id ?? `input-${Math.random().toString(36).slice(2, 9)}`;
  return (
    <div>
      {label && (
        <label htmlFor={inputId} className="block text-[13px] font-medium text-[var(--text-primary)] mb-1.5">
          {label}
        </label>
      )}
      <div className="relative">
        {leftIcon && (
          <span className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--text-tertiary)] pointer-events-none">
            {leftIcon}
          </span>
        )}
        <input
          ref={ref}
          id={inputId}
          aria-invalid={!!error}
          className={`${BASE} h-10 px-3 ${leftIcon ? 'pl-10' : ''} ${monospace ? 'font-mono' : ''} ${
            error
              ? 'border border-[var(--danger)] bg-[var(--bg-surface)] text-[var(--text-primary)]'
              : 'border border-[var(--border-default)] bg-[var(--bg-surface)] text-[var(--text-primary)] placeholder:text-[var(--text-tertiary)]'
          } ${className}`}
          {...rest}
        />
      </div>
      {error && <p className="mt-1 text-xs text-[var(--danger)]">{error}</p>}
      {!error && hint && <p className="mt-1 text-xs text-[var(--text-tertiary)]">{hint}</p>}
    </div>
  );
});
