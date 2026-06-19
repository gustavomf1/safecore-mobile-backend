package com.engseg.mobile.service;

import com.engseg.mobile.entity.NotificacaoHistorico;
import com.engseg.mobile.repository.NotificacaoHistoricoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @Mock NotificacaoHistoricoRepository repository;
    @InjectMocks NotificacaoService service;

    @Test
    void marcarComoLida_quandoNotificacaoPertenceAoUsuario_marcaComoLida() {
        UUID usuarioId = UUID.randomUUID();
        UUID notificacaoId = UUID.randomUUID();
        NotificacaoHistorico notificacao = NotificacaoHistorico.builder()
                .id(notificacaoId).usuarioId(usuarioId).ncId(UUID.randomUUID())
                .tipo("NC_ATIVADA").titulo("t").corpo("c").lida(false).criadoEm(LocalDateTime.now())
                .build();
        when(repository.findByIdAndUsuarioId(notificacaoId, usuarioId)).thenReturn(Optional.of(notificacao));

        service.marcarComoLida(usuarioId, notificacaoId);

        assertThat(notificacao.isLida()).isTrue();
        verify(repository).save(notificacao);
    }

    @Test
    void marcarComoLida_quandoNotificacaoNaoPertenceAoUsuario_lanca404() {
        UUID usuarioId = UUID.randomUUID();
        UUID notificacaoId = UUID.randomUUID();
        when(repository.findByIdAndUsuarioId(notificacaoId, usuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.marcarComoLida(usuarioId, notificacaoId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");

        verify(repository, never()).save(any());
    }

    @Test
    void marcarTodasComoLidas_delegaParaUpdateEmLoteEscopadoPorUsuario() {
        UUID usuarioId = UUID.randomUUID();

        service.marcarTodasComoLidas(usuarioId);

        verify(repository).marcarTodasComoLidas(usuarioId);
    }
}
