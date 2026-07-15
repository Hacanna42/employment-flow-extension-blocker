import { httpExtensionApi } from './extensionApi.http';
import { mockExtensionApi } from './extensionApi.mock';
import type { ExtensionApi } from './extensionApi.types';

export const extensionApi: ExtensionApi =
  import.meta.env.VITE_USE_MOCK === 'true' ? mockExtensionApi : httpExtensionApi;

export type { ExtensionApi } from './extensionApi.types';
