import { httpClient } from './httpClient';
import type { ExtensionApi } from './extensionApi.types';
import type { CustomExtension, FixedExtension } from '@/types/extension';

export const httpExtensionApi: ExtensionApi = {
  getFixedExtensions: () => httpClient.get<FixedExtension[]>('/extensions/fixed'),

  updateFixedExtension: (name, checked) =>
    httpClient.patch<FixedExtension>(`/extensions/fixed/${encodeURIComponent(name)}`, { checked }),

  getCustomExtensions: () => httpClient.get<CustomExtension[]>('/extensions/custom'),

  addCustomExtension: (name) => httpClient.post<CustomExtension>('/extensions/custom', { name }),

  deleteCustomExtension: (id) => httpClient.delete<void>(`/extensions/custom/${id}`),
};
