import { ChevronLeft, ChevronRight } from 'lucide-react';

interface Props {
  page: number;
  rows: number;
  total: number;
  onChange: (page: number) => void;
  onRowsChange?: (rows: number) => void;
  rowsOptions?: number[];
}

export function Pagination({
  page, rows, total, onChange, onRowsChange, rowsOptions = [10, 20, 50],
}: Props) {
  const totalPages = Math.max(1, Math.ceil(total / rows));
  const canPrev = page > 1;
  const canNext = page < totalPages;

  return (
    <div className="flex items-center justify-between px-4 py-3 border-t border-[var(--border-default)] text-[13px]">
      <span className="text-[var(--text-secondary)]">
        共 <span className="font-medium text-[var(--text-primary)]">{total}</span> 条 · 第{' '}
        <span className="font-medium text-[var(--text-primary)]">{page}</span> / {totalPages} 页
      </span>
      <div className="flex items-center gap-2">
        {onRowsChange && (
          <select
            value={rows}
            onChange={(e) => onRowsChange(Number(e.target.value))}
            className="h-8 px-2 rounded border border-[var(--border-default)] bg-[var(--bg-surface)] text-[13px] text-[var(--text-primary)] focus:outline-none focus:ring-[2px] focus:ring-[rgba(37,99,235,0.25)]"
          >
            {rowsOptions.map((n) => (
              <option key={n} value={n}>{n} / 页</option>
            ))}
          </select>
        )}
        <button
          type="button"
          disabled={!canPrev}
          onClick={() => canPrev && onChange(page - 1)}
          className="inline-flex items-center gap-1 h-8 px-3 border border-[var(--border-default)] rounded text-[var(--text-primary)] bg-[var(--bg-surface)] hover:bg-[var(--bg-sunken)] disabled:opacity-40 disabled:cursor-not-allowed"
        >
          <ChevronLeft size={14} />
          上一页
        </button>
        <button
          type="button"
          disabled={!canNext}
          onClick={() => canNext && onChange(page + 1)}
          className="inline-flex items-center gap-1 h-8 px-3 border border-[var(--border-default)] rounded text-[var(--text-primary)] bg-[var(--bg-surface)] hover:bg-[var(--bg-sunken)] disabled:opacity-40 disabled:cursor-not-allowed"
        >
          下一页
          <ChevronRight size={14} />
        </button>
      </div>
    </div>
  );
}
