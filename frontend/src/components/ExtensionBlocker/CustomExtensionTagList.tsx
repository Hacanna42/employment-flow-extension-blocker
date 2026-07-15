import { CUSTOM_EXTENSION_MAX_COUNT } from '@/constants/extension';
import type { CustomExtension } from '@/types/extension';

interface Props {
  extensions: CustomExtension[];
  onRemove: (id: number) => void;
}

export function CustomExtensionTagList({ extensions, onRemove }: Props) {
  return (
    <div className="tag-board">
      <p className="tag-board__count">
        <strong>{extensions.length}</strong>/{CUSTOM_EXTENSION_MAX_COUNT}
      </p>
      {extensions.length === 0 ? (
        <p className="tag-board__empty">차단할 확장자를 위에서 추가해 주세요.</p>
      ) : (
        <ul className="tag-board__list">
          {extensions.map(({ id, name }) => (
            <li key={id} className="tag">
              {name}
              <button
                type="button"
                className="tag__remove"
                aria-label={`${name} 확장자 삭제`}
                onClick={() => onRemove(id)}
              >
                ✕
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
