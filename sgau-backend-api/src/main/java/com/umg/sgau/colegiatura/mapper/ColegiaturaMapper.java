package com.umg.sgau.colegiatura.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.umg.sgau.colegiatura.dto.ColegiaturaRequestDTO;
import com.umg.sgau.colegiatura.dto.ColegiaturaResponseDTO;
import com.umg.sgau.colegiatura.entity.ColegiaturaEntity;
import com.umg.sgau.estudiante.entity.EstudianteEntity;


public class ColegiaturaMapper {


    private ColegiaturaMapper() {
    }



    public static ColegiaturaEntity toEntity(
            ColegiaturaRequestDTO dto,
            EstudianteEntity estudiante) {


        ColegiaturaEntity entity = new ColegiaturaEntity();


        entity.setMes(dto.getMes());

        entity.setCiclo(dto.getCiclo());

        entity.setMonto(dto.getMonto());


        entity.setPagado(
                dto.getPagado() != null
                        ? dto.getPagado()
                        : false
        );


        entity.setFechaPago(
                dto.getFechaPago()
        );


        entity.setEstudiante(
                estudiante
        );


        return entity;
    }




    public static ColegiaturaResponseDTO toDTO(
            ColegiaturaEntity entity) {


        ColegiaturaResponseDTO dto =
                new ColegiaturaResponseDTO();


        dto.setId(entity.getId());

        dto.setMes(entity.getMes());

        dto.setCiclo(entity.getCiclo());

        dto.setMonto(entity.getMonto());

        dto.setPagado(entity.getPagado());

        dto.setActivo(entity.getActivo());

        dto.setFechaPago(
                entity.getFechaPago()
        );

        dto.setFechaCreacion(
                entity.getFechaCreacion()
        );


        if(entity.getEstudiante()!=null){

            dto.setIdEstudiante(
                    entity.getEstudiante().getId()
            );
        }


        return dto;
    }





    public static List<ColegiaturaResponseDTO> toDTOList(
            List<ColegiaturaEntity> lista) {


        return lista.stream()
                .map(ColegiaturaMapper::toDTO)
                .collect(Collectors.toList());

    }

}

