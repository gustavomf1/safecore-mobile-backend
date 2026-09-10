package com.safecore.mobile.dto;

import java.util.List;

public record SyncBatchRequest(List<SyncBatchItem> items) {}
