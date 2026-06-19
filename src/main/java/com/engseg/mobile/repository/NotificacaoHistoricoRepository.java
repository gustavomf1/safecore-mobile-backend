package com.engseg.mobile.repository;

import com.engseg.mobile.entity.NotificacaoHistorico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface NotificacaoHistoricoRepository extends JpaRepository<NotificacaoHistorico, UUID> {

    boolean existsByEventId(UUID eventId);

    Page<NotificacaoHistorico> findByUsuarioIdOrderByCriadoEmDesc(UUID usuarioId, Pageable pageable);

    Optional<NotificacaoHistorico> findByIdAndUsuarioId(UUID id, UUID usuarioId);

    long countByUsuarioIdAndLidaFalse(UUID usuarioId);

    @Modifying
    @Query("UPDATE NotificacaoHistorico n SET n.lida = true WHERE n.usuarioId = :usuarioId AND n.lida = false")
    void marcarTodasComoLidas(@Param("usuarioId") UUID usuarioId);
}
