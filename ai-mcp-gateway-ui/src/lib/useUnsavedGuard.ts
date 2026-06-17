import { useEffect } from 'react';
import { useBlocker } from 'react-router-dom';

/**
 * 表单 dirty 时拦截路由跳转 + 浏览器离开。
 * 用法: useUnsavedGuard(form.formState.isDirty && formOpen)
 */
export function useUnsavedGuard(when: boolean) {
  const blocker = useBlocker(when);

  useEffect(() => {
    if (!when) return;
    const onBeforeUnload = (e: BeforeUnloadEvent) => {
      e.preventDefault();
      e.returnValue = '';
    };
    window.addEventListener('beforeunload', onBeforeUnload);
    return () => window.removeEventListener('beforeunload', onBeforeUnload);
  }, [when]);

  return blocker;
}
