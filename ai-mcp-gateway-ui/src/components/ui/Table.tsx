import type { ReactNode } from 'react';
import { Spinner } from './Spinner';
import { EmptyState } from './EmptyState';
import { ErrorState } from './ErrorState';

export interface Column<T> {
  key: string;
  header: ReactNode;
  width?: string;
  align?: 'left' | 'right' | 'center';
  render: (row: T) => ReactNode;
}

interface Props<T> {
  columns: Column<T>[];
  data: T[];
  rowKey: (row: T) => string;
  loading?: boolean;
  error?: string | null;
  onRetry?: () => void;
  emptyTitle?: string;
  emptyDesc?: string;
  emptyAction?: ReactNode;
}

const ALIGN: Record<NonNullable<Column<unknown>['align']>, string> = {
  left: 'text-left', right: 'text-right', center: 'text-center',
};

export function Table<T>({
  columns, data, rowKey, loading, error, onRetry,
  emptyTitle = '暂无数据', emptyDesc, emptyAction,
}: Props<T>) {
  return (
    <div className="overflow-x-auto">
      <table className="w-full text-[13px]">
        <thead className="bg-[var(--bg-sunken)] border-b border-[var(--border-default)]">
          <tr>
            {columns.map((c) => (
              <th
                key={c.key}
                style={c.width ? { width: c.width } : undefined}
                className={`px-4 py-2.5 text-xs font-semibold uppercase tracking-wider text-[var(--text-tertiary)] ${ALIGN[c.align ?? 'left']}`}
              >
                {c.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="divide-y divide-[var(--border-default)]">
          {loading && (
            <tr>
              <td colSpan={columns.length} className="px-4 py-12 text-center">
                <div className="inline-flex items-center gap-2 text-sm text-[var(--text-secondary)]">
                  <Spinner size="sm" /> 加载中…
                </div>
              </td>
            </tr>
          )}
          {!loading && error && (
            <tr>
              <td colSpan={columns.length}>
                <ErrorState title="加载失败" desc={error} onRetry={onRetry} />
              </td>
            </tr>
          )}
          {!loading && !error && data.length === 0 && (
            <tr>
              <td colSpan={columns.length}>
                <EmptyState title={emptyTitle} desc={emptyDesc} action={emptyAction} />
              </td>
            </tr>
          )}
          {!loading && !error && data.map((row) => (
            <tr key={rowKey(row)} className="hover:bg-[var(--brand-soft)]/40 transition-colors">
              {columns.map((c) => (
                <td
                  key={c.key}
                  className={`px-4 py-3 leading-relaxed text-[var(--text-primary)] ${ALIGN[c.align ?? 'left']}`}
                >
                  {c.render(row)}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
