package com.umg.sgau.docente.controller; 

import com.umg.sgau.docente.exception.DocenteNoEncontradoException; 
import com.umg.sgau.docente.dto.DocenteRequestDTO; 
import com.umg.sgau.docente.dto.DocenteResponseDTO; 
import com.umg.sgau.docente.entity.DocenteEntity; 
import com.umg.sgau.docente.mapper.DocenteMapper; 
import com.umg.sgau.docente.service.DocenteService;
import com.umg.sgau.curso.dto.CursoResponseDTO;
import com.umg.sgau.curso.service.CursoService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity; 
import org.springframework.web.bind.annotation.*; 
import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/docentes") 
public class DocenteController { 

    private final DocenteService docenteService; 
    private final CursoService cursoService;
 
    public DocenteController(
            DocenteService docenteService,
            CursoService cursoService) {
        this.docenteService = docenteService; 
        this.cursoService = cursoService;
    } 

    @GetMapping("/me")
    public ResponseEntity<DocenteResponseDTO> obtenerAutenticado() {
        return ResponseEntity.ok(
                DocenteMapper.aResponseDTO(docenteService.obtenerAutenticado())
        );
    }

    @GetMapping("/me/cursos")
    public ResponseEntity<List<CursoResponseDTO>> obtenerCursosAutenticado() {
        return ResponseEntity.ok(cursoService.obtenerPorDocenteAutenticado());
    }
 
    // Registrar catedrático
    @PostMapping 
    public ResponseEntity<DocenteResponseDTO> crear(@Valid @RequestBody DocenteRequestDTO request) {
        DocenteEntity docenteCreado = docenteService.registrarDocente(DocenteMapper.aEntidad(request));
        DocenteResponseDTO response = DocenteMapper.aResponseDTO(docenteCreado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } 
 
    // Buscar por DPI
    @GetMapping("/dpi/{dpi}") 
    public ResponseEntity<DocenteResponseDTO> obtenerPorDpi(@PathVariable String dpi) {
        DocenteEntity docente = docenteService.buscarPorDpi(dpi);
        return ResponseEntity.ok(DocenteMapper.aResponseDTO(docente));
    } 
    
    // Buscar por ID
    @GetMapping("/{id}") 
    public ResponseEntity<DocenteResponseDTO> buscarPorId(@PathVariable Long id) {
        DocenteEntity docente = docenteService.buscarPorId(id);
        return ResponseEntity.ok(DocenteMapper.aResponseDTO(docente));
    } 
    // Buscar por Especialidad
    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<List<DocenteResponseDTO>> buscarPorEspecialidad(
            @PathVariable String especialidad) {
        List<DocenteEntity> docente = docenteService.buscarPorEspecialidad(especialidad);
        return ResponseEntity.ok(DocenteMapper.aResponseDTOList(docente));
    }
    
    // Buscar por Docentes activos
    @GetMapping("/activos")
    public ResponseEntity<List<DocenteResponseDTO>> obtenerDocentesActivos() {
        List<DocenteEntity> docente = docenteService.obtenerDocentesActivos();
        return ResponseEntity.ok(DocenteMapper.aResponseDTOList(docente));
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
    public ResponseEntity<DocenteResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DocenteRequestDTO request) {
        DocenteEntity docenteActualizado = docenteService.actualizar(
                id, DocenteMapper.aEntidad(request));
        return ResponseEntity.ok(DocenteMapper.aResponseDTO(docenteActualizado));
    } 
 
    // Inhabilitar catedrático (Eliminación lógica)
    @DeleteMapping("/{id}") 
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        docenteService.eliminar(id);
        return ResponseEntity.noContent().build();
    } 

    // Habilitar catedrático
    @PutMapping("/{id}/habilitar")
    public ResponseEntity<Void> habilitar(@PathVariable Long id) {
        docenteService.habilitar(id);
        return ResponseEntity.ok().build();
    }
}
