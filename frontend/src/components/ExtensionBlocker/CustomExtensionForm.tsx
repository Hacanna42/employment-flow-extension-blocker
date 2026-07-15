import { useState } from 'react';
import { CUSTOM_EXTENSION_MAX_LENGTH } from '@/constants/extension';
import { validateCustomExtension, type ValidationContext } from '@/utils/validateExtension';

interface Props {
  context: ValidationContext;
  onAdd: (name: string) => Promise<boolean>;
}

export function CustomExtensionForm({ context, onAdd }: Props) {
  const [value, setValue] = useState('');
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async () => {
    const result = validateCustomExtension(value, context);
    if (!result.ok) {
      setErrorMessage(result.message);
      return;
    }
    setErrorMessage(null);
    setIsSubmitting(true);
    const added = await onAdd(result.value);
    setIsSubmitting(false);
    if (added) setValue('');
  };

  return (
    <>
      <div className="custom-form">
        <div className="custom-form__field">
          <input
            className="custom-form__input"
            type="text"
            value={value}
            placeholder="확장자 입력"
            maxLength={CUSTOM_EXTENSION_MAX_LENGTH}
            aria-label="커스텀 확장자 입력"
            aria-describedby="custom-extension-error"
            onChange={(e) => {
              setValue(e.target.value);
              if (errorMessage) setErrorMessage(null);
            }}
            onKeyDown={(e) => {
              if (e.key === 'Enter' && !e.nativeEvent.isComposing) handleSubmit();
            }}
          />
          <span className="custom-form__count" aria-hidden="true">
            {value.length}/{CUSTOM_EXTENSION_MAX_LENGTH}
          </span>
        </div>
        <button
          type="button"
          className="custom-form__submit"
          disabled={value.trim().length === 0 || isSubmitting}
          onClick={handleSubmit}
        >
          {isSubmitting ? '추가 중...' : '+ 추가'}
        </button>
      </div>
      {errorMessage && (
        <p className="custom-error" id="custom-extension-error" role="alert">
          {errorMessage}
        </p>
      )}
    </>
  );
}
