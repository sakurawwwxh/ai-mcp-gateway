import { Loader2 } from 'lucide-react';

interface Props {
  size?: 'sm' | 'md';
  className?: string;
}

const SIZE: Record<NonNullable<Props['size']>, number> = { sm: 14, md: 20 };

export function Spinner({ size = 'md', className = '' }: Props) {
  return (
    <Loader2
      size={SIZE[size]}
      className={`animate-spin text-[var(--brand)] ${className}`}
      aria-label="加载中"
    />
  );
}
