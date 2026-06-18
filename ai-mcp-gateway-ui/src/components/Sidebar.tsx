import { NavLink, useNavigate } from 'react-router-dom';
import { LayoutDashboard, Server, Sliders, Wrench, Network, ShieldCheck, FlaskConical, LogOut } from 'lucide-react';
import { useAuthStore } from '../stores/auth';

type NavItem = { to: string; label: string; icon: typeof LayoutDashboard; end?: boolean };

const MAIN: NavItem[] = [
  { to: '/admin',           label: '控制台',     icon: LayoutDashboard, end: true },
  { to: '/admin/gateways',  label: '网关列表',   icon: Server },
];

const CONFIG: NavItem[] = [
  { to: '/admin/configs',   label: '基础配置',   icon: Sliders },
  { to: '/admin/tools',     label: '工具配置',   icon: Wrench },
  { to: '/admin/protocols', label: '协议配置',   icon: Network },
  { to: '/admin/auth',      label: '认证配置',   icon: ShieldCheck },
  { to: '/admin/llm-test',  label: 'LLM 测试',   icon: FlaskConical },
];

function NavGroup({ label, items }: { label: string; items: NavItem[] }) {
  return (
    <div>
      <div className="px-5 py-2 text-[11px] font-semibold uppercase tracking-wider text-[var(--text-tertiary)]">
        {label}
      </div>
      {items.map((it) => {
        const Icon = it.icon;
        return (
          <NavLink
            key={it.to}
            to={it.to}
            end={it.end}
            className={({ isActive }) =>
              `flex items-center gap-3 px-5 py-2 text-sm transition-colors ${
                isActive
                  ? 'bg-[var(--brand-soft)] text-[var(--brand)] font-medium border-l-[3px] border-[var(--brand)]'
                  : 'text-[var(--text-secondary)] hover:bg-[var(--bg-sunken)] border-l-[3px] border-transparent'
              }`
            }
          >
            <Icon size={16} />
            <span>{it.label}</span>
          </NavLink>
        );
      })}
    </div>
  );
}

export function Sidebar() {
  const navigate = useNavigate();
  const { username, logout } = useAuthStore();

  return (
    <aside className="w-[240px] shrink-0 flex flex-col bg-[var(--bg-surface)] border-r border-[var(--border-default)]">
      <div className="px-5 py-5 flex items-center gap-3 border-b border-[var(--border-default)]">
        <div className="w-8 h-8 rounded-md bg-[var(--brand)] flex items-center justify-center text-white font-semibold text-sm">
          MG
        </div>
        <div>
          <div className="text-sm font-semibold text-[var(--text-primary)] leading-none">MCP Gateway</div>
          <div className="text-[11px] text-[var(--text-tertiary)] mt-1">v1.0 · {username ?? 'admin'}</div>
        </div>
      </div>

      <nav className="flex-1 py-3 overflow-y-auto">
        <NavGroup label="MAIN" items={MAIN} />
        <div className="mt-4">
          <NavGroup label="配置" items={CONFIG} />
        </div>
      </nav>

      <div className="py-3 border-t border-[var(--border-default)]">
        <button
          onClick={() => { logout(); navigate('/login'); }}
          className="w-full flex items-center gap-3 px-5 py-2 text-sm text-[var(--text-secondary)] hover:bg-[var(--bg-sunken)] transition-colors"
        >
          <LogOut size={14} />
          <span>退出登录</span>
        </button>
      </div>
    </aside>
  );
}
