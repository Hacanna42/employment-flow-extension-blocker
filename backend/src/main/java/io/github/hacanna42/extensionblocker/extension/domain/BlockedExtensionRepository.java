package io.github.hacanna42.extensionblocker.extension.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlockedExtensionRepository extends JpaRepository<BlockedExtension, Long> {

    List<BlockedExtension> findAllBySpaceId(SpaceId spaceId);

    Optional<BlockedExtension> findBySpaceIdAndId(SpaceId spaceId, Long id);

    void deleteBySpaceIdAndExtensionName(SpaceId spaceId, ExtensionName extensionName);

    @Modifying
    @Query(
            value = "INSERT INTO blocked_extension (space_id, name, created_at) "
                    + "VALUES (:spaceId, :name, :createdAt) "
                    + "ON CONFLICT (space_id, name) DO NOTHING",
            nativeQuery = true
    )
    void insertIfAbsent(@Param("spaceId") long spaceId, @Param("name") String name, @Param("createdAt") Instant createdAt);

    @Query(value = "SELECT pg_advisory_xact_lock(:namespace, :spaceId)", nativeQuery = true)
    void lockSpace(@Param("namespace") int namespace, @Param("spaceId") int spaceId);
}
