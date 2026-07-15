import type { ExtensionApi } from './extensionApi.types';
import type { CustomExtension, FixedExtension } from '@/types/extension';

const FIXED_KEY = 'extension-blocker/fixed';
const CUSTOM_KEY = 'extension-blocker/custom';
const SEQ_KEY = 'extension-blocker/custom-seq';
const LATENCY_MS = 120;

const DEFAULT_FIXED: FixedExtension[] = ['bat', 'cmd', 'com', 'cpl', 'exe', 'scr', 'js'].map(
  (name) => ({ name, checked: false }),
);

const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

function load<T>(key: string, fallback: T): T {
  const raw = localStorage.getItem(key);
  if (raw === null) return fallback;
  try {
    return JSON.parse(raw) as T;
  } catch {
    return fallback;
  }
}

function save(key: string, value: unknown): void {
  localStorage.setItem(key, JSON.stringify(value));
}

export const mockExtensionApi: ExtensionApi = {
  async getFixedExtensions() {
    await delay(LATENCY_MS);
    return load<FixedExtension[]>(FIXED_KEY, DEFAULT_FIXED);
  },

  async updateFixedExtension(name, checked) {
    await delay(LATENCY_MS);
    const list = load<FixedExtension[]>(FIXED_KEY, DEFAULT_FIXED);
    const target = list.find((item) => item.name === name);
    if (!target) throw new Error(`고정 확장자를 찾을 수 없어요: ${name}`);
    target.checked = checked;
    save(FIXED_KEY, list);
    return { ...target };
  },

  async getCustomExtensions() {
    await delay(LATENCY_MS);
    return load<CustomExtension[]>(CUSTOM_KEY, []);
  },

  async addCustomExtension(name) {
    await delay(LATENCY_MS);
    const list = load<CustomExtension[]>(CUSTOM_KEY, []);
    if (list.some((item) => item.name === name)) {
      throw new Error('이미 추가된 확장자예요.');
    }
    const nextId = load<number>(SEQ_KEY, 0) + 1;
    const created: CustomExtension = { id: nextId, name };
    save(CUSTOM_KEY, [...list, created]);
    save(SEQ_KEY, nextId);
    return created;
  },

  async deleteCustomExtension(id) {
    await delay(LATENCY_MS);
    const list = load<CustomExtension[]>(CUSTOM_KEY, []);
    save(
      CUSTOM_KEY,
      list.filter((item) => item.id !== id),
    );
  },
};
