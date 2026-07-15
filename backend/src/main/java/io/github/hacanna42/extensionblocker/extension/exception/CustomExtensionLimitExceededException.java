package io.github.hacanna42.extensionblocker.extension.exception;

public class CustomExtensionLimitExceededException extends RuntimeException {

    public CustomExtensionLimitExceededException() {
        super("커스텀 확장자는 최대 200개까지 추가할 수 있어요.");
    }
}
