package com.engseg.mobile.controller;

import com.engseg.mobile.dto.NotificacaoResponse;
import com.engseg.mobile.security.AuthenticatedUser;
import com.engseg.mobile.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @GetMapping
    public ResponseEntity<Page<NotificacaoResponse>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(notificacaoService.listar(user.uid(), PageRequest.of(page, size)));
    }

    @PostMapping("/{id}/lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable UUID id,
                                                @AuthenticationPrincipal AuthenticatedUser user) {
        notificacaoService.marcarComoLida(user.uid(), id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lidas")
    public ResponseEntity<Void> marcarTodasComoLidas(@AuthenticationPrincipal AuthenticatedUser user) {
        notificacaoService.marcarTodasComoLidas(user.uid());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nao-lidas/contagem")
    public ResponseEntity<Map<String, Long>> contarNaoLidas(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(Map.of("contagem", notificacaoService.contarNaoLidas(user.uid())));
    }
}
