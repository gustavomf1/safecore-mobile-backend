ALTER TABLE notificacao_historico ALTER COLUMN nc_id DROP NOT NULL;
ALTER TABLE notificacao_historico ADD COLUMN desvio_id UUID;
CREATE INDEX idx_notificacao_historico_desvio ON notificacao_historico(desvio_id) WHERE desvio_id IS NOT NULL;
