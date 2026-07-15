import {
  CUSTOM_EXTENSION_MAX_COUNT,
  CUSTOM_EXTENSION_MAX_LENGTH,
  EXTENSION_NAME_PATTERN,
} from '@/constants/extension';

export interface ValidationContext {
  customNames: string[];
  fixedNames: string[];
}

export type ValidationResult = { ok: true; value: string } | { ok: false; message: string };

export function validateCustomExtension(raw: string, ctx: ValidationContext): ValidationResult {
  const value = raw.trim().toLowerCase().replace(/^\.+/, '');

  if (value.length === 0) {
    return { ok: false, message: '확장자를 입력해 주세요.' };
  }
  if (value.length > CUSTOM_EXTENSION_MAX_LENGTH) {
    return { ok: false, message: `확장자는 최대 ${CUSTOM_EXTENSION_MAX_LENGTH}자까지 입력할 수 있어요.` };
  }
  if (!EXTENSION_NAME_PATTERN.test(value)) {
    return { ok: false, message: '영문 소문자와 숫자만 입력할 수 있어요.' };
  }
  if (ctx.fixedNames.includes(value)) {
    return { ok: false, message: '고정 확장자에 이미 있는 확장자예요. 위에서 체크해 주세요.' };
  }
  if (ctx.customNames.includes(value)) {
    return { ok: false, message: '이미 추가된 확장자예요.' };
  }
  if (ctx.customNames.length >= CUSTOM_EXTENSION_MAX_COUNT) {
    return { ok: false, message: `커스텀 확장자는 최대 ${CUSTOM_EXTENSION_MAX_COUNT}개까지 추가할 수 있어요.` };
  }
  return { ok: true, value };
}
