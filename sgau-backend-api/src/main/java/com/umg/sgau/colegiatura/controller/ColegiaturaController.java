package com.umg.sgau.colegiatura.controller;


import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.umg.sgau.colegiatura.dto.ColegiaturaRequestDTO;
import com.umg.sgau.colegiatura.entity.ColegiaturaEntity;
import com.umg.sgau.colegiatura.mapper.ColegiaturaMapper;
import com.umg.sgau.colegiatura.service.ColegiaturaService;
import com.umg.sgau.estudiante.entity.EstudianteEntity;
import com.umg.sgau.estudiante.repository.EstudianteRepository;



@RestController
@RequestMapping("/api/colegiaturas")
public class ColegiaturaController {



    private final ColegiaturaService colegiaturaService;

    private final EstudianteRepository estudianteRepository;




    public ColegiaturaController(
            ColegiaturaService colegiaturaService,
            EstudianteRepository estudianteRepository){


        this.colegiaturaService =
                colegiaturaService;

        this.estudianteRepository =
                estudianteRepository;

    }






    @PostMapping
    public ResponseEntity<?> crear(
            @RequestBody ColegiaturaRequestDTO request){



        EstudianteEntity estudiante =
                estudianteRepository
                .findById(request.getIdEstudiante())
                .orElseThrow(
                    () -> new RuntimeException(
                    "Estudiante no encontrado")
                );



        ColegiaturaEntity creada =
                colegiaturaService.crear(

                    ColegiaturaMapper.aEntidad(
                            request,
                            estudiante
                    )

                );



        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                    ColegiaturaMapper.aResponseDTO(
                            creada)
                );


    }





    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(
            @PathVariable Long id){


        return ResponseEntity.ok(

            ColegiaturaMapper.aResponseDTO(
                colegiaturaService.obtenerPorId(id)
            )

        );

    }





    @GetMapping
    public ResponseEntity<?> obtenerTodos(){


        List<ColegiaturaEntity> lista =
                colegiaturaService.obtenerTodos();



        return ResponseEntity.ok(
                ColegiaturaMapper
                .aResponseDTOList(lista)
        );


    }






    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody ColegiaturaRequestDTO request){



        EstudianteEntity estudiante =
                estudianteRepository
                .findById(request.getIdEstudiante())
                .orElseThrow();



        ColegiaturaEntity actualizada =
                colegiaturaService.actualizar(
                    id,
                    ColegiaturaMapper.aEntidad(
                            request,
                            estudiante)
                );



        return ResponseEntity.ok(
                ColegiaturaMapper.aResponseDTO(
                        actualizada)
        );


    }





    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Long id){


        colegiaturaService.eliminar(id);


        return ResponseEntity
                .noContent()
                .build();

    }



}