package io.github.hacanna42.extensionblocker.extension.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import io.github.hacanna42.extensionblocker.common.lock.AdvisoryLockNamespace;
import io.github.hacanna42.extensionblocker.extension.api.dto.CustomExtensionResponse;
import io.github.hacanna42.extensionblocker.extension.domain.BlockedExtension;
import io.github.hacanna42.extensionblocker.extension.domain.BlockedExtensionRepository;
import io.github.hacanna42.extensionblocker.extension.domain.ExtensionName;
import io.github.hacanna42.extensionblocker.extension.domain.SpaceId;
import io.github.hacanna42.extensionblocker.extension.exception.CustomExtensionLimitExceededException;
import io.github.hacanna42.extensionblocker.extension.exception.DuplicateExtensionException;
import io.github.hacanna42.extensionblocker.extension.exception.ExtensionNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomExtensionServiceTest {

    private static final SpaceId SPACE_ID = new SpaceId(1L);

    @Mock
    private BlockedExtensionRepository repository;

    @Mock
    private CurrentSpaceIdProvider spaceIdProvider;

    private CustomExtensionService service;

    @BeforeEach
    void setUp() {
        service = new CustomExtensionService(repository, spaceIdProvider);
    }

    @Nested
    @DisplayName("커스텀 확장자 목록 조회")
    class List_ {

        @Test
        @DisplayName("고정 확장자는 목록에서 제외한다")
        void excludesFixedExtensions() {
            // given
            given(spaceIdProvider.currentSpaceId()).willReturn(SPACE_ID);
            given(repository.findAllBySpaceId(SPACE_ID)).willReturn(List.of(
                    new BlockedExtension(SPACE_ID, ExtensionName.from("exe")),
                    new BlockedExtension(SPACE_ID, ExtensionName.from("sh"))
            ));

            // when
            List<CustomExtensionResponse> result = service.list();

            // then
            assertThat(result).extracting(CustomExtensionResponse::name).containsExactly("sh");
        }
    }

    @Nested
    @DisplayName("커스텀 확장자 추가")
    class Add {

        @Test
        @DisplayName("정원 내의 새 이름이면 space를 잠근 뒤 저장한다")
        void locksSpaceThenSavesNewExtension() {
            // given
            given(spaceIdProvider.currentSpaceId()).willReturn(SPACE_ID);
            given(repository.findAllBySpaceId(SPACE_ID)).willReturn(List.of());
            given(repository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            CustomExtensionResponse response = service.add("sh");

            // then
            verify(repository).lockSpace(AdvisoryLockNamespace.BLOCKED_EXTENSION.classId(), 1);
            ArgumentCaptor<BlockedExtension> captor = ArgumentCaptor.forClass(BlockedExtension.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().isNamed(ExtensionName.from("sh"))).isTrue();
            assertThat(response.name()).isEqualTo("sh");
        }

        @Test
        @DisplayName("아직 체크되지 않은 고정 확장자 이름이면 예외가 발생하고 저장하지 않는다")
        void rejectsUncheckedFixedExtensionNameWithoutSaving() {
            // given: exe는 아직 체크되지 않아 저장된 행이 하나도 없는 상태
            given(spaceIdProvider.currentSpaceId()).willReturn(SPACE_ID);
            given(repository.findAllBySpaceId(SPACE_ID)).willReturn(List.of());

            // when & then
            assertThatThrownBy(() -> service.add("exe"))
                    .isInstanceOf(DuplicateExtensionException.class);
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("이미 차단된 이름이면 예외가 발생하고 저장하지 않는다")
        void rejectsDuplicateNameWithoutSaving() {
            // given
            given(spaceIdProvider.currentSpaceId()).willReturn(SPACE_ID);
            given(repository.findAllBySpaceId(SPACE_ID))
                    .willReturn(List.of(new BlockedExtension(SPACE_ID, ExtensionName.from("sh"))));

            // when & then
            assertThatThrownBy(() -> service.add("sh"))
                    .isInstanceOf(DuplicateExtensionException.class);
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("커스텀 확장자가 200개면 정원 초과 예외가 발생하고 저장하지 않는다")
        void rejectsWhenCapacityReached() {
            // given
            List<BlockedExtension> rows = new ArrayList<>();
            for (int i = 0; i < 200; i++) {
                rows.add(new BlockedExtension(SPACE_ID, ExtensionName.from("c" + i)));
            }
            given(spaceIdProvider.currentSpaceId()).willReturn(SPACE_ID);
            given(repository.findAllBySpaceId(SPACE_ID)).willReturn(rows);

            // when & then
            assertThatThrownBy(() -> service.add("overflow"))
                    .isInstanceOf(CustomExtensionLimitExceededException.class);
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("커스텀 확장자 삭제")
    class Remove {

        @Test
        @DisplayName("존재하는 커스텀 확장자를 삭제한다")
        void deletesExistingCustomExtension() {
            // given
            BlockedExtension target = new BlockedExtension(SPACE_ID, ExtensionName.from("sh"));
            given(spaceIdProvider.currentSpaceId()).willReturn(SPACE_ID);
            given(repository.findBySpaceIdAndId(SPACE_ID, 1L)).willReturn(Optional.of(target));

            // when
            service.remove(1L);

            // then
            verify(repository).delete(target);
        }

        @Test
        @DisplayName("존재하지 않는 id면 예외가 발생한다")
        void rejectsWhenNotFound() {
            // given
            given(spaceIdProvider.currentSpaceId()).willReturn(SPACE_ID);
            given(repository.findBySpaceIdAndId(SPACE_ID, 999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> service.remove(999L))
                    .isInstanceOf(ExtensionNotFoundException.class);
        }

        @Test
        @DisplayName("고정 확장자 행은 이 경로로 삭제할 수 없다")
        void rejectsRemovingFixedExtensionRow() {
            // given
            BlockedExtension fixedRow = new BlockedExtension(SPACE_ID, ExtensionName.from("exe"));
            given(spaceIdProvider.currentSpaceId()).willReturn(SPACE_ID);
            given(repository.findBySpaceIdAndId(SPACE_ID, 1L)).willReturn(Optional.of(fixedRow));

            // when & then
            assertThatThrownBy(() -> service.remove(1L))
                    .isInstanceOf(ExtensionNotFoundException.class);
            verify(repository, never()).delete(any());
        }
    }
}
