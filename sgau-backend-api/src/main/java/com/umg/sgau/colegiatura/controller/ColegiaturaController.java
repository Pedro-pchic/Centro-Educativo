package com.umg.sgau.colegiatura.controller;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.umg.sgau.colegiatura.dto.ColegiaturaRequestDTO;
import com.umg.sgau.colegiatura.dto.ColegiaturaResponseDTO;
import com.umg.sgau.colegiatura.service.ColegiaturaService;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/colegiaturas")
@RequiredArgsConstructor
public class ColegiaturaController {



    private final ColegiaturaService colegiaturaService;




    @PostMapping
    public ResponseEntity<ColegiaturaResponseDTO> crear(
            @Valid @RequestBody ColegiaturaRequestDTO request) {


        return new ResponseEntity<>(
                colegiaturaService.crear(request),
                HttpStatus.CREATED
        );

    }





    @GetMapping
    public ResponseEntity<List<ColegiaturaResponseDTO>> obtenerTodos(){


        return ResponseEntity.ok(
                colegiaturaService.obtenerTodos()
        );

    }





    @GetMapping("/{id}")
    public ResponseEntity<ColegiaturaResponseDTO> obtenerPorId(
            @PathVariable Long id){


        return ResponseEntity.ok(
                colegiaturaService.obtenerPorId(id)
        );

    }





    @PutMapping("/{id}")
    public ResponseEntity<ColegiaturaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ColegiaturaRequestDTO request){


        return ResponseEntity.ok(
                colegiaturaService.actualizar(id, request)
        );

    }





    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id){


        colegiaturaService.eliminar(id);


        return ResponseEntity.noContent().build();

    }


}



