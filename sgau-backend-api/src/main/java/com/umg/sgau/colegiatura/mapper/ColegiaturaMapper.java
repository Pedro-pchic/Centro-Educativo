package com.umg.sgau.colegiatura.mapper;


import java.util.List;
import java.util.stream.Collectors;

import com.umg.sgau.colegiatura.dto.ColegiaturaRequestDTO;
import com.umg.sgau.colegiatura.dto.ColegiaturaResponseDTO;
import com.umg.sgau.colegiatura.entity.ColegiaturaEntity;
import com.umg.sgau.estudiante.entity.EstudianteEntity;



public class ColegiaturaMapper {


    private ColegiaturaMapper(){
    }



    public static ColegiaturaEntity aEntidad(
            ColegiaturaRequestDTO dto,
            EstudianteEntity estudiante){


        ColegiaturaEntity colegiatura =
                new ColegiaturaEntity();


        colegiatura.setMes(dto.getMes());

        colegiatura.setCiclo(dto.getCiclo());

        colegiatura.setMonto(dto.getMonto());

        colegiatura.setPagado(dto.getPagado());

        colegiatura.setFechaPago(dto.getFechaPago());


        // ESTE ERA EL QUE FALTABA
        colegiatura.setEstudiante(estudiante);


        return colegiatura;

    }




    public static ColegiaturaResponseDTO aResponseDTO(
            ColegiaturaEntity colegiatura){


        ColegiaturaResponseDTO dto =
                new ColegiaturaResponseDTO();



        dto.setId(colegiatura.getId());

        dto.setMes(colegiatura.getMes());

        dto.setCiclo(colegiatura.getCiclo());

        dto.setMonto(colegiatura.getMonto());

        dto.setPagado(colegiatura.getPagado());

        dto.setActivo(colegiatura.getActivo());

        dto.setFechaPago(
                colegiatura.getFechaPago()
        );


        dto.setFechaCreacion(
                colegiatura.getFechaCreacion()
        );



        if(colegiatura.getEstudiante()!=null){

            dto.setIdEstudiante(
                    colegiatura.getEstudiante().getId()
            );

        }


        return dto;

    }




    public static List<ColegiaturaResponseDTO> aResponseDTOList(
            List<ColegiaturaEntity> lista){


        return lista.stream()
                .map(ColegiaturaMapper::aResponseDTO)
                .collect(Collectors.toList());

    }


}  

