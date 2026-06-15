import { useLocation } from 'react-router-dom';
import { useAuthStore } from '../stores/auth';

const PATH_TITLE: Record<string, string> = {
  '/admin':           '控制台',
  '/admin/gateways':  '网关列表',
  '/admin/configs':   '基础配置',
  '/admin/tools':     '工具配置',
  '/admin/protocols': '协议配置',
  '/admin/auth':      '认证配置',
};

export function TopBar() {
  const { pathname } = useLocation();
  const { username } = useAuthStore();
  const title = PATH_TITLE[pathname] ?? 'MCP Gateway';

  return (
    <header className="h-14 px-6 flex items-center justify-between bg-[var(--bg-surface)] border-b border-[var(--border-default)]">
      <div className="text-sm text-[var(--text-tertiary)]">
        MCP Gateway <span className="mx-1.5 text-[var(--border-strong)]">/</span> <span className="text-[var(--text-primary)] font-medium">{title}</span>
      </div>
      <div className="flex items-center gap-2 text-sm text-[var(--text-secondary)]">
        <div className="w-7 h-7 rounded-full bg-[var(--brand-soft)] text-[var(--brand)] flex items-center justify-center text-xs font-semibold">
          {(username ?? 'A').slice(0, 1).toUpperCase()}
        </div>
        <span>{username ?? 'admin'}</span>
      </div>
    </header>
  );
}
