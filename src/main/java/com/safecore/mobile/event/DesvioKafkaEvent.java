package com.safecore.mobile.event;

import java.util.List;
import java.util.UUID;

public record DesvioKafkaEvent(
        UUID eventId,
        String tipo,
        UUID desvioId,
        List<UUID> destinatarios,
        String titulo,
        String corpo
) {}
