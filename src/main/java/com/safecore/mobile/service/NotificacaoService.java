package com.safecore.mobile.service;

import com.safecore.mobile.dto.NotificacaoResponse;
import com.safecore.mobile.entity.NotificacaoHistorico;
import com.safecore.mobile.repository.NotificacaoHistoricoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoHistoricoRepository repository;

    public Page<NotificacaoResponse> listar(UUID usuarioId, Pageable pageable) {
        return repository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId, pageable)
                .map(this::toResponse);
    }

    public long contarNaoLidas(UUID usuarioId) {
        return repository.countByUsuarioIdAndLidaFalse(usuarioId);
    }

    @Transactional
    public void marcarComoLida(UUID usuarioId, UUID notificacaoId) {
        NotificacaoHistorico notificacao = repository.findByIdAndUsuarioId(notificacaoId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificação não encontrada"));
        notificacao.setLida(true);
        repository.save(notificacao);
    }

    @Transactional
    public void marcarTodasComoLidas(UUID usuarioId) {
        repository.marcarTodasComoLidas(usuarioId);
    }

    private NotificacaoResponse toResponse(NotificacaoHistorico n) {
        return new NotificacaoResponse(n.getId(), n.getNcId(), n.getDesvioId(),
                n.getTipo(), n.getTitulo(), n.getCorpo(),
                n.isLida(), n.getCriadoEm());
    }
}
