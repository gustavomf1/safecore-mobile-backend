package com.safecore.mobile.service;

import com.safecore.mobile.dto.DeviceTokenRequest;
import com.safecore.mobile.entity.DeviceToken;
import com.safecore.mobile.repository.DeviceTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceTokenServiceTest {

    @Mock DeviceTokenRepository repository;
    @InjectMocks DeviceTokenService service;

    @Test
    void deveCriarNovoTokenQuandoUsuarioNaoPossuiToken() {
        UUID usuarioId = UUID.randomUUID();
        DeviceTokenRequest req = new DeviceTokenRequest(null, "fcm-token-abc", "ANDROID");
        when(repository.findByUsuarioId(usuarioId)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.registrar(usuarioId, req);

        ArgumentCaptor<DeviceToken> captor = ArgumentCaptor.forClass(DeviceToken.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getUsuarioId()).isEqualTo(usuarioId);
        assertThat(captor.getValue().getFcmToken()).isEqualTo("fcm-token-abc");
        assertThat(captor.getValue().getPlataforma()).isEqualTo("ANDROID");
    }

    @Test
    void deveAtualizarTokenQuandoUsuarioJaPossuiToken() {
        UUID usuarioId = UUID.randomUUID();
        DeviceToken existing = DeviceToken.builder()
                .id(UUID.randomUUID())
                .usuarioId(usuarioId)
                .fcmToken("token-antigo")
                .plataforma("IOS")
                .build();
        DeviceTokenRequest req = new DeviceTokenRequest(null, "token-novo", "IOS");
        when(repository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.registrar(usuarioId, req);

        ArgumentCaptor<DeviceToken> captor = ArgumentCaptor.forClass(DeviceToken.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getFcmToken()).isEqualTo("token-novo");
        assertThat(captor.getValue().getId()).isEqualTo(existing.getId());
    }
}
