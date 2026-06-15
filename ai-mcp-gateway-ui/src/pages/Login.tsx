import { useState, useRef, useEffect } from 'react';
import { useNavigate, Navigate, Link } from 'react-router-dom';
import { useAuthStore } from '../stores/auth';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { LogIn, Info } from 'lucide-react';

export default function Login() {
  const navigate = useNavigate();
  const { token, login } = useAuthStore();
  const [u, setU] = useState('admin');
  const [p, setP] = useState('password123');
  const [err, setErr] = useState<string | null>(null);
  const [shake, setShake] = useState(0);
  const [submitting, setSubmitting] = useState(false);
  const errTimer = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(() => () => { if (errTimer.current) clearTimeout(errTimer.current); }, []);

  if (token) return <Navigate to="/admin" replace />;

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setSubmitting(true);
    setErr(null);
    const ok = await login(u, p);
    setSubmitting(false);
    if (ok) navigate('/admin');
    else {
      setErr('账号或密码错误');
      setShake((n) => n + 1);
      if (errTimer.current) clearTimeout(errTimer.current);
      errTimer.current = setTimeout(() => setErr(null), 2500);
    }
  }

  return (
    <div className="min-h-screen w-screen flex items-center justify-center bg-[var(--bg-app)] p-4">
      <form
        key={shake}
        onSubmit={submit}
        className={`w-full max-w-[400px] bg-[var(--bg-surface)] border border-[var(--border-default)] rounded-lg p-8 shadow-[0_4px_12px_rgba(15,23,42,0.08)] ${shake > 0 ? 'animate-shake' : ''}`}
      >
        <div className="flex items-center gap-3 mb-6">
          <div className="w-10 h-10 rounded-md bg-[var(--brand)] flex items-center justify-center text-white">
            <LogIn size={20} />
          </div>
          <div>
            <h1 className="text-xl font-semibold text-[var(--text-primary)]">MCP Gateway</h1>
            <p className="text-xs text-[var(--text-tertiary)] mt-0.5">管理控制台登录</p>
          </div>
        </div>

        <div className="space-y-4">
          <Input
            label="用户名"
            value={u}
            onChange={(e) => setU(e.target.value)}
            autoFocus
            autoComplete="username"
            required
          />
          <Input
            label="密码"
            type="password"
            value={p}
            onChange={(e) => setP(e.target.value)}
            error={err ?? undefined}
            autoComplete="current-password"
            required
          />
        </div>

        <div className="mt-4 flex items-start gap-2 p-3 rounded-md bg-[var(--brand-soft)] border border-[var(--brand)]/20 text-xs text-[var(--text-secondary)]">
          <Info size={14} className="text-[var(--brand)] mt-0.5 shrink-0" />
          <span>测试账号 <code className="font-mono text-[var(--brand)]">admin</code> / <code className="font-mono text-[var(--brand)]">password123</code></span>
        </div>

        <Button type="submit" loading={submitting} className="w-full mt-5">
          登录
        </Button>

        <p className="text-center text-xs text-[var(--text-tertiary)] mt-4">
          登录即代表同意 <Link to="#" className="text-[var(--brand)] hover:underline">服务条款</Link>
        </p>
      </form>
    </div>
  );
}
