package com.safecore.mobile.controller;

import com.safecore.mobile.dto.DeviceTokenRequest;
import com.safecore.mobile.security.AuthenticatedUser;
import com.safecore.mobile.service.DeviceTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceTokenController {

    private final DeviceTokenService deviceTokenService;

    @PostMapping("/token")
    public ResponseEntity<Void> registrar(@Valid @RequestBody DeviceTokenRequest request,
                                          @AuthenticationPrincipal AuthenticatedUser user) {
        // usuarioId vem do JWT autenticado, nunca do corpo (anti-spoofing)
        deviceTokenService.registrar(user.uid(), request);
        return ResponseEntity.ok().build();
    }
}
