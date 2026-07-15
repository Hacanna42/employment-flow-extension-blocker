import { useCallback, useEffect, useState } from 'react';
import { extensionApi } from '@/api';
import type { CustomExtension } from '@/types/extension';

export function useCustomExtensions() {
  const [customExtensions, setCustomExtensions] = useState<CustomExtension[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let alive = true;
    extensionApi
      .getCustomExtensions()
      .then((data) => alive && setCustomExtensions(data))
      .catch((e: Error) => alive && setError(e.message))
      .finally(() => alive && setIsLoading(false));
    return () => {
      alive = false;
    };
  }, []);

  const add = useCallback(async (name: string): Promise<boolean> => {
    setError(null);
    try {
      const created = await extensionApi.addCustomExtension(name);
      setCustomExtensions((prev) => [...prev, created]);
      return true;
    } catch (e) {
      setError(e instanceof Error ? e.message : '추가에 실패했어요. 다시 시도해 주세요.');
      return false;
    }
  }, []);

  const remove = useCallback(async (id: number) => {
    setError(null);
    const snapshot = customExtensions;
    setCustomExtensions((prev) => prev.filter((item) => item.id !== id));
    try {
      await extensionApi.deleteCustomExtension(id);
    } catch (e) {
      setCustomExtensions(snapshot);
      setError(e instanceof Error ? e.message : '삭제에 실패했어요. 다시 시도해 주세요.');
    }
  }, [customExtensions]);

  return { customExtensions, isLoading, error, add, remove };
}
