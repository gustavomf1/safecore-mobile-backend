package com.safecore.mobile.consumer;

import com.safecore.mobile.entity.NotificacaoHistorico;
import com.safecore.mobile.event.DesvioKafkaEvent;
import com.safecore.mobile.event.ExpiryAlertEvent;
import com.safecore.mobile.event.NcKafkaEvent;
import com.safecore.mobile.repository.NotificacaoHistoricoRepository;
import com.safecore.mobile.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MobileKafkaConsumer {

    private final PushNotificationService pushService;
    private final NotificacaoHistoricoRepository notificacaoHistoricoRepository;

    @KafkaListener(topics = "engseg.nc.events", groupId = "engseg-mobile-backend",
                   containerFactory = "ncKafkaListenerContainerFactory")
    public void consumirNcEvent(NcKafkaEvent event) {
        if (notificacaoHistoricoRepository.existsByEventId(event.eventId())) {
            log.debug("MobileKafkaConsumer: evento {} já processado, ignorado", event.eventId());
            return;
        }

        List<UUID> destinatarios = event.destinatarios() != null ? event.destinatarios() : List.of();
        if (destinatarios.isEmpty()) return;

        for (UUID usuarioId : destinatarios) {
            NotificacaoHistorico historico = NotificacaoHistorico.builder()
                    .id(UUID.randomUUID())
                    .eventId(event.eventId())
                    .usuarioId(usuarioId)
                    .ncId(event.ncId())
                    .desvioId(null)
                    .tipo(event.tipo())
                    .titulo(event.titulo())
                    .corpo(event.corpo())
                    .lida(false)
                    .criadoEm(LocalDateTime.now())
                    .build();
            notificacaoHistoricoRepository.save(historico);
            pushService.enviarParaUsuario(usuarioId, event.titulo(), event.corpo(), event.ncId(), null, event.tipo());
        }
    }

    @KafkaListener(topics = "engseg.desvio.events", groupId = "engseg-mobile-backend",
                   containerFactory = "desvioKafkaListenerContainerFactory")
    public void consumirDesvioEvent(DesvioKafkaEvent event) {
        if (notificacaoHistoricoRepository.existsByEventId(event.eventId())) {
            log.debug("MobileKafkaConsumer: evento desvio {} já processado, ignorado", event.eventId());
            return;
        }

        List<UUID> destinatarios = event.destinatarios() != null ? event.destinatarios() : List.of();
        if (destinatarios.isEmpty()) return;

        for (UUID usuarioId : destinatarios) {
            NotificacaoHistorico historico = NotificacaoHistorico.builder()
                    .id(UUID.randomUUID())
                    .eventId(event.eventId())
                    .usuarioId(usuarioId)
                    .ncId(null)
                    .desvioId(event.desvioId())
                    .tipo(event.tipo())
                    .titulo(event.titulo())
                    .corpo(event.corpo())
                    .lida(false)
                    .criadoEm(LocalDateTime.now())
                    .build();
            notificacaoHistoricoRepository.save(historico);
            pushService.enviarParaUsuario(usuarioId, event.titulo(), event.corpo(), null, event.desvioId(), event.tipo());
        }
    }

    @KafkaListener(topics = "engseg.expiry.alerts", groupId = "engseg-mobile-backend",
                   containerFactory = "expiryKafkaListenerContainerFactory")
    public void consumirExpiryAlert(ExpiryAlertEvent event) {
        log.info("MobileKafkaConsumer: expiry alert ncId={} diasRestantes={}", event.ncId(), event.diasRestantes());
        if (event.responsavelId() == null) return;
        pushService.enviar(List.of(event.responsavelId()),
                "EngSeg — NC vencendo em " + event.diasRestantes() + " dias",
                event.titulo() + " vence em " + event.diasRestantes() + " dias.");
    }

}
