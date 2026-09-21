package com.safecore.mobile.event;

import java.util.UUID;

public record ExpiryAlertEvent(
        UUID ncId,
        String titulo,
        String codigo,
        int diasRestantes,
        UUID responsavelId
) {}
