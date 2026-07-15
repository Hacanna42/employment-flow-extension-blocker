package io.github.hacanna42.extensionblocker.extension.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BlockedExtensionTest {

    private static final SpaceId SPACE_ID = new SpaceId(1L);

    @Test
    @DisplayName("같은 이름으로 물으면 isNamed()는 true를 반환한다")
    void isNamedReturnsTrueForSameName() {
        // given
        BlockedExtension blockedExtension = new BlockedExtension(SPACE_ID, ExtensionName.from("sh"));

        // when
        boolean named = blockedExtension.isNamed(ExtensionName.from("sh"));

        // then
        assertThat(named).isTrue();
    }

    @Test
    @DisplayName("다른 이름으로 물으면 isNamed()는 false를 반환한다")
    void isNamedReturnsFalseForDifferentName() {
        // given
        BlockedExtension blockedExtension = new BlockedExtension(SPACE_ID, ExtensionName.from("sh"));

        // when
        boolean named = blockedExtension.isNamed(ExtensionName.from("py"));

        // then
        assertThat(named).isFalse();
    }

    @Test
    @DisplayName("고정 확장자 이름으로 생성하면 isFixed()는 true를 반환한다")
    void isFixedReturnsTrueForFixedExtensionName() {
        // given
        BlockedExtension blockedExtension = new BlockedExtension(SPACE_ID, ExtensionName.from("exe"));

        // when
        boolean fixed = blockedExtension.isFixed();

        // then
        assertThat(fixed).isTrue();
    }

    @Test
    @DisplayName("커스텀 확장자 이름으로 생성하면 isFixed()는 false를 반환한다")
    void isFixedReturnsFalseForCustomExtensionName() {
        // given
        BlockedExtension blockedExtension = new BlockedExtension(SPACE_ID, ExtensionName.from("sh"));

        // when
        boolean fixed = blockedExtension.isFixed();

        // then
        assertThat(fixed).isFalse();
    }
}
