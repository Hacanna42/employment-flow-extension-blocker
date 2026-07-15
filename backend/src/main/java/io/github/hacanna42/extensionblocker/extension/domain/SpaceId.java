package io.github.hacanna42.extensionblocker.extension.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record SpaceId(@Column(name = "space_id", nullable = false) Long value) {
}
