package io.github.hacanna42.extensionblocker.extension.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlockedExtensionRepository extends JpaRepository<BlockedExtension, Long> {

    List<BlockedExtension> findAllBySpaceId(SpaceId spaceId);

    Optional<BlockedExtension> findBySpaceIdAndId(SpaceId spaceId, Long id);

    void deleteBySpaceIdAndExtensionName(SpaceId spaceId, ExtensionName extensionName);

    boolean existsBySpaceIdAndExtensionName(SpaceId spaceId, ExtensionName extensionName);

    @Query(value = "SELECT pg_advisory_xact_lock(:namespace, :spaceId)", nativeQuery = true)
    void lockSpace(@Param("namespace") int namespace, @Param("spaceId") int spaceId);
}
