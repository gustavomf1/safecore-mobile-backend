package com.engseg.mobile.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DesvioKafkaEventContractTest {

    @Test
    void deserializaJsonNoFormatoEmitidoPeloEngsegApi() throws Exception {
        String json = """
                {
                  "eventId": "11111111-1111-1111-1111-111111111111",
                  "tipo": "DESVIO_ATIVADO",
                  "desvioId": "22222222-2222-2222-2222-222222222222",
                  "destinatarios": [
                    "33333333-3333-3333-3333-333333333333",
                    "44444444-4444-4444-4444-444444444444"
                  ],
                  "titulo": "EngSeg — Desvio ativado",
                  "corpo": "Titulo do Desvio está aguardando sua tratativa."
                }
                """;

        ObjectMapper mapper = new ObjectMapper();
        DesvioKafkaEvent event = mapper.readValue(json, DesvioKafkaEvent.class);

        assertThat(event.eventId()).isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        assertThat(event.tipo()).isEqualTo("DESVIO_ATIVADO");
        assertThat(event.desvioId()).isEqualTo(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        assertThat(event.destinatarios()).containsExactly(
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                UUID.fromString("44444444-4444-4444-4444-444444444444")
        );
        assertThat(event.titulo()).isEqualTo("EngSeg — Desvio ativado");
        assertThat(event.corpo()).contains("aguardando sua tratativa");
    }
}
