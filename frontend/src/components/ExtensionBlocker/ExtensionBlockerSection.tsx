import { useMemo } from 'react';
import { useCustomExtensions } from '@/hooks/useCustomExtensions';
import { useFixedExtensions } from '@/hooks/useFixedExtensions';
import { CustomExtensionForm } from './CustomExtensionForm';
import { CustomExtensionTagList } from './CustomExtensionTagList';
import { FixedExtensionList } from './FixedExtensionList';
import './ExtensionBlocker.css';

export function ExtensionBlockerSection() {
  const fixed = useFixedExtensions();
  const custom = useCustomExtensions();

  const validationContext = useMemo(
    () => ({
      customNames: custom.customExtensions.map((e) => e.name),
      fixedNames: fixed.fixedExtensions.map((e) => e.name),
    }),
    [custom.customExtensions, fixed.fixedExtensions],
  );

  const serverError = fixed.error ?? custom.error;

  return (
    <section className="blocker-card" aria-labelledby="blocker-title">
      <h1 className="blocker-card__title" id="blocker-title">
        확장자 차단
      </h1>
      <p className="blocker-card__desc">파일 업로드 시 차단할 확장자를 관리합니다.</p>

      <div className="blocker-row">
        <span className="blocker-row__label">고정 확장자</span>
        {fixed.isLoading ? (
          <span className="status-text">불러오는 중...</span>
        ) : (
          <FixedExtensionList extensions={fixed.fixedExtensions} onToggle={fixed.toggle} />
        )}
      </div>

      <div className="blocker-row">
        <span className="blocker-row__label">커스텀 확장자</span>
        <div>
          <CustomExtensionForm context={validationContext} onAdd={custom.add} />
          {custom.isLoading ? (
            <p className="status-text" style={{ marginTop: 12 }}>
              불러오는 중...
            </p>
          ) : (
            <CustomExtensionTagList
              extensions={custom.customExtensions}
              onRemove={custom.remove}
            />
          )}
          {serverError && (
            <p className="custom-error" role="alert">
              {serverError}
            </p>
          )}
        </div>
      </div>
    </section>
  );
}
