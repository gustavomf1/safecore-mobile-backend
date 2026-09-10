package com.safecore.mobile.service;

import com.safecore.mobile.dto.SyncBatchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncForwardingServiceTest {

    @Mock RestTemplate restTemplate;
    @InjectMocks SyncForwardingService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "engsegApiBaseUrl", "http://test-api:8080");
    }

    @Test
    void deveEncaminharBatchComAuthorizationHeader() {
        SyncBatchRequest batch = new SyncBatchRequest(List.of());
        String token = "Bearer jwt-token-123";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(Map.of("results", List.of())));

        service.encaminhar(batch, token);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq("http://test-api:8080/api/sync/ocorrencias"),
                eq(HttpMethod.POST),
                captor.capture(),
                eq(Map.class));
        assertThat(captor.getValue().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .isEqualTo(token);
    }
}
