package io.github.hacanna42.extensionblocker.extension.application;

import io.github.hacanna42.extensionblocker.extension.domain.SpaceId;
import org.springframework.stereotype.Component;

@Component
public class FixedCurrentSpaceIdProvider implements CurrentSpaceIdProvider {

    private static final long PLACEHOLDER_SPACE_ID = 1L;

    @Override
    public SpaceId currentSpaceId() {
        return new SpaceId(PLACEHOLDER_SPACE_ID);
    }
}
