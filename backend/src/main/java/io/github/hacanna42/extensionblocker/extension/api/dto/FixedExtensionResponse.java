package io.github.hacanna42.extensionblocker.extension.api.dto;

import io.github.hacanna42.extensionblocker.extension.domain.ExtensionName;

public record FixedExtensionResponse(String name, boolean checked) {

    public static FixedExtensionResponse of(ExtensionName extensionName, boolean checked) {
        return new FixedExtensionResponse(extensionName.value(), checked);
    }
}
