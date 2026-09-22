package com.umg.sgau.error;

import com.umg.sgau.carrera.exception.CarreraDuplicadaException;
import com.umg.sgau.carrera.exception.CarreraNoEncontradaException;
import com.umg.sgau.colegiatura.exception.ColegiaturaDuplicadaException;
import com.umg.sgau.colegiatura.exception.ColegiaturaNoEncontradaException;
import com.umg.sgau.curso.exception.CursoDuplicadoException;
import com.umg.sgau.curso.exception.CursoNoEncontradoException;
import com.umg.sgau.curso.exception.DocenteInactivoParaCursoException;
import com.umg.sgau.docente.exception.DocenteNoEncontradoException;
import com.umg.sgau.estudiante.exception.EstudianteNoEncontradoException;
import com.umg.sgau.inscripcion.exception.InscripcionDuplicadaException;
import com.umg.sgau.inscripcion.exception.InscripcionNoEncontradaException;
import com.umg.sgau.inscripcion.exception.InscripcionRelacionInactivaException;
import com.umg.sgau.nota.exception.NotaNoEncontradaException;
import com.umg.sgau.usuario.exception.AsociacionAcademicaException;
import com.umg.sgau.usuario.exception.UsuarioNoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return response(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos",
                request, fieldErrors);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class,
            IllegalArgumentException.class,
            InscripcionRelacionInactivaException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            Exception exception,
            HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "Solicitud inválida", request, null);
    }

    @ExceptionHandler({
            CarreraNoEncontradaException.class,
            ColegiaturaNoEncontradaException.class,
            CursoNoEncontradoException.class,
            DocenteNoEncontradoException.class,
            EstudianteNoEncontradoException.class,
            InscripcionNoEncontradaException.class,
            NotaNoEncontradaException.class,
            UsuarioNoEncontradoException.class
    })
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            RuntimeException exception,
            HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), request, null);
    }

    @ExceptionHandler({
            CarreraDuplicadaException.class,
            ColegiaturaDuplicadaException.class,
            CursoDuplicadoException.class,
            InscripcionDuplicadaException.class,
            DocenteInactivoParaCursoException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<ApiErrorResponse> handleConflict(
            Exception exception,
            HttpServletRequest request) {
        String message = exception instanceof DataIntegrityViolationException
                ? "No se pudo completar la operación por un conflicto de integridad."
                : exception.getMessage();
        return response(HttpStatus.CONFLICT, message, request, null);
    }

    @ExceptionHandler({
            AsociacionAcademicaException.class,
            AccessDeniedException.class
    })
    public ResponseEntity<ApiErrorResponse> handleForbidden(
            Exception exception,
            HttpServletRequest request) {
        return response(HttpStatus.FORBIDDEN,
                "No tiene permisos para este recurso.", request, null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(
            AuthenticationException exception,
            HttpServletRequest request) {
        return response(HttpStatus.UNAUTHORIZED, "Credenciales inválidas", request, null);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(
            ResponseStatusException exception,
            HttpServletRequest request) {
        HttpStatusCode status = exception.getStatusCode();
        HttpStatus httpStatus = HttpStatus.resolve(status.value());
        if (httpStatus == null) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String message = httpStatus.is4xxClientError()
                ? (exception.getReason() == null ? "Solicitud inválida" : exception.getReason())
                : "Ocurrió un error interno.";
        return response(httpStatus, message, request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
            Exception exception,
            HttpServletRequest request) {
        log.error("Error inesperado {} {}: {}",
                request.getMethod(), request.getRequestURI(),
                exception.getClass().getSimpleName());
        return response(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno.", request, null);
    }

    private ResponseEntity<ApiErrorResponse> response(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> fieldErrors) {
        ApiErrorResponse body = new ApiErrorResponse(
                java.time.Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                fieldErrors);
        return ResponseEntity.status(status).body(body);
    }
}
