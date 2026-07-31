package com.umg.sgau.docente.controller; 

import com.umg.sgau.docente.exception.DocenteNoEncontradoException; 
import com.umg.sgau.docente.dto.DocenteRequestDTO; 
import com.umg.sgau.docente.dto.DocenteResponseDTO; 
import com.umg.sgau.docente.entity.DocenteEntity; 
import com.umg.sgau.docente.mapper.DocenteMapper; 
import com.umg.sgau.docente.service.DocenteService; 

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity; 
import org.springframework.web.bind.annotation.*; 

@RestController 
@RequestMapping("/api/docentes") 
public class DocenteController { 

    private final DocenteService docenteService; 
 
    public DocenteController(DocenteService docenteService) { 
        this.docenteService = docenteService; 
    } 
 
    // Registrar catedrático
    @PostMapping 
    public ResponseEntity<?> crear(@RequestBody DocenteRequestDTO request) { 
        try {
            DocenteEntity docenteCreado = docenteService.registrarDocente(DocenteMapper.aEntidad(request)); 
            DocenteResponseDTO response = DocenteMapper.aResponseDTO(docenteCreado); 
            return ResponseEntity.status(HttpStatus.CREATED).body(response); 
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    } 
 
    @GetMapping("/{dpi}") 
    public ResponseEntity<?> obtenerPorDpi(@PathVariable String dpi) { 
        try { 
            DocenteEntity docente = docenteService.buscarPorDpi(dpi); 
            return ResponseEntity.ok(DocenteMapper.aResponseDTO(docente)); 
        } catch (RuntimeException ex) { 
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()); 
        } 
    } 
 
    // Consultar catedráticos (con paginación) y Buscar mediante filtros
    @GetMapping 
    public ResponseEntity<Page<DocenteResponseDTO>> obtenerTodos(
            @RequestParam(required = false) String filtro,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) { 
        
        Pageable pageable = PageRequest.of(page, size);
        Page<DocenteEntity> docentesPage;

        if (filtro != null && !filtro.trim().isEmpty()) {
            docentesPage = docenteService.buscarPorFiltros(filtro, pageable);
        } else {
            docentesPage = docenteService.obtenerTodosPaginados(pageable);
        }

        Page<DocenteResponseDTO> responsePage = docentesPage.map(DocenteMapper::aResponseDTO); 
        return ResponseEntity.ok(responsePage); 
    } 
 
    // Actualizar información de catedráticos
    @PutMapping("/{id}") 
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody DocenteRequestDTO request) { 
        try { 
            DocenteEntity docenteActualizado = docenteService.actualizar(id, DocenteMapper.aEntidad(request)); 
            return ResponseEntity.ok(DocenteMapper.aResponseDTO(docenteActualizado)); 
        } catch (DocenteNoEncontradoException ex) { 
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()); 
        } 
    } 
 
    // Inhabilitar catedrático (Eliminación lógica)
    @DeleteMapping("/{id}") 
    public ResponseEntity<?> eliminar(@PathVariable Long id) { 
        try { 
            docenteService.eliminar(id); 
            return ResponseEntity.noContent().build(); 
        } catch (DocenteNoEncontradoException ex) { 
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()); 
        } 
    } 

    // Habilitar catedrático
    @PutMapping("/{id}/habilitar")
    public ResponseEntity<?> habilitar(@PathVariable Long id) {
        try {
            docenteService.habilitar(id);
            return ResponseEntity.ok().build();
        } catch (DocenteNoEncontradoException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}