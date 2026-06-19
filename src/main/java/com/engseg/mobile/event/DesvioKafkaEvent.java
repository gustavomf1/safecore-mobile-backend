package com.engseg.mobile.event;

import java.util.UUID;

public record DesvioKafkaEvent(
        String tipo,
        UUID desvioId,
        String titulo,
        String status,
        UUID responsavelId,
        UUID responsavelTrativaId,
        UUID criadorId
) {}
