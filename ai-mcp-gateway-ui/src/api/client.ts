import axios, { AxiosError, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios';
import { useAuthStore } from '../stores/auth';

// axios 1.x 默认用 XMLHttpRequest（MSW 不拦截），改用 fetch 让 MSW 能拦截
axios.defaults.adapter = 'fetch';

export const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api-gateway',
  timeout: 8000,
  headers: { 'Content-Type': 'application/json' },
});

/* === Auth header === */

client.interceptors.request.use((req: InternalAxiosRequestConfig) => {
  const token = useAuthStore.getState().token;
  if (token) req.headers.set('X-Mock-Token', token);
  return req;
});

/* === Business code check + 401 redirect === */

export class ApiError extends Error {
  constructor(
    public code: 'NETWORK' | 'UNAUTHORIZED' | 'BUSINESS' | 'VALIDATION' | 'NOT_FOUND' | 'CONFLICT' | 'SERVER' | 'UNKNOWN',
    public httpStatus: number,
    public businessCode: string,
    message: string,
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

interface BodyShape { code?: string; info?: string; data?: unknown }

function unauthorized() {
  useAuthStore.getState().logout();
  setTimeout(() => { window.location.href = '/login'; }, 0);
}

client.interceptors.response.use(
  (res: AxiosResponse<BodyShape>) => {
    if (res.status === 401) {
      unauthorized();
      throw new ApiError('UNAUTHORIZED', 401, '1003', res.data?.info || '会话过期');
    }
    const code = res.data?.code;
    if (code === '0000') return res.data.data as any;
    if (code === '1003') {
      unauthorized();
      throw new ApiError('UNAUTHORIZED', res.status, code, res.data?.info || '登录已过期');
    }
    throw new ApiError('BUSINESS', res.status, code ?? 'UNKNOWN', res.data?.info || `业务错误 ${code}`);
  },
  (err: AxiosError<BodyShape>) => {
    if (!err.response) {
      return Promise.reject(new ApiError('NETWORK', 0, 'NETWORK', err.message || '网络异常'));
    }
    const { status, data } = err.response;
    if (status === 401) {
      unauthorized();
      return Promise.reject(new ApiError('UNAUTHORIZED', 401, data?.code ?? '1003', data?.info || '会话过期'));
    }
    if (status === 400) return Promise.reject(new ApiError('VALIDATION', 400, data?.code ?? '', data?.info || '参数错误'));
    if (status === 404) return Promise.reject(new ApiError('NOT_FOUND', 404, data?.code ?? '', data?.info || '资源不存在'));
    if (status === 409) return Promise.reject(new ApiError('CONFLICT', 409, data?.code ?? '', data?.info || '资源冲突'));
    if (status >= 500) return Promise.reject(new ApiError('SERVER', status, data?.code ?? '', data?.info || '服务器错误'));
    return Promise.reject(new ApiError('UNKNOWN', status, data?.code ?? '', data?.info || '未知错误'));
  },
);
