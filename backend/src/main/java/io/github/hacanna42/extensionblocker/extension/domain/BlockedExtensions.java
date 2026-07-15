package io.github.hacanna42.extensionblocker.extension.domain;

import io.github.hacanna42.extensionblocker.extension.exception.CustomExtensionLimitExceededException;
import io.github.hacanna42.extensionblocker.extension.exception.DuplicateExtensionException;
import java.util.List;

public class BlockedExtensions {

    public static final int CUSTOM_EXTENSION_MAX_COUNT = 200;

    private final List<BlockedExtension> values;

    public BlockedExtensions(List<BlockedExtension> values) {
        this.values = List.copyOf(values);
    }

    public boolean containsName(ExtensionName extensionName) {
        return values.stream().anyMatch(row -> row.isNamed(extensionName));
    }

    public List<BlockedExtension> customOnly() {
        return values.stream().filter(row -> !row.isFixed()).toList();
    }

    public void assertCanAdd(ExtensionName extensionName) {
        assertNotAlreadyBlocked(extensionName);
        assertCustomCapacityAvailable();
    }

    private void assertNotAlreadyBlocked(ExtensionName extensionName) {
        if (containsName(extensionName)) {
            throw new DuplicateExtensionException(extensionName);
        }
    }

    private void assertCustomCapacityAvailable() {
        if (customOnly().size() >= CUSTOM_EXTENSION_MAX_COUNT) {
            throw new CustomExtensionLimitExceededException();
        }
    }
}
