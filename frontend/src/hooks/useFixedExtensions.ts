import { useCallback, useEffect, useState } from 'react';
import { extensionApi } from '@/api';
import type { FixedExtension } from '@/types/extension';
import { readCache, writeCache } from '@/utils/localCache';

const CACHE_KEY = 'fixed-extensions';

export function useFixedExtensions() {
  const [fixedExtensions, setFixedExtensions] = useState<FixedExtension[]>(
    () => readCache<FixedExtension[]>(CACHE_KEY) ?? [],
  );
  const [isLoading, setIsLoading] = useState(() => readCache<FixedExtension[]>(CACHE_KEY) === null);
  const [error, setError] = useState<string | null>(null);
  const [pendingNames, setPendingNames] = useState<Set<string>>(new Set());

  useEffect(() => {
    let alive = true;
    extensionApi
      .getFixedExtensions()
      .then((data) => {
        if (!alive) return;
        setFixedExtensions(data);
        writeCache(CACHE_KEY, data);
      })
      .catch((e: Error) => alive && setError(e.message))
      .finally(() => alive && setIsLoading(false));
    return () => {
      alive = false;
    };
  }, []);

  const toggle = useCallback(async (name: string, checked: boolean) => {
    setError(null);
    setPendingNames((prev) => new Set(prev).add(name));
    try {
      await extensionApi.updateFixedExtension(name, checked);
      setFixedExtensions((prev) => {
        const next = prev.map((item) => (item.name === name ? { ...item, checked } : item));
        writeCache(CACHE_KEY, next);
        return next;
      });
    } catch (e) {
      setError(e instanceof Error ? e.message : '저장에 실패했어요. 다시 시도해 주세요.');
    } finally {
      setPendingNames((prev) => {
        const next = new Set(prev);
        next.delete(name);
        return next;
      });
    }
  }, []);

  return { fixedExtensions, isLoading, error, toggle, pendingNames };
}
