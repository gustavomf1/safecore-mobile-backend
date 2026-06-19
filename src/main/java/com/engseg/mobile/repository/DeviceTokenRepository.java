package com.engseg.mobile.repository;

import com.engseg.mobile.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, UUID> {
    Optional<DeviceToken> findByUsuarioId(UUID usuarioId);
    List<DeviceToken> findByUsuarioIdIn(List<UUID> usuarioIds);
    void deleteByFcmToken(String fcmToken);
}
