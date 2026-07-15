import type { CustomExtension, FixedExtension } from '@/types/extension';

export interface ExtensionApi {
  getFixedExtensions(): Promise<FixedExtension[]>;
  updateFixedExtension(name: string, checked: boolean): Promise<FixedExtension>;
  getCustomExtensions(): Promise<CustomExtension[]>;
  addCustomExtension(name: string): Promise<CustomExtension>;
  deleteCustomExtension(id: number): Promise<void>;
}
