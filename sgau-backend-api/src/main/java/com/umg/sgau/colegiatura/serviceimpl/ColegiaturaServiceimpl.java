package com.umg.sgau.colegiatura.serviceimpl;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
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
import com.umg.sgau.usuario.entity.RolUsuario;
import com.umg.sgau.usuario.entity.UsuarioEntity;
import com.umg.sgau.usuario.exception.AsociacionAcademicaException;
import com.umg.sgau.usuario.service.IdentidadAcademicaService;


@Service
@Transactional
public class ColegiaturaServiceimpl 
        implements ColegiaturaService {



    private final ColegiaturaRepository colegiaturaRepository;

    private final EstudianteRepository estudianteRepository;
    private final IdentidadAcademicaService identidadAcademicaService;



    @Autowired
    public ColegiaturaServiceimpl(
            ColegiaturaRepository colegiaturaRepository,
            EstudianteRepository estudianteRepository,
            IdentidadAcademicaService identidadAcademicaService) {


        this.colegiaturaRepository = colegiaturaRepository;

        this.estudianteRepository = estudianteRepository;
        this.identidadAcademicaService = identidadAcademicaService;
    }

    public ColegiaturaServiceimpl(
            ColegiaturaRepository colegiaturaRepository,
            EstudianteRepository estudianteRepository) {
        this(colegiaturaRepository, estudianteRepository, null);

    }





    @Override
    public ColegiaturaResponseDTO crear(
            ColegiaturaRequestDTO request) {

        requerirAdmin();


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

        validarAcceso(entity);



        return ColegiaturaMapper.toDTO(entity);

    }








    @Override
    @Transactional(readOnly = true)
    public List<ColegiaturaResponseDTO> obtenerTodos() {

        requerirAdmin();



        return ColegiaturaMapper.toDTOList(
                colegiaturaRepository.findAllByActivoTrue()
        );

    }


    @Override
    public ColegiaturaResponseDTO actualizar(
            Long id,
            ColegiaturaRequestDTO request) {

        requerirAdmin();



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

        requerirAdmin();



        ColegiaturaEntity entity =
                colegiaturaRepository
                .findByIdAndActivoTrue(id)
                .orElseThrow(
                        () -> new ColegiaturaNoEncontradaException(id)
                );



        entity.setActivo(false);



        colegiaturaRepository.save(entity);

    }

    @Override
    @Transactional(readOnly = true)
    public List<ColegiaturaResponseDTO> obtenerMisColegiaturas() {
        if (identidadAcademicaService == null) {
            throw new AsociacionAcademicaException(
                    "No se configuró el resolver de identidad académica.");
        }

        EstudianteEntity estudiante = identidadAcademicaService.obtenerEstudianteAutenticado();
        return ColegiaturaMapper.toDTOList(
                colegiaturaRepository.findByEstudianteAndActivoTrue(estudiante));
    }

    private void requerirAdmin() {
        if (identidadAcademicaService != null
                && !identidadAcademicaService.esAdmin()) {
            throw new AsociacionAcademicaException(
                    "Solo ADMIN puede administrar colegiaturas.");
        }
    }

    private void validarAcceso(ColegiaturaEntity entity) {
        if (identidadAcademicaService == null) {
            return;
        }

        UsuarioEntity usuario = identidadAcademicaService.obtenerUsuarioAutenticado();
        if (usuario.getRol() == RolUsuario.ADMIN) {
            return;
        }
        if (usuario.getRol() == RolUsuario.ESTUDIANTE
                && entity.getEstudiante() != null
                && identidadAcademicaService.obtenerEstudianteAutenticado()
                        .getId().equals(entity.getEstudiante().getId())) {
            return;
        }
        throw new AsociacionAcademicaException(
                "No tiene permisos para consultar esta colegiatura.");
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
