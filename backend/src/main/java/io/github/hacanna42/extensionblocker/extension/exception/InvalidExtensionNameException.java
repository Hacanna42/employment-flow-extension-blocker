package io.github.hacanna42.extensionblocker.extension.exception;

public class InvalidExtensionNameException extends RuntimeException {

    public InvalidExtensionNameException() {
        super("영문 소문자와 숫자만 입력할 수 있어요.");
    }
}
