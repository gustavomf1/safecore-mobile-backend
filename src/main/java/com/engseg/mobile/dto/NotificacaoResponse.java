package com.engseg.mobile.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificacaoResponse(
        UUID id,
        UUID ncId,
        UUID desvioId,
        String tipo,
        String titulo,
        String corpo,
        boolean lida,
        LocalDateTime criadoEm
) {}
