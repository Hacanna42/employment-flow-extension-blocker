import { useCallback, useEffect, useState } from 'react';
import { extensionApi } from '@/api';
import type { CustomExtension } from '@/types/extension';
import { readCache, writeCache } from '@/utils/localCache';

const CACHE_KEY = 'custom-extensions';

export function useCustomExtensions() {
  const [customExtensions, setCustomExtensions] = useState<CustomExtension[]>(
    () => readCache<CustomExtension[]>(CACHE_KEY) ?? [],
  );
  const [isLoading, setIsLoading] = useState(() => readCache<CustomExtension[]>(CACHE_KEY) === null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let alive = true;
    extensionApi
      .getCustomExtensions()
      .then((data) => {
        if (!alive) return;
        setCustomExtensions(data);
        writeCache(CACHE_KEY, data);
      })
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
      setCustomExtensions((prev) => {
        const next = [...prev, created];
        writeCache(CACHE_KEY, next);
        return next;
      });
      return true;
    } catch (e) {
      setError(e instanceof Error ? e.message : '추가에 실패했어요. 다시 시도해 주세요.');
      return false;
    }
  }, []);

  const remove = useCallback(async (id: number) => {
    setError(null);
    const snapshot = customExtensions;
    setCustomExtensions((prev) => {
      const next = prev.filter((item) => item.id !== id);
      writeCache(CACHE_KEY, next);
      return next;
    });
    try {
      await extensionApi.deleteCustomExtension(id);
    } catch (e) {
      setCustomExtensions(snapshot);
      writeCache(CACHE_KEY, snapshot);
      setError(e instanceof Error ? e.message : '삭제에 실패했어요. 다시 시도해 주세요.');
    }
  }, [customExtensions]);

  return { customExtensions, isLoading, error, add, remove };
}
