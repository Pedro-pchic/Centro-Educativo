BEGIN;

ALTER TABLE usuarios
    ADD COLUMN IF NOT EXISTS rol VARCHAR(20);

UPDATE usuarios
SET rol = 'ADMIN'
WHERE username = 'Admin1';

UPDATE usuarios
SET rol = 'DOCENTE'
WHERE username = 'Celeste01';

UPDATE usuarios
SET rol = 'ESTUDIANTE'
WHERE username = 'pedro.chic';

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM usuarios
        WHERE username = 'Admin1'
          AND rol = 'ADMIN'
    ) THEN
        RAISE EXCEPTION 'No se encontró el usuario Admin1 como ADMIN.';
    END IF;
END
$$;

ALTER TABLE usuarios
    ALTER COLUMN rol SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'usuarios_rol_check'
          AND conrelid = 'usuarios'::regclass
    ) THEN
        ALTER TABLE usuarios
            ADD CONSTRAINT usuarios_rol_check
            CHECK (rol IN ('ADMIN', 'DOCENTE', 'ESTUDIANTE'));
    END IF;
END
$$;

COMMIT;
