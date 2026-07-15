package io.github.hacanna42.extensionblocker.extension.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(
        name = "blocked_extension",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_blocked_extension_space_name",
                columnNames = {"space_id", "name"}
        )
)
public class BlockedExtension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private SpaceId spaceId;

    @Embedded
    private ExtensionName extensionName;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected BlockedExtension() {
    }

    public BlockedExtension(SpaceId spaceId, ExtensionName extensionName) {
        this.spaceId = spaceId;
        this.extensionName = extensionName;
        this.createdAt = Instant.now();
    }

    public boolean isNamed(ExtensionName candidate) {
        return extensionName.equals(candidate);
    }

    public boolean isFixed() {
        return extensionName.isFixed();
    }

    public Long id() {
        return id;
    }

    public ExtensionName extensionName() {
        return extensionName;
    }
}
