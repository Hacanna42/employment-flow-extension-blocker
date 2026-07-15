package io.github.hacanna42.extensionblocker.common.lock;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AdvisoryLockNamespaceTest {

    @Test
    @DisplayName("모든 네임스페이스는 서로 다른 classId를 가진다")
    void everyNamespaceHasUniqueClassId() {
        // given
        AdvisoryLockNamespace[] namespaces = AdvisoryLockNamespace.values();

        // when
        Set<Integer> classIds = Arrays.stream(namespaces)
                .map(AdvisoryLockNamespace::classId)
                .collect(Collectors.toSet());

        // then
        assertThat(classIds).hasSameSizeAs(namespaces);
    }
}
