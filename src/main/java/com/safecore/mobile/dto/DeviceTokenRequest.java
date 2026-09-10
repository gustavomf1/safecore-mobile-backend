package com.safecore.mobile.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

// usuarioId é ignorado pelo servidor (derivado do JWT); mantido por compatibilidade do payload.
public record DeviceTokenRequest(
        UUID usuarioId,
        @NotBlank String fcmToken,
        String plataforma
) {}
