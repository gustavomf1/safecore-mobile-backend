package com.engseg.mobile.service;

import com.engseg.mobile.dto.DeviceTokenRequest;
import com.engseg.mobile.entity.DeviceToken;
import com.engseg.mobile.repository.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceTokenService {

    private final DeviceTokenRepository repository;

    @Transactional
    public void registrar(UUID usuarioId, DeviceTokenRequest request) {
        DeviceToken token = repository.findByUsuarioId(usuarioId)
                .orElseGet(() -> DeviceToken.builder()
                        .id(UUID.randomUUID())
                        .usuarioId(usuarioId)
                        .build());

        token.setFcmToken(request.fcmToken());
        token.setPlataforma(request.plataforma());
        token.setUpdatedAt(OffsetDateTime.now());
        repository.save(token);
        log.info("DeviceToken registrado para usuario={}", usuarioId);
    }

    @Transactional
    public void removerTokenInvalido(String fcmToken) {
        repository.deleteByFcmToken(fcmToken);
        log.info("DeviceToken removido (UNREGISTERED): {}", fcmToken);
    }
}
