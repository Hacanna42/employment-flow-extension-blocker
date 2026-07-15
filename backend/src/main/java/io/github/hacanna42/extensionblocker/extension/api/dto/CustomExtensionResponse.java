package io.github.hacanna42.extensionblocker.extension.api.dto;

import io.github.hacanna42.extensionblocker.extension.domain.BlockedExtension;

public record CustomExtensionResponse(Long id, String name) {

    public static CustomExtensionResponse from(BlockedExtension blockedExtension) {
        return new CustomExtensionResponse(blockedExtension.id(), blockedExtension.extensionName().value());
    }
}
