package io.github.hacanna42.extensionblocker.extension.application;

import io.github.hacanna42.extensionblocker.extension.api.dto.FixedExtensionResponse;
import io.github.hacanna42.extensionblocker.extension.domain.BlockedExtensionRepository;
import io.github.hacanna42.extensionblocker.extension.domain.BlockedExtensions;
import io.github.hacanna42.extensionblocker.extension.domain.ExtensionName;
import io.github.hacanna42.extensionblocker.extension.domain.FixedExtensionNames;
import io.github.hacanna42.extensionblocker.extension.domain.SpaceId;
import io.github.hacanna42.extensionblocker.extension.exception.ExtensionNotFoundException;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class FixedExtensionService {

    private final BlockedExtensionRepository repository;
    private final CurrentSpaceIdProvider spaceIdProvider;

    public FixedExtensionService(BlockedExtensionRepository repository, CurrentSpaceIdProvider spaceIdProvider) {
        this.repository = repository;
        this.spaceIdProvider = spaceIdProvider;
    }

    @Transactional(readOnly = true)
    public List<FixedExtensionResponse> list() {
        SpaceId spaceId = spaceIdProvider.currentSpaceId();
        BlockedExtensions blocked = new BlockedExtensions(repository.findAllBySpaceId(spaceId));
        return FixedExtensionNames.orderedValues().stream()
                .map(name -> FixedExtensionResponse.of(name, blocked.containsName(name)))
                .toList();
    }

    @Transactional
    public FixedExtensionResponse updateChecked(String rawName, boolean checked) {
        ExtensionName extensionName = assertKnownFixedName(rawName);
        SpaceId spaceId = spaceIdProvider.currentSpaceId();
        applyChecked(spaceId, extensionName, checked);
        log.info("고정 확장자 변경: space={} name={} checked={}", spaceId.value(), extensionName.value(), checked);
        return new FixedExtensionResponse(extensionName.value(), checked);
    }

    private ExtensionName assertKnownFixedName(String rawName) {
        ExtensionName extensionName = ExtensionName.from(rawName);
        if (!extensionName.isFixed()) {
            throw new ExtensionNotFoundException();
        }
        return extensionName;
    }

    private void applyChecked(SpaceId spaceId, ExtensionName extensionName, boolean checked) {
        if (checked) {
            checkOn(spaceId, extensionName);
            return;
        }
        checkOff(spaceId, extensionName);
    }

    private void checkOn(SpaceId spaceId, ExtensionName extensionName) {
        repository.insertIfAbsent(spaceId.value(), extensionName.value(), Instant.now());
    }

    private void checkOff(SpaceId spaceId, ExtensionName extensionName) {
        repository.deleteBySpaceIdAndExtensionName(spaceId, extensionName);
    }
}
