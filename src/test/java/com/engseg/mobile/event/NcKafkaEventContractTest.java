package com.engseg.mobile.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NcKafkaEventContractTest {

    @Test
    void deserializaJsonNoFormatoEmitidoPeloEngsegApi() throws Exception {
        UUID eventId = UUID.randomUUID();
        UUID ncId = UUID.randomUUID();
        UUID destinatario = UUID.randomUUID();
        String json = """
                {
                  "eventId": "%s",
                  "tipo": "NC_PLANO_REPROVADO",
                  "ncId": "%s",
                  "destinatarios": ["%s"],
                  "titulo": "EngSeg — Vazamento na linha 3",
                  "corpo": "Plano da NC \\"Vazamento na linha 3\\": ✅ Isolar a área aprovada"
                }
                """.formatted(eventId, ncId, destinatario);

        NcKafkaEvent event = new ObjectMapper().readValue(json, NcKafkaEvent.class);

        assertThat(event.eventId()).isEqualTo(eventId);
        assertThat(event.tipo()).isEqualTo("NC_PLANO_REPROVADO");
        assertThat(event.ncId()).isEqualTo(ncId);
        assertThat(event.destinatarios()).containsExactly(destinatario);
        assertThat(event.titulo()).isEqualTo("EngSeg — Vazamento na linha 3");
        assertThat(event.corpo()).contains("Isolar a área aprovada");
    }
}
