package com.umg.sgau.colegiatura.serviceimpl;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.umg.sgau.colegiatura.dto.ColegiaturaRequestDTO;
import com.umg.sgau.colegiatura.dto.ColegiaturaResponseDTO;
import com.umg.sgau.colegiatura.entity.ColegiaturaEntity;
import com.umg.sgau.colegiatura.exception.ColegiaturaNoEncontradaException;
import com.umg.sgau.colegiatura.mapper.ColegiaturaMapper;
import com.umg.sgau.colegiatura.repository.ColegiaturaRepository;
import com.umg.sgau.colegiatura.service.ColegiaturaService;

import com.umg.sgau.estudiante.entity.EstudianteEntity;
import com.umg.sgau.estudiante.exception.EstudianteNoEncontradoException;
import com.umg.sgau.estudiante.repository.EstudianteRepository;


@Service
@Transactional
public class ColegiaturaServiceimpl 
        implements ColegiaturaService {



    private final ColegiaturaRepository colegiaturaRepository;

    private final EstudianteRepository estudianteRepository;



    public ColegiaturaServiceimpl(
            ColegiaturaRepository colegiaturaRepository,
            EstudianteRepository estudianteRepository) {


        this.colegiaturaRepository = colegiaturaRepository;

        this.estudianteRepository = estudianteRepository;

    }





    @Override
    public ColegiaturaResponseDTO crear(
            ColegiaturaRequestDTO request) {


        validarDatosPago(request);



        EstudianteEntity estudiante =
                estudianteRepository
                .findByIdAndActivoTrue(
                        request.getIdEstudiante()
                )
                .orElseThrow(
                        () -> new EstudianteNoEncontradoException(
                                request.getIdEstudiante()
                        )
                );




        boolean existe =
                colegiaturaRepository
                .existsByEstudianteAndMesAndCicloAndActivoTrue(
                        estudiante,
                        request.getMes(),
                        request.getCiclo()
                );



        if(existe){

            throw new RuntimeException(
                    "Ya existe una colegiatura activa para este estudiante"
            );

        }





        ColegiaturaEntity entity =
                ColegiaturaMapper.toEntity(
                        request,
                        estudiante
                );



        entity.setActivo(true);



        ColegiaturaEntity guardada =
                colegiaturaRepository.save(entity);



        return ColegiaturaMapper.toDTO(
                guardada
        );

    }








    @Override
    @Transactional(readOnly = true)
    public ColegiaturaResponseDTO obtenerPorId(
            Long id) {



        ColegiaturaEntity entity =
                colegiaturaRepository
                .findByIdAndActivoTrue(id)
                .orElseThrow(
                        () -> new ColegiaturaNoEncontradaException(id)
                );



        return ColegiaturaMapper.toDTO(entity);

    }








    @Override
    @Transactional(readOnly = true)
    public List<ColegiaturaResponseDTO> obtenerTodos() {



        return ColegiaturaMapper.toDTOList(
                colegiaturaRepository.findAllByActivoTrue()
        );

    }


    @Override
    public ColegiaturaResponseDTO actualizar(
            Long id,
            ColegiaturaRequestDTO request) {



        validarDatosPago(request);




        ColegiaturaEntity actual =
                colegiaturaRepository
                .findByIdAndActivoTrue(id)
                .orElseThrow(
                        () -> new ColegiaturaNoEncontradaException(id)
                );





        boolean existe =
                colegiaturaRepository
                .existsByEstudianteAndMesAndCicloAndActivoTrue(
                        actual.getEstudiante(),
                        request.getMes(),
                        request.getCiclo()
                );



        if(existe &&
                (!actual.getMes().equals(request.getMes())
                ||
                !actual.getCiclo().equals(request.getCiclo()))) {


            throw new RuntimeException(
                    "Ya existe otra colegiatura activa con esos datos"
            );

        }





        actual.setMes(
                request.getMes()
        );


        actual.setCiclo(
                request.getCiclo()
        );


        actual.setMonto(
                request.getMonto()
        );


        actual.setPagado(
                request.getPagado()!=null
                ? request.getPagado()
                : false
        );



        if(Boolean.TRUE.equals(actual.getPagado())){


            actual.setFechaPago(
                    request.getFechaPago()!=null
                    ? request.getFechaPago()
                    : LocalDateTime.now()
            );


        }else{


            actual.setFechaPago(null);

        }





        ColegiaturaEntity actualizada =
                colegiaturaRepository.save(actual);



        return ColegiaturaMapper.toDTO(
                actualizada
        );

    }








    @Override
    public void eliminar(Long id) {



        ColegiaturaEntity entity =
                colegiaturaRepository
                .findByIdAndActivoTrue(id)
                .orElseThrow(
                        () -> new ColegiaturaNoEncontradaException(id)
                );



        entity.setActivo(false);



        colegiaturaRepository.save(entity);

    }









    private void validarDatosPago(
            ColegiaturaRequestDTO request) {



        if(request.getMonto()==null ||
                request.getMonto()
                .compareTo(BigDecimal.ZERO)<=0){


            throw new IllegalArgumentException(
                    "El monto debe ser mayor a cero"
            );

        }





        if(Boolean.TRUE.equals(request.getPagado())
                &&
                request.getFechaPago()==null){


            throw new IllegalArgumentException(
                    "Debe ingresar fecha de pago cuando la colegiatura está pagada"
            );

        }





        if(request.getFechaPago()!=null &&
                request.getFechaPago()
                .isAfter(LocalDateTime.now())){


            throw new IllegalArgumentException(
                    "La fecha de pago no puede ser futura"
            );

        }

    }

}

