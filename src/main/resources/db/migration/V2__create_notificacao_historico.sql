CREATE TABLE notificacao_historico (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL,
    usuario_id UUID NOT NULL,
    nc_id UUID NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    titulo TEXT NOT NULL,
    corpo TEXT NOT NULL,
    lida BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX idx_notificacao_historico_event_usuario ON notificacao_historico(event_id, usuario_id);
CREATE INDEX idx_notificacao_historico_usuario ON notificacao_historico(usuario_id, criado_em DESC);
