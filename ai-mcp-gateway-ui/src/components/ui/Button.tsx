import { forwardRef, type ButtonHTMLAttributes, type ReactNode } from 'react';
import { Spinner } from './Spinner';

type Variant = 'primary' | 'secondary' | 'ghost' | 'danger';
type Size = 'sm' | 'md';

interface Props extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant;
  size?: Size;
  loading?: boolean;
  leftIcon?: ReactNode;
}

const BASE = 'inline-flex items-center justify-center gap-2 font-medium rounded-md transition-all duration-150 disabled:opacity-50 disabled:cursor-not-allowed focus:outline-none focus:ring-[3px] focus:ring-[rgba(37,99,235,0.25)]';
const VARIANT: Record<Variant, string> = {
  primary:   'bg-[var(--brand)] text-white hover:bg-[var(--brand-hover)]',
  secondary: 'bg-[var(--bg-surface)] text-[var(--text-primary)] border border-[var(--border-default)] hover:bg-[var(--bg-sunken)]',
  ghost:     'bg-transparent text-[var(--text-secondary)] hover:bg-[var(--bg-sunken)]',
  danger:    'bg-[var(--danger)] text-white hover:opacity-90',
};
const SIZE: Record<Size, string> = {
  sm: 'h-8 px-3 text-[13px]',
  md: 'h-10 px-4 text-sm',
};

export const Button = forwardRef<HTMLButtonElement, Props>(function Button(
  { variant = 'primary', size = 'md', loading, leftIcon, children, className = '', disabled, ...rest },
  ref,
) {
  return (
    <button
      ref={ref}
      disabled={disabled || loading}
      className={`${BASE} ${VARIANT[variant]} ${SIZE[size]} ${className}`}
      {...rest}
    >
      {loading ? <Spinner size="sm" /> : leftIcon}
      {children}
    </button>
  );
});
