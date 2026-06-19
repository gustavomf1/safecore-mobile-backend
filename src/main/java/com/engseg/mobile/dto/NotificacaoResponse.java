package com.engseg.mobile.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificacaoResponse(
        UUID id,
        UUID ncId,
        String tipo,
        String titulo,
        String corpo,
        boolean lida,
        LocalDateTime criadoEm
) {}
