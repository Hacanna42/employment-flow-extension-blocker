package io.github.hacanna42.extensionblocker.extension.domain;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FixedExtensionNames {

    private static final List<String> ORDERED_RAW_VALUES = List.of("bat", "cmd", "com", "cpl", "exe", "scr", "js");

    private static final Set<ExtensionName> VALUES = ORDERED_RAW_VALUES.stream()
            .map(ExtensionName::from)
            .collect(Collectors.toUnmodifiableSet());

    private FixedExtensionNames() {
    }

    public static boolean contains(ExtensionName candidate) {
        return VALUES.contains(candidate);
    }

    public static List<ExtensionName> orderedValues() {
        return ORDERED_RAW_VALUES.stream().map(ExtensionName::from).toList();
    }
}
