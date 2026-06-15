import { forwardRef, type SelectHTMLAttributes } from 'react';

export interface SelectOption {
  value: string | number;
  label: string;
}

interface Props extends Omit<SelectHTMLAttributes<HTMLSelectElement>, 'children'> {
  label?: string;
  error?: string;
  hint?: string;
  options: SelectOption[];
  placeholder?: string;
}

const BASE = 'block w-full rounded-md text-sm transition-colors focus:outline-none focus:ring-[3px] focus:ring-[rgba(37,99,235,0.25)] disabled:opacity-50 disabled:bg-[var(--bg-sunken)]';

export const Select = forwardRef<HTMLSelectElement, Props>(function Select(
  { label, error, hint, options, placeholder, className = '', id, ...rest },
  ref,
) {
  const sid = id ?? `sel-${Math.random().toString(36).slice(2, 9)}`;
  return (
    <div>
      {label && (
        <label htmlFor={sid} className="block text-[13px] font-medium text-[var(--text-primary)] mb-1.5">
          {label}
        </label>
      )}
      <select
        ref={ref}
        id={sid}
        aria-invalid={!!error}
        className={`${BASE} h-10 px-3 appearance-none bg-[var(--bg-surface)] border ${
          error ? 'border-[var(--danger)]' : 'border-[var(--border-default)]'
        } text-[var(--text-primary)] bg-no-repeat bg-right pr-9 ${className}`}
        style={{
          backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%2394a3b8' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E")`,
          backgroundPosition: 'right 0.75rem center',
        }}
        {...rest}
      >
        {placeholder && <option value="">{placeholder}</option>}
        {options.map((o) => (
          <option key={String(o.value)} value={o.value}>{o.label}</option>
        ))}
      </select>
      {error && <p className="mt-1 text-xs text-[var(--danger)]">{error}</p>}
      {!error && hint && <p className="mt-1 text-xs text-[var(--text-tertiary)]">{hint}</p>}
    </div>
  );
});
