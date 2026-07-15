import { useCallback, useEffect, useState } from 'react';
import { extensionApi } from '@/api';
import type { FixedExtension } from '@/types/extension';

export function useFixedExtensions() {
  const [fixedExtensions, setFixedExtensions] = useState<FixedExtension[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let alive = true;
    extensionApi
      .getFixedExtensions()
      .then((data) => alive && setFixedExtensions(data))
      .catch((e: Error) => alive && setError(e.message))
      .finally(() => alive && setIsLoading(false));
    return () => {
      alive = false;
    };
  }, []);

  const toggle = useCallback(async (name: string, checked: boolean) => {
    setError(null);
    setFixedExtensions((prev) =>
      prev.map((item) => (item.name === name ? { ...item, checked } : item)),
    );
    try {
      await extensionApi.updateFixedExtension(name, checked);
    } catch (e) {
      setFixedExtensions((prev) =>
        prev.map((item) => (item.name === name ? { ...item, checked: !checked } : item)),
      );
      setError(e instanceof Error ? e.message : '저장에 실패했어요. 다시 시도해 주세요.');
    }
  }, []);

  return { fixedExtensions, isLoading, error, toggle };
}
