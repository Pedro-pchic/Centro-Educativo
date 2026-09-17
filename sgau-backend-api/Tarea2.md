# FASE 1B — Identidad académica Usuario–Perfil

## Objetivo

Implementar la base de identidad académica definida en Fase 1A:

Usuario
├── 0..1 Estudiante
└── 0..1 Docente

Permitir asociar de forma segura un Usuario con un perfil académico sin
romper los CRUD, JWT, RBAC ni datos existentes.

Esta fase NO implementa todavía `/me` ni autorización por propiedad completa.

---

## Decisiones ya aprobadas — NO reanalizar

- FK en `estudiantes.usuario_id`.
- FK en `docente.usuario_id`.
- `usuario_id` nullable durante transición.
- `UNIQUE(usuario_id)` en cada tabla.
- FK hacia `usuarios(id)`.
- Sin `ON DELETE CASCADE`.
- ADMIN no requiere perfil.
- ESTUDIANTE solo puede asociarse con Estudiante.
- DOCENTE solo puede asociarse con Docente.
- La contraseña permanece únicamente en Usuario.
- No implementar contraseña ni login por carnet.
- El login objetivo es correo + password.
- Carnet permanece como identificador académico.
- Mantener BCrypt.
- No reemplazar `JwtService` ni `JwtAuthenticationFilter`.
- No inferir asociaciones existentes por nombre/correo/carnet.

Reutilizar la arquitectura y código existentes.

---

## 1. Precheck mínimo

Antes de modificar, inspeccionar SOLO lo necesario para confirmar:

- campos reales de `UsuarioEntity`;
- si login actual usa `username`, `email` o ambos;
- unicidad existente de username/email/carnet;
- nombres reales de tablas/PK;
- estructura de migraciones existente;
- métodos relevantes de Usuario/Estudiante/Docente.

No repetir auditoría ni Fase 1A.

Si el login actual todavía depende de `username`, NO rediseñarlo en esta fase.
Documentar brevemente la diferencia con el objetivo correo+password para una
fase posterior.

No consultar ni modificar Neon.

---

## 2. Modelo JPA

Implementar la relación mínima según el modelo real.

Agregar asociación Usuario en:

- EstudianteEntity
- DocenteEntity

Requisitos:

- relación opcional;
- columna `usuario_id`;
- FK lógica hacia Usuario;
- asociación sin cascade destructivo;
- evitar serialización recursiva;
- preservar soft delete;
- no romper DTO/mappers existentes.

No agregar relaciones inversas en Usuario salvo necesidad técnica demostrada.

---

## 3. Migración

Crear una migración SQL versionada compatible con el mecanismo actual del
repositorio.

Debe:

1. agregar `usuario_id` nullable a estudiantes;
2. agregar `usuario_id` nullable a docente;
3. crear FK a usuarios(id);
4. crear UNIQUE por `usuario_id`;
5. preservar todos los registros existentes.

Antes de agregar cualquier UNIQUE adicional sobre username/email/carnet,
verificar en código/esquema que sea compatible.

Si no puede demostrarse con seguridad, NO agregarlo y reportarlo como pendiente.

La migración debe ser idempotente solo si el patrón existente del proyecto lo
requiere.

PROHIBIDO:

- ejecutar la migración contra Neon;
- DROP/TRUNCATE;
- borrar/recrear tablas;
- `migrate:fresh`;
- UPDATE automático para asociar perfiles;
- modificar datos productivos.

Solo crear el archivo de migración.

---

## 4. Asociación Usuario–Perfil

Implementar el mecanismo mínimo en capa Service para asociar un Usuario
existente con Estudiante o Docente existente.

Debe validar:

### Usuario → Estudiante

- usuario existe;
- usuario activo;
- rol ESTUDIANTE;
- estudiante existe;
- estudiante activo;
- estudiante sin usuario asociado;
- usuario no asociado previamente a otro Estudiante;
- usuario no asociado a Docente.

### Usuario → Docente

Mismas reglas, exigiendo rol DOCENTE.

ADMIN no puede asociarse como perfil académico.

La operación debe ser transaccional.

No realizar asociaciones automáticas.

---

## 5. Repositories

Agregar únicamente consultas necesarias, preferentemente `exists...` /
`find...` específicas.

Ejemplos conceptuales:

- findByUsuarioId(...)
- existsByUsuarioId(...)

Usar los nombres reales de entidades/campos.

Evitar `findAll()` para validar asociaciones.

---

## 6. API administrativa mínima

Solo si el proyecto necesita un endpoint para realizar la asociación, crear
la mínima API administrativa consistente con los controladores actuales.

Debe ser accesible únicamente por ADMIN mediante el RBAC existente.

No crear CRUD nuevo.

No implementar `/me`.

No permitir que ESTUDIANTE o DOCENTE elijan/cambien su propio `usuario_id`.

Preferir un request DTO pequeño con IDs necesarios.

---

## 7. Login

NO implementar login por carnet.

NO crear contraseña en Estudiante/Docente.

Mantener el flujo de autenticación existente funcionando.

Si actualmente el sistema autentica por `username` y no por correo:

- conservar compatibilidad;
- no hacer una refactorización del JWT;
- reportar exactamente qué cambio mínimo será necesario posteriormente para
  usar correo + password.

---

## 8. Pruebas focalizadas

Reutilizar infraestructura H2 existente.

Agregar SOLO pruebas necesarias para Fase 1B:

- asociar ESTUDIANTE válido → OK;
- asociar DOCENTE válido → OK;
- ADMIN → perfil académico → rechazado;
- rol ESTUDIANTE → Docente → rechazado;
- rol DOCENTE → Estudiante → rechazado;
- perfil ya asociado → rechazado;
- usuario ya asociado → rechazado;
- usuario/perfil inactivo → rechazado;
- registros sin asociación siguen siendo válidos;
- RBAC existente continúa funcionando.

No conectar tests a Neon.

Ejecutar primero las pruebas nuevas/focalizadas y después la suite completa
solo una vez.

---

## 9. Alcance prohibido

NO implementar en esta fase:

- `/me`;
- autorización completa por propiedad;
- cambios generales en SecurityConfig salvo proteger el endpoint administrativo;
- cambios al formato JWT;
- login por carnet;
- Android;
- rangos de notas;
- refactor general de DTO;
- RestControllerAdvice general;
- correcciones de colegiaturas;
- cambios generales de soft delete;
- Flyway/Liquibase si no existe actualmente;
- despliegue;
- commit/push/merge.

No corregir hallazgos ajenos aunque sean encontrados.

---

## 10. Eficiencia

Para minimizar trabajo/tokens:

1. reutilizar Fase 1A;
2. no repetir auditoría;
3. inspeccionar cada archivo solo cuando sea necesario;
4. usar búsquedas dirigidas antes de abrir archivos completos;
5. modificar únicamente dependencias directas;
6. no explicar código sin necesidad;
7. no generar documentación extensa;
8. no crear archivos auxiliares de reporte.

Si una condición del repositorio contradice esta tarea, detener únicamente
esa parte y reportarla; continuar con las partes seguras independientes.

---

## 11. Validación final

Ejecutar:

- pruebas focalizadas;
- suite existente completa una sola vez;
- `git diff --check`;
- `git status --short`.

No ejecutar comandos que escriban sobre Neon.

---

## Entrega

Responder únicamente con:

### Implementado
- cambios realizados

### Migración
- archivo creado
- cambios de esquema preparados
- confirmar explícitamente: NO aplicada a Neon

### Login actual
- identificador real utilizado actualmente
- compatibilidad con futuro correo + password

### Pruebas
- focalizadas: X/X
- suite completa: X/X

### Pendiente para Fase 1Cs
- máximo 5 puntos

### Git
- archivos modificados/creados
- confirmar sin commit/push/deploy

No repetir el contenido de esta tarea.