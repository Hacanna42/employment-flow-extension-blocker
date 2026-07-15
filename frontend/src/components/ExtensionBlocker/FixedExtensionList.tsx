import type { FixedExtension } from '@/types/extension';

interface Props {
  extensions: FixedExtension[];
  onToggle: (name: string, checked: boolean) => void;
  pendingNames: Set<string>;
}

export function FixedExtensionList({ extensions, onToggle, pendingNames }: Props) {
  return (
    <div className="fixed-list" role="group" aria-label="고정 확장자 목록">
      {extensions.map(({ name, checked }) => {
        const isPending = pendingNames.has(name);
        return (
          <label key={name} className="fixed-item" aria-busy={isPending}>
            <input
              type="checkbox"
              checked={checked}
              disabled={isPending}
              onChange={(e) => onToggle(name, e.target.checked)}
            />
            {name}
            {isPending && <span className="spinner" aria-hidden="true" />}
          </label>
        );
      })}
    </div>
  );
}
