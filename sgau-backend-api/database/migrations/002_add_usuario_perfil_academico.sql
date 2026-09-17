BEGIN;

ALTER TABLE estudiantes
    ADD COLUMN IF NOT EXISTS usuario_id BIGINT;

ALTER TABLE docente
    ADD COLUMN IF NOT EXISTS usuario_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_estudiantes_usuario'
          AND conrelid = 'estudiantes'::regclass
    ) THEN
        ALTER TABLE estudiantes
            ADD CONSTRAINT fk_estudiantes_usuario
            FOREIGN KEY (usuario_id) REFERENCES usuarios(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_docente_usuario'
          AND conrelid = 'docente'::regclass
    ) THEN
        ALTER TABLE docente
            ADD CONSTRAINT fk_docente_usuario
            FOREIGN KEY (usuario_id) REFERENCES usuarios(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_estudiantes_usuario_id'
          AND conrelid = 'estudiantes'::regclass
    ) THEN
        ALTER TABLE estudiantes
            ADD CONSTRAINT uq_estudiantes_usuario_id UNIQUE (usuario_id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_docente_usuario_id'
          AND conrelid = 'docente'::regclass
    ) THEN
        ALTER TABLE docente
            ADD CONSTRAINT uq_docente_usuario_id UNIQUE (usuario_id);
    END IF;
END
$$;

COMMIT;
