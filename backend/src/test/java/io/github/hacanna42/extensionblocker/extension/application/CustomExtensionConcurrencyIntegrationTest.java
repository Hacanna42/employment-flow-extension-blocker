package io.github.hacanna42.extensionblocker.extension.application;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.hacanna42.extensionblocker.extension.domain.BlockedExtension;
import io.github.hacanna42.extensionblocker.extension.domain.BlockedExtensionRepository;
import io.github.hacanna42.extensionblocker.extension.domain.ExtensionName;
import io.github.hacanna42.extensionblocker.extension.domain.SpaceId;
import io.github.hacanna42.extensionblocker.extension.exception.CustomExtensionLimitExceededException;
import io.github.hacanna42.extensionblocker.support.AbstractIntegrationTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CustomExtensionConcurrencyIntegrationTest extends AbstractIntegrationTest {

    private static final SpaceId SPACE_ID = new SpaceId(1L);
    private static final int CUSTOM_EXTENSIONS_NEAR_CAPACITY = 199;
    private static final int CONCURRENT_REQUEST_COUNT = 20;

    @Autowired
    private CustomExtensionService customExtensionService;

    @Autowired
    private BlockedExtensionRepository repository;

    @BeforeEach
    void seedNearCapacity() {
        repository.deleteAll();
        for (int i = 0; i < CUSTOM_EXTENSIONS_NEAR_CAPACITY; i++) {
            repository.save(new BlockedExtension(SPACE_ID, ExtensionName.from("seed" + i)));
        }
    }

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("199개인 상태에서 20개 요청이 동시에 추가를 시도하면 advisory lock 덕분에 정확히 1건만 성공한다")
    void exactlyOneSucceedsAtCapacityBoundary() throws InterruptedException {
        // given
        CountDownLatch readyLatch = new CountDownLatch(CONCURRENT_REQUEST_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(CONCURRENT_REQUEST_COUNT);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger limitExceededCount = new AtomicInteger();
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_REQUEST_COUNT);

        // when
        for (int i = 0; i < CONCURRENT_REQUEST_COUNT; i++) {
            String candidateName = "race" + i;
            executor.submit(() -> attemptAdd(candidateName, readyLatch, startLatch, successCount, limitExceededCount, doneLatch));
        }
        readyLatch.await();
        startLatch.countDown();
        boolean completedInTime = doneLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // then
        assertThat(completedInTime).isTrue();
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(limitExceededCount.get()).isEqualTo(CONCURRENT_REQUEST_COUNT - 1);
        assertThat(repository.findAllBySpaceId(SPACE_ID)).hasSize(200);
    }

    private void attemptAdd(
            String candidateName,
            CountDownLatch readyLatch,
            CountDownLatch startLatch,
            AtomicInteger successCount,
            AtomicInteger limitExceededCount,
            CountDownLatch doneLatch
    ) {
        readyLatch.countDown();
        awaitUninterruptibly(startLatch);
        try {
            customExtensionService.add(candidateName);
            successCount.incrementAndGet();
        } catch (CustomExtensionLimitExceededException e) {
            limitExceededCount.incrementAndGet();
        } finally {
            doneLatch.countDown();
        }
    }

    private void awaitUninterruptibly(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
