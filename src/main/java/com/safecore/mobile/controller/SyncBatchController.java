package com.safecore.mobile.controller;

import com.safecore.mobile.dto.SyncBatchRequest;
import com.safecore.mobile.service.SyncForwardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
public class SyncBatchController {

    private final SyncForwardingService syncForwardingService;

    @PostMapping("/batch")
    public ResponseEntity<Map> syncBatch(
            @RequestBody SyncBatchRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        Map result = syncForwardingService.encaminhar(request, authorization);
        return ResponseEntity.ok(result);
    }
}
