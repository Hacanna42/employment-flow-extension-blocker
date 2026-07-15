package io.github.hacanna42.extensionblocker.extension.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.hacanna42.extensionblocker.extension.exception.CustomExtensionLimitExceededException;
import io.github.hacanna42.extensionblocker.extension.exception.DuplicateExtensionException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BlockedExtensionsTest {

    private static final SpaceId SPACE_ID = new SpaceId(1L);

    @Nested
    @DisplayName("이름 포함 여부 조회")
    class ContainsName {

        @Test
        @DisplayName("차단 목록에 있는 이름은 포함된 것으로 판단한다")
        void findsExistingName() {
            // given
            BlockedExtensions blocked = blockedExtensionsOf("exe");

            // when
            boolean contained = blocked.containsName(ExtensionName.from("exe"));

            // then
            assertThat(contained).isTrue();
        }

        @Test
        @DisplayName("차단 목록에 없는 이름은 포함되지 않은 것으로 판단한다")
        void doesNotFindAbsentName() {
            // given
            BlockedExtensions blocked = blockedExtensionsOf("exe");

            // when
            boolean contained = blocked.containsName(ExtensionName.from("sh"));

            // then
            assertThat(contained).isFalse();
        }
    }

    @Nested
    @DisplayName("커스텀 확장자만 추출")
    class CustomOnly {

        @Test
        @DisplayName("고정 확장자는 커스텀 목록에서 제외한다")
        void excludesFixedExtensions() {
            // given
            BlockedExtensions blocked = blockedExtensionsOf("exe", "sh");

            // when
            List<BlockedExtension> customOnly = blocked.customOnly();

            // then
            assertThat(customOnly).hasSize(1);
            assertThat(customOnly.get(0).isNamed(ExtensionName.from("sh"))).isTrue();
        }

        @Test
        @DisplayName("커스텀 확장자가 없으면 빈 목록을 반환한다")
        void returnsEmptyListWhenNoCustomExtension() {
            // given
            BlockedExtensions blocked = blockedExtensionsOf("exe", "bat");

            // when
            List<BlockedExtension> customOnly = blocked.customOnly();

            // then
            assertThat(customOnly).isEmpty();
        }
    }

    @Nested
    @DisplayName("커스텀 확장자 추가 가능 여부 검증")
    class AssertCanAdd {

        @Test
        @DisplayName("이미 차단된 이름이면 예외가 발생한다")
        void rejectsDuplicateName() {
            // given
            BlockedExtensions blocked = blockedExtensionsOf("sh");

            // when & then
            assertThatThrownBy(() -> blocked.assertCanAdd(ExtensionName.from("sh")))
                    .isInstanceOf(DuplicateExtensionException.class);
        }

        @Test
        @DisplayName("아직 체크되지 않은 고정 확장자 이름이어도 커스텀으로는 추가할 수 없다")
        void rejectsUncheckedFixedExtensionName() {
            // given: exe는 아직 체크되지 않아 저장된 행이 하나도 없는 상태
            BlockedExtensions blocked = blockedExtensionsOf();

            // when & then
            assertThatThrownBy(() -> blocked.assertCanAdd(ExtensionName.from("exe")))
                    .isInstanceOf(DuplicateExtensionException.class);
        }

        @Test
        @DisplayName("이미 체크된 고정 확장자와 같은 이름이면 예외가 발생한다")
        void rejectsNameCollidingWithFixedExtension() {
            // given
            BlockedExtensions blocked = blockedExtensionsOf("exe");

            // when & then
            assertThatThrownBy(() -> blocked.assertCanAdd(ExtensionName.from("exe")))
                    .isInstanceOf(DuplicateExtensionException.class);
        }

        @Test
        @DisplayName("정원 내의 새로운 이름은 예외 없이 통과한다")
        void allowsNewNameUnderCapacity() {
            // given
            BlockedExtensions blocked = blockedExtensionsOf("sh");

            // when & then
            assertThatCode(() -> blocked.assertCanAdd(ExtensionName.from("py")))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("커스텀 확장자가 200개면 정원 초과 예외가 발생한다")
        void rejectsWhenCustomCapacityReached() {
            // given
            List<BlockedExtension> rows = new ArrayList<>();
            for (int i = 0; i < BlockedExtensions.CUSTOM_EXTENSION_MAX_COUNT; i++) {
                rows.add(new BlockedExtension(SPACE_ID, ExtensionName.from("c" + i)));
            }
            BlockedExtensions blocked = new BlockedExtensions(rows);

            // when & then
            assertThatThrownBy(() -> blocked.assertCanAdd(ExtensionName.from("overflow")))
                    .isInstanceOf(CustomExtensionLimitExceededException.class);
        }

        @Test
        @DisplayName("커스텀 확장자가 199개면 정원 초과 없이 통과한다")
        void allowsWhenOneSlotRemains() {
            // given
            List<BlockedExtension> rows = new ArrayList<>();
            for (int i = 0; i < BlockedExtensions.CUSTOM_EXTENSION_MAX_COUNT - 1; i++) {
                rows.add(new BlockedExtension(SPACE_ID, ExtensionName.from("c" + i)));
            }
            BlockedExtensions blocked = new BlockedExtensions(rows);

            // when & then
            assertThatCode(() -> blocked.assertCanAdd(ExtensionName.from("lastslot")))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("고정 확장자는 커스텀 정원 계산에 포함하지 않는다")
        void fixedExtensionsDoNotCountTowardCapacity() {
            // given: 고정 확장자 7개가 모두 체크돼 있어도 커스텀 199개는 추가 여유가 있어야 한다
            List<BlockedExtension> rows = new ArrayList<>();
            for (ExtensionName fixedName : FixedExtensionNames.orderedValues()) {
                rows.add(new BlockedExtension(SPACE_ID, fixedName));
            }
            for (int i = 0; i < BlockedExtensions.CUSTOM_EXTENSION_MAX_COUNT - 1; i++) {
                rows.add(new BlockedExtension(SPACE_ID, ExtensionName.from("c" + i)));
            }
            BlockedExtensions blocked = new BlockedExtensions(rows);

            // when & then
            assertThatCode(() -> blocked.assertCanAdd(ExtensionName.from("lastslot")))
                    .doesNotThrowAnyException();
        }
    }

    private BlockedExtensions blockedExtensionsOf(String... names) {
        List<BlockedExtension> rows = new ArrayList<>();
        for (String name : names) {
            rows.add(new BlockedExtension(SPACE_ID, ExtensionName.from(name)));
        }
        return new BlockedExtensions(rows);
    }
}
