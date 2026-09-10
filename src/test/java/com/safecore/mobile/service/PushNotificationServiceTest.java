package com.safecore.mobile.service;

import com.safecore.mobile.entity.DeviceToken;
import com.safecore.mobile.repository.DeviceTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PushNotificationServiceTest {

    @Mock FirebaseMessaging firebaseMessaging;
    @Mock DeviceTokenRepository deviceTokenRepository;
    @Mock DeviceTokenService deviceTokenService;
    @InjectMocks PushNotificationService service;

    @Test
    void enviar_naoInclueDataPayload() throws Exception {
        UUID usuarioId = UUID.randomUUID();
        DeviceToken token = DeviceToken.builder().id(UUID.randomUUID()).usuarioId(usuarioId).fcmToken("tok-1").build();
        when(deviceTokenRepository.findByUsuarioIdIn(List.of(usuarioId))).thenReturn(List.of(token));
        when(firebaseMessaging.send(any(Message.class))).thenReturn("ok");

        service.enviar(List.of(usuarioId), "Titulo", "Corpo");

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(firebaseMessaging).send(captor.capture());
        
        Message message = captor.getValue();
        Map<String, String> data = extractMessageData(message);
        assertThat(data).isEmpty();
    }

    @Test
    void enviarParaUsuario_inclueNcIdETipoNoDataPayload() throws Exception {
        UUID usuarioId = UUID.randomUUID();
        UUID ncId = UUID.randomUUID();
        DeviceToken token = DeviceToken.builder().id(UUID.randomUUID()).usuarioId(usuarioId).fcmToken("tok-1").build();
        when(deviceTokenRepository.findByUsuarioIdIn(List.of(usuarioId))).thenReturn(List.of(token));
        when(firebaseMessaging.send(any(Message.class))).thenReturn("ok");

        service.enviarParaUsuario(usuarioId, "Titulo", "Corpo", ncId, null, "NC_PLANO_REPROVADO");

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(firebaseMessaging).send(captor.capture());
        
        Message message = captor.getValue();
        Map<String, String> data = extractMessageData(message);
        assertThat(data).containsEntry("ncId", ncId.toString());
        assertThat(data).containsEntry("tipo", "NC_PLANO_REPROVADO");
    }

    @Test
    void enviarParaUsuario_com_desvioId_inclui_desvioId_no_payload() throws Exception {
        UUID usuarioId = UUID.randomUUID();
        UUID desvioId = UUID.randomUUID();
        DeviceToken token = DeviceToken.builder().id(UUID.randomUUID()).usuarioId(usuarioId).fcmToken("tok-1").build();
        when(deviceTokenRepository.findByUsuarioIdIn(List.of(usuarioId))).thenReturn(List.of(token));
        when(firebaseMessaging.send(any(Message.class))).thenReturn("ok");

        service.enviarParaUsuario(usuarioId, "Titulo", "Corpo", null, desvioId, "DESVIO_ATIVADO");

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(firebaseMessaging).send(captor.capture());

        Map<String, String> data = extractMessageData(captor.getValue());
        assertThat(data).containsEntry("desvioId", desvioId.toString());
        assertThat(data).containsEntry("tipo", "DESVIO_ATIVADO");
        assertThat(data).doesNotContainKey("ncId");
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> extractMessageData(Message message) throws Exception {
        java.lang.reflect.Field field = Message.class.getDeclaredField("data");
        field.setAccessible(true);
        Map<String, String> data = (Map<String, String>) field.get(message);
        return data != null ? data : java.util.Collections.emptyMap();
    }
}
