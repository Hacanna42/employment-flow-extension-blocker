package io.github.hacanna42.extensionblocker.extension.application;

import io.github.hacanna42.extensionblocker.common.lock.AdvisoryLockNamespace;
import io.github.hacanna42.extensionblocker.extension.api.dto.CustomExtensionResponse;
import io.github.hacanna42.extensionblocker.extension.domain.BlockedExtension;
import io.github.hacanna42.extensionblocker.extension.domain.BlockedExtensionRepository;
import io.github.hacanna42.extensionblocker.extension.domain.BlockedExtensions;
import io.github.hacanna42.extensionblocker.extension.domain.ExtensionName;
import io.github.hacanna42.extensionblocker.extension.domain.SpaceId;
import io.github.hacanna42.extensionblocker.extension.exception.ExtensionNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomExtensionService {

    private final BlockedExtensionRepository repository;
    private final CurrentSpaceIdProvider spaceIdProvider;

    public CustomExtensionService(BlockedExtensionRepository repository, CurrentSpaceIdProvider spaceIdProvider) {
        this.repository = repository;
        this.spaceIdProvider = spaceIdProvider;
    }

    @Transactional(readOnly = true)
    public List<CustomExtensionResponse> list() {
        SpaceId spaceId = spaceIdProvider.currentSpaceId();
        BlockedExtensions blocked = new BlockedExtensions(repository.findAllBySpaceId(spaceId));
        return blocked.customOnly().stream().map(CustomExtensionResponse::from).toList();
    }

    @Transactional
    public CustomExtensionResponse add(String rawName) {
        SpaceId spaceId = spaceIdProvider.currentSpaceId();
        ExtensionName extensionName = ExtensionName.from(rawName);
        repository.lockSpace(AdvisoryLockNamespace.BLOCKED_EXTENSION.classId(), Math.toIntExact(spaceId.value()));
        BlockedExtensions blocked = new BlockedExtensions(repository.findAllBySpaceId(spaceId));
        blocked.assertCanAdd(extensionName);
        BlockedExtension saved = repository.save(new BlockedExtension(spaceId, extensionName));
        return CustomExtensionResponse.from(saved);
    }

    @Transactional
    public void remove(Long id) {
        SpaceId spaceId = spaceIdProvider.currentSpaceId();
        BlockedExtension target = findRemovableCustomExtension(spaceId, id);
        repository.delete(target);
    }

    private BlockedExtension findRemovableCustomExtension(SpaceId spaceId, Long id) {
        return repository.findBySpaceIdAndId(spaceId, id)
                .filter(row -> !row.isFixed())
                .orElseThrow(ExtensionNotFoundException::new);
    }
}
