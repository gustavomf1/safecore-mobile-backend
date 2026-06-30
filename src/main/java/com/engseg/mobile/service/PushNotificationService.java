package com.engseg.mobile.service;

import com.engseg.mobile.entity.DeviceToken;
import com.engseg.mobile.repository.DeviceTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private final FirebaseMessaging firebaseMessaging;
    private final DeviceTokenRepository deviceTokenRepository;
    private final DeviceTokenService deviceTokenService;

    public void enviar(List<UUID> usuarioIds, String titulo, String corpo) {
        if (usuarioIds == null || usuarioIds.isEmpty()) return;

        List<DeviceToken> tokens = deviceTokenRepository.findByUsuarioIdIn(usuarioIds);
        for (DeviceToken token : tokens) {
            enviarParaToken(token.getFcmToken(), titulo, corpo, null, null, null);
        }
    }

    public void enviarParaUsuario(UUID usuarioId, String titulo, String corpo,
                                   UUID ncId, UUID desvioId, String tipo) {
        List<DeviceToken> tokens = deviceTokenRepository.findByUsuarioIdIn(List.of(usuarioId));
        for (DeviceToken token : tokens) {
            enviarParaToken(token.getFcmToken(), titulo, corpo, ncId, desvioId, tipo);
        }
    }

    private void enviarParaToken(String fcmToken, String titulo, String corpo,
                                  UUID ncId, UUID desvioId, String tipo) {
        Message.Builder messageBuilder = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle(titulo)
                        .setBody(corpo)
                        .build());
        if (ncId != null) messageBuilder.putData("ncId", ncId.toString());
        if (desvioId != null) messageBuilder.putData("desvioId", desvioId.toString());
        if (tipo != null) messageBuilder.putData("tipo", tipo);

        try {
            String response = firebaseMessaging.send(messageBuilder.build());
            log.debug("Push enviado: {}", response);
        } catch (FirebaseMessagingException e) {
            log.warn("Erro ao enviar push para token {}: {}", fcmToken, e.getMessage());
            if (e.getMessagingErrorCode() != null &&
                    "UNREGISTERED".equals(e.getMessagingErrorCode().name())) {
                deviceTokenService.removerTokenInvalido(fcmToken);
            }
        }
    }
}
