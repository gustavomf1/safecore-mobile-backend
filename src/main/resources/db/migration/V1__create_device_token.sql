CREATE TABLE device_token (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    fcm_token TEXT NOT NULL,
    plataforma VARCHAR(10),
    updated_at TIMESTAMPTZ
);

CREATE UNIQUE INDEX idx_device_token_usuario ON device_token(usuario_id);
