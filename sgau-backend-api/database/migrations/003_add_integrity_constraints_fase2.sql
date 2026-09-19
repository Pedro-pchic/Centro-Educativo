BEGIN;

CREATE UNIQUE INDEX IF NOT EXISTS uq_inscripciones_estudiante_curso_activa
    ON inscripciones (estudiante_id, curso_id)
    WHERE activo IS TRUE;

CREATE UNIQUE INDEX IF NOT EXISTS uq_nota_inscripcion_activa
    ON nota (inscripcion_id)
    WHERE activo IS TRUE;

COMMIT;
