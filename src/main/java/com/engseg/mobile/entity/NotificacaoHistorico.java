package com.engseg.mobile.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificacao_historico")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoHistorico {

    @Id
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "nc_id", nullable = false)
    private UUID ncId;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String corpo;

    @Column(nullable = false)
    @Builder.Default
    private boolean lida = false;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;
}
