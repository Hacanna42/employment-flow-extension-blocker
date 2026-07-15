package io.github.hacanna42.extensionblocker.extension.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.hacanna42.extensionblocker.extension.exception.InvalidExtensionNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ExtensionNameTest {

    @Nested
    @DisplayName("정규화")
    class Normalize {

        @Test
        @DisplayName("앞뒤 공백을 제거한다")
        void trimsWhitespace() {
            // given
            String rawValue = "  exe  ";

            // when
            ExtensionName extensionName = ExtensionName.from(rawValue);

            // then
            assertThat(extensionName.value()).isEqualTo("exe");
        }

        @Test
        @DisplayName("대문자를 소문자로 바꾼다")
        void lowercasesValue() {
            // given
            String rawValue = "EXE";

            // when
            ExtensionName extensionName = ExtensionName.from(rawValue);

            // then
            assertThat(extensionName.value()).isEqualTo("exe");
        }

        @Test
        @DisplayName("맨 앞에 붙은 점을 제거한다")
        void stripsLeadingDots() {
            // given
            String rawValue = "..exe";

            // when
            ExtensionName extensionName = ExtensionName.from(rawValue);

            // then
            assertThat(extensionName.value()).isEqualTo("exe");
        }
    }

    @Nested
    @DisplayName("형식 검증")
    class FormatValidation {

        @Test
        @DisplayName("영문 소문자와 숫자로만 이루어진 이름은 허용한다")
        void acceptsLowerCaseLettersAndDigits() {
            // given
            String rawValue = "mp4a1";

            // when
            ExtensionName extensionName = ExtensionName.from(rawValue);

            // then
            assertThat(extensionName.value()).isEqualTo("mp4a1");
        }

        @Test
        @DisplayName("정확히 20자는 허용한다")
        void acceptsExactlyTwentyCharacters() {
            // given
            String rawValue = "a".repeat(20);

            // when
            ExtensionName extensionName = ExtensionName.from(rawValue);

            // then
            assertThat(extensionName.value()).hasSize(20);
        }

        @Test
        @DisplayName("21자 이상이면 예외가 발생한다")
        void rejectsMoreThanTwentyCharacters() {
            // given
            String rawValue = "a".repeat(21);

            // when & then
            assertThatThrownBy(() -> ExtensionName.from(rawValue))
                    .isInstanceOf(InvalidExtensionNameException.class);
        }

        @Test
        @DisplayName("영문/숫자 외 문자가 섞이면 예외가 발생한다")
        void rejectsNonAlphanumericCharacters() {
            // given
            String rawValue = "exe!";

            // when & then
            assertThatThrownBy(() -> ExtensionName.from(rawValue))
                    .isInstanceOf(InvalidExtensionNameException.class);
        }

        @Test
        @DisplayName("정규화 후 빈 문자열이 되면 예외가 발생한다")
        void rejectsBlankValue() {
            // given
            String rawValue = "   ";

            // when & then
            assertThatThrownBy(() -> ExtensionName.from(rawValue))
                    .isInstanceOf(InvalidExtensionNameException.class);
        }
    }

    @Nested
    @DisplayName("고정 확장자 판별")
    class FixedDetection {

        @Test
        @DisplayName("고정 확장자 목록에 있으면 isFixed()는 true를 반환한다")
        void recognizesFixedExtension() {
            // given
            ExtensionName extensionName = ExtensionName.from("exe");

            // when
            boolean isFixed = extensionName.isFixed();

            // then
            assertThat(isFixed).isTrue();
        }

        @Test
        @DisplayName("고정 확장자 목록에 없으면 isFixed()는 false를 반환한다")
        void recognizesCustomExtension() {
            // given
            ExtensionName extensionName = ExtensionName.from("sh");

            // when
            boolean isFixed = extensionName.isFixed();

            // then
            assertThat(isFixed).isFalse();
        }

        @Test
        @DisplayName("대문자로 입력해도 정규화 후 고정 확장자로 인식한다")
        void recognizesFixedExtensionRegardlessOfCase() {
            // given
            ExtensionName extensionName = ExtensionName.from("EXE");

            // when
            boolean isFixed = extensionName.isFixed();

            // then
            assertThat(isFixed).isTrue();
        }
    }
}
