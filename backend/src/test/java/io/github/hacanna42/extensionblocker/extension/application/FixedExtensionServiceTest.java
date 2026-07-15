package io.github.hacanna42.extensionblocker.extension.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import io.github.hacanna42.extensionblocker.extension.api.dto.FixedExtensionResponse;
import io.github.hacanna42.extensionblocker.extension.domain.BlockedExtension;
import io.github.hacanna42.extensionblocker.extension.domain.BlockedExtensionRepository;
import io.github.hacanna42.extensionblocker.extension.domain.ExtensionName;
import io.github.hacanna42.extensionblocker.extension.domain.SpaceId;
import io.github.hacanna42.extensionblocker.extension.exception.ExtensionNotFoundException;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FixedExtensionServiceTest {

    private static final SpaceId SPACE_ID = new SpaceId(1L);

    @Mock
    private BlockedExtensionRepository repository;

    @Mock
    private CurrentSpaceIdProvider spaceIdProvider;

    private FixedExtensionService service;

    @BeforeEach
    void setUp() {
        service = new FixedExtensionService(repository, spaceIdProvider);
    }

    @Nested
    @DisplayName("고정 확장자 목록 조회")
    class List_ {

        @Test
        @DisplayName("체크된 확장자만 checked=true로 표시한다")
        void marksOnlyCheckedExtensionAsChecked() {
            // given
            given(spaceIdProvider.currentSpaceId()).willReturn(SPACE_ID);
            given(repository.findAllBySpaceId(SPACE_ID))
                    .willReturn(List.of(new BlockedExtension(SPACE_ID, ExtensionName.from("exe"))));

            // when
            List<FixedExtensionResponse> result = service.list();

            // then
            assertThat(result).hasSize(7);
            assertThat(result)
                    .filteredOn(response -> response.name().equals("exe"))
                    .extracting(FixedExtensionResponse::checked)
                    .containsExactly(true);
            assertThat(result)
                    .filteredOn(response -> !response.name().equals("exe"))
                    .extracting(FixedExtensionResponse::checked)
                    .containsOnly(false);
        }

        @Test
        @DisplayName("체크된 확장자가 없으면 전부 checked=false다")
        void marksAllAsUncheckedWhenNoneStored() {
            // given
            given(spaceIdProvider.currentSpaceId()).willReturn(SPACE_ID);
            given(repository.findAllBySpaceId(SPACE_ID)).willReturn(List.of());

            // when
            List<FixedExtensionResponse> result = service.list();

            // then
            assertThat(result).extracting(FixedExtensionResponse::checked).containsOnly(false);
        }
    }

    @Nested
    @DisplayName("고정 확장자 체크/해제")
    class UpdateChecked {

        @Test
        @DisplayName("체크하면 멱등 insert(ON CONFLICT DO NOTHING)를 호출한다")
        void insertsIdempotentlyWhenChecking() {
            // given
            given(spaceIdProvider.currentSpaceId()).willReturn(SPACE_ID);

            // when
            service.updateChecked("exe", true);

            // then
            verify(repository).insertIfAbsent(eq(SPACE_ID.value().longValue()), eq("exe"), any(Instant.class));
        }

        @Test
        @DisplayName("체크를 해제하면 저장된 행을 삭제한다")
        void deletesWhenUnchecking() {
            // given
            given(spaceIdProvider.currentSpaceId()).willReturn(SPACE_ID);

            // when
            service.updateChecked("exe", false);

            // then
            verify(repository).deleteBySpaceIdAndExtensionName(SPACE_ID, ExtensionName.from("exe"));
        }

        @Test
        @DisplayName("고정 확장자 목록에 없는 이름이면 예외가 발생하고 저장소를 건드리지 않는다")
        void rejectsUnknownFixedExtensionName() {
            // when & then
            assertThatThrownBy(() -> service.updateChecked("zip", true))
                    .isInstanceOf(ExtensionNotFoundException.class);
        }
    }
}
