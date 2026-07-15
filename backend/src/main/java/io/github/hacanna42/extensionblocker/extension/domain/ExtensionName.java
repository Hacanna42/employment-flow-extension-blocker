package io.github.hacanna42.extensionblocker.extension.domain;

import io.github.hacanna42.extensionblocker.extension.exception.InvalidExtensionNameException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;

@Embeddable
public record ExtensionName(@Column(name = "name", nullable = false, length = 20) String value) {

    private static final Pattern VALID_FORMAT = Pattern.compile("^[a-z0-9]{1,20}$");

    public ExtensionName {
        value = normalize(value);
        assertValidFormat(value);
    }

    public static ExtensionName from(String rawValue) {
        return new ExtensionName(rawValue);
    }

    private static String normalize(String rawValue) {
        return rawValue.trim().toLowerCase().replaceFirst("^\\.+", "");
    }

    private static void assertValidFormat(String candidate) {
        if (!VALID_FORMAT.matcher(candidate).matches()) {
            throw new InvalidExtensionNameException();
        }
    }

    public boolean isFixed() {
        return FixedExtensionNames.contains(this);
    }
}
