import { Routes, Route, Navigate } from 'react-router-dom';
import { lazy, Suspense } from 'react';
import { useAuthStore } from './stores/auth';
import { AppLayout } from './components/AppLayout';

const Login = lazy(() => import('./pages/Login'));
const Dashboard = lazy(() => import('./pages/Dashboard'));
const Gateways = lazy(() => import('./pages/Gateways'));
const Configs = lazy(() => import('./pages/Configs'));
const Tools = lazy(() => import('./pages/Tools'));
const Protocols = lazy(() => import('./pages/Protocols'));
const Auth = lazy(() => import('./pages/Auth'));

function RequireAuth({ children }: { children: React.ReactNode }) {
  const token = useAuthStore((s) => s.token);
  if (!token) return <Navigate to="/login" replace />;
  return <>{children}</>;
}

export default function App() {
  return (
    <Suspense fallback={
          <div className="min-h-screen w-screen flex items-center justify-center bg-[var(--bg-app)]">
            <div className="text-sm text-[var(--text-tertiary)]">加载中…</div>
          </div>
        }>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route
          path="/admin/*"
          element={
            <RequireAuth>
              <AppLayout />
            </RequireAuth>
          }
        >
          <Route index element={<Dashboard />} />
          <Route path="gateways" element={<Gateways />} />
          <Route path="configs" element={<Configs />} />
          <Route path="tools" element={<Tools />} />
          <Route path="protocols" element={<Protocols />} />
          <Route path="auth" element={<Auth />} />
        </Route>
        <Route path="*" element={<Navigate to="/admin" replace />} />
      </Routes>
    </Suspense>
  );
}
