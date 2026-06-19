package com.engseg.mobile.consumer;

import com.engseg.mobile.entity.NotificacaoHistorico;
import com.engseg.mobile.event.ExpiryAlertEvent;
import com.engseg.mobile.event.NcKafkaEvent;
import com.engseg.mobile.repository.NotificacaoHistoricoRepository;
import com.engseg.mobile.service.PushNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MobileKafkaConsumerTest {

    @Mock PushNotificationService pushService;
    @Mock NotificacaoHistoricoRepository notificacaoHistoricoRepository;
    @InjectMocks MobileKafkaConsumer consumer;

    @Test
    void consumirNcEvent_salvaHistoricoEEnviaPushParaCadaDestinatario() {
        UUID eventId = UUID.randomUUID();
        UUID ncId = UUID.randomUUID();
        UUID destino1 = UUID.randomUUID();
        UUID destino2 = UUID.randomUUID();
        NcKafkaEvent event = new NcKafkaEvent(eventId, "NC_PLANO_REPROVADO", ncId,
                List.of(destino1, destino2), "EngSeg — NC Teste", "corpo do push");

        when(notificacaoHistoricoRepository.existsByEventId(eventId)).thenReturn(false);

        consumer.consumirNcEvent(event);

        ArgumentCaptor<NotificacaoHistorico> captor = ArgumentCaptor.forClass(NotificacaoHistorico.class);
        verify(notificacaoHistoricoRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(NotificacaoHistorico::getUsuarioId)
                .containsExactlyInAnyOrder(destino1, destino2);

        verify(pushService).enviarParaUsuario(destino1, "EngSeg — NC Teste", "corpo do push", ncId, "NC_PLANO_REPROVADO");
        verify(pushService).enviarParaUsuario(destino2, "EngSeg — NC Teste", "corpo do push", ncId, "NC_PLANO_REPROVADO");
    }

    @Test
    void consumirNcEvent_eventoJaProcessado_ignoraRedelivery() {
        UUID eventId = UUID.randomUUID();
        NcKafkaEvent event = new NcKafkaEvent(eventId, "NC_ATIVADA", UUID.randomUUID(),
                List.of(UUID.randomUUID()), "titulo", "corpo");

        when(notificacaoHistoricoRepository.existsByEventId(eventId)).thenReturn(true);

        consumer.consumirNcEvent(event);

        verify(notificacaoHistoricoRepository, never()).save(any());
        verify(pushService, never()).enviarParaUsuario(any(), any(), any(), any(), any());
    }

    @Test
    void consumirNcEvent_destinatariosVazios_naoSalvaNemEnviaPush() {
        UUID eventId = UUID.randomUUID();
        NcKafkaEvent event = new NcKafkaEvent(eventId, "NC_CRIADA", UUID.randomUUID(),
                List.of(), "titulo", "corpo");

        when(notificacaoHistoricoRepository.existsByEventId(eventId)).thenReturn(false);

        consumer.consumirNcEvent(event);

        verify(notificacaoHistoricoRepository, never()).save(any());
        verify(pushService, never()).enviarParaUsuario(any(), any(), any(), any(), any());
    }

    @Test
    void expiryAlertDeveNotificarResponsavelTratativa() {
        UUID responsavelId = UUID.randomUUID();
        ExpiryAlertEvent event = new ExpiryAlertEvent(
                UUID.randomUUID(), "NC Vencendo", 10, responsavelId);

        consumer.consumirExpiryAlert(event);

        verify(pushService).enviar(eq(List.of(responsavelId)), any(), any());
    }
}
