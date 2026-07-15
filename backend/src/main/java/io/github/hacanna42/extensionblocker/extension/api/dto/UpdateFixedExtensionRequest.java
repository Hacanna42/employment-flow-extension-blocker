package io.github.hacanna42.extensionblocker.extension.api.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateFixedExtensionRequest(@NotNull Boolean checked) {
}
