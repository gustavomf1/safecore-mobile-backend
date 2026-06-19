package com.engseg.mobile.consumer;

import com.engseg.mobile.entity.NotificacaoHistorico;
import com.engseg.mobile.event.DesvioKafkaEvent;
import com.engseg.mobile.event.ExpiryAlertEvent;
import com.engseg.mobile.event.NcKafkaEvent;
import com.engseg.mobile.repository.NotificacaoHistoricoRepository;
import com.engseg.mobile.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
                    .tipo(event.tipo())
                    .titulo(event.titulo())
                    .corpo(event.corpo())
                    .lida(false)
                    .criadoEm(LocalDateTime.now())
                    .build();
            notificacaoHistoricoRepository.save(historico);
            pushService.enviarParaUsuario(usuarioId, event.titulo(), event.corpo(), event.ncId(), event.tipo());
        }
    }

    @KafkaListener(topics = "engseg.desvio.events", groupId = "engseg-mobile-backend",
                   containerFactory = "desvioKafkaListenerContainerFactory")
    public void consumirDesvioEvent(DesvioKafkaEvent event) {
        log.info("MobileKafkaConsumer: Desvio event tipo={} desvioId={}", event.tipo(), event.desvioId());
        List<UUID> destinatarios = resolverDestinatariosDesvio(event);
        if (destinatarios.isEmpty()) return;

        String titulo = "EngSeg — " + event.titulo();
        String corpo = switch (event.tipo()) {
            case "DESVIO_CRIADO" -> "Novo Desvio aberto: " + event.titulo();
            case "DESVIO_STATUS_ALTERADO" -> "Desvio atualizado para " + event.status() + ": " + event.titulo();
            default -> event.titulo();
        };
        pushService.enviar(destinatarios, titulo, corpo);
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

    private List<UUID> resolverDestinatariosDesvio(DesvioKafkaEvent event) {
        List<UUID> dest = new ArrayList<>();
        if ("DESVIO_CRIADO".equals(event.tipo())) {
            if (event.responsavelId() != null) dest.add(event.responsavelId());
            if (event.responsavelTrativaId() != null
                    && !event.responsavelTrativaId().equals(event.responsavelId()))
                dest.add(event.responsavelTrativaId());
        } else {
            if (event.criadorId() != null) dest.add(event.criadorId());
            if (event.responsavelId() != null && !event.responsavelId().equals(event.criadorId()))
                dest.add(event.responsavelId());
        }
        return dest;
    }
}
