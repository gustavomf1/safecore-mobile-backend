package com.engseg.mobile.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.Map;

public class SyncBatchItem {
    private String localId;
    private String tipo;
    private final Map<String, Object> extra = new LinkedHashMap<>();

    public String getLocalId() { return localId; }
    public void setLocalId(String localId) { this.localId = localId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    @JsonAnyGetter
    public Map<String, Object> getExtra() { return extra; }

    @JsonAnySetter
    public void setExtra(String key, Object value) { extra.put(key, value); }
}
