package io.github.hacanna42.extensionblocker.extension.exception;

import io.github.hacanna42.extensionblocker.extension.domain.ExtensionName;

public class DuplicateExtensionException extends RuntimeException {

    public DuplicateExtensionException(ExtensionName extensionName) {
        super(buildMessage(extensionName));
    }

    private static String buildMessage(ExtensionName extensionName) {
        if (extensionName.isFixed()) {
            return "고정 확장자에 이미 있는 확장자예요. 위에서 체크해 주세요.";
        }
        return "이미 추가된 확장자예요.";
    }
}
