package io.github.hacanna42.extensionblocker.extension.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddCustomExtensionRequest(
        @NotBlank(message = "확장자를 입력해 주세요.")
        @Size(max = 20, message = "확장자는 최대 20자까지 입력할 수 있어요.")
        String name
) {
}
