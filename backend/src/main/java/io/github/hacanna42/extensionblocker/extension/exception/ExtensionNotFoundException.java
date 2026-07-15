package io.github.hacanna42.extensionblocker.extension.exception;

public class ExtensionNotFoundException extends RuntimeException {

    public ExtensionNotFoundException() {
        super("확장자를 찾을 수 없어요.");
    }
}
