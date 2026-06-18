import { createBrowserRouter, Navigate, RouterProvider } from 'react-router-dom';
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
const LlmTest = lazy(() => import('./pages/LlmTest'));

function RequireAuth({ children }: { children: React.ReactNode }) {
  const token = useAuthStore((s) => s.token);
  if (!token) return <Navigate to="/login" replace />;
  return <>{children}</>;
}

const Fallback = (
  <div className="min-h-screen w-screen flex items-center justify-center bg-[var(--bg-app)]">
    <div className="text-sm text-[var(--text-tertiary)]">加载中…</div>
  </div>
);

const router = createBrowserRouter([
  { path: '/login', element: <Suspense fallback={Fallback}><Login /></Suspense> },
  {
    path: '/admin',
    element: <RequireAuth><AppLayout /></RequireAuth>,
    children: [
      { index: true, element: <Suspense fallback={Fallback}><Dashboard /></Suspense> },
      { path: 'gateways',  element: <Suspense fallback={Fallback}><Gateways  /></Suspense> },
      { path: 'configs',   element: <Suspense fallback={Fallback}><Configs   /></Suspense> },
      { path: 'tools',     element: <Suspense fallback={Fallback}><Tools     /></Suspense> },
      { path: 'protocols', element: <Suspense fallback={Fallback}><Protocols /></Suspense> },
      { path: 'auth',      element: <Suspense fallback={Fallback}><Auth      /></Suspense> },
      { path: 'llm-test',  element: <Suspense fallback={Fallback}><LlmTest  /></Suspense> },
    ],
  },
  { path: '*', element: <Navigate to="/admin" replace /> },
]);

export default function App() {
  return <RouterProvider router={router} />;
}
