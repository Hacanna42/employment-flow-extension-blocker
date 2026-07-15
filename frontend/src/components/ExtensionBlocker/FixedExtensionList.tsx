import type { FixedExtension } from '@/types/extension';

interface Props {
  extensions: FixedExtension[];
  onToggle: (name: string, checked: boolean) => void;
}

export function FixedExtensionList({ extensions, onToggle }: Props) {
  return (
    <div className="fixed-list" role="group" aria-label="고정 확장자 목록">
      {extensions.map(({ name, checked }) => (
        <label key={name} className="fixed-item">
          <input
            type="checkbox"
            checked={checked}
            onChange={(e) => onToggle(name, e.target.checked)}
          />
          {name}
        </label>
      ))}
    </div>
  );
}
