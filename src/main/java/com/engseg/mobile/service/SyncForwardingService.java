package com.engseg.mobile.service;

import com.engseg.mobile.dto.SyncBatchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncForwardingService {

    private final RestTemplate restTemplate;

    @Value("${engseg-api.base-url}")
    private String engsegApiBaseUrl;

    public Map encaminhar(SyncBatchRequest batch, String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);

        HttpEntity<SyncBatchRequest> entity = new HttpEntity<>(batch, headers);
        String url = engsegApiBaseUrl + "/api/sync/ocorrencias";

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        log.info("SyncForwardingService: batch encaminhado, status={}", response.getStatusCode());
        return response.getBody();
    }
}
