package com.umg.sgau.colegiatura.serviceimpl;


import com.umg.sgau.colegiatura.entity.ColegiaturaEntity;
import com.umg.sgau.colegiatura.exception.ColegiaturaNoEncontradaException;
import com.umg.sgau.colegiatura.repository.ColegiaturaRepository;
import com.umg.sgau.colegiatura.service.ColegiaturaService;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
public class ColegiaturaServiceimpl 
        implements ColegiaturaService {


    private final ColegiaturaRepository colegiaturaRepository;



    public ColegiaturaServiceimpl(
            ColegiaturaRepository colegiaturaRepository){

        this.colegiaturaRepository =
                colegiaturaRepository;

    }





    @Override
    public ColegiaturaEntity crear(
            ColegiaturaEntity colegiatura){


        colegiatura.setActivo(true);


        return colegiaturaRepository.save(colegiatura);

    }





    @Override
    public ColegiaturaEntity obtenerPorId(Long id){


        Optional<ColegiaturaEntity> encontrada =
                colegiaturaRepository.findById(id);



        if(encontrada.isEmpty()){

            throw new ColegiaturaNoEncontradaException(id);

        }



        ColegiaturaEntity colegiatura =
                encontrada.get();



        if(!Boolean.TRUE.equals(
                colegiatura.getActivo())){


            throw new ColegiaturaNoEncontradaException(id);

        }



        return colegiatura;


    }






    @Override
    public List<ColegiaturaEntity> obtenerTodos(){


        return colegiaturaRepository.findAll()
                .stream()
                .filter(c ->
                    Boolean.TRUE.equals(c.getActivo()))
                .collect(Collectors.toList());

    }







    @Override
    public ColegiaturaEntity actualizar(
            Long id,
            ColegiaturaEntity colegiatura){


        ColegiaturaEntity actual =
                obtenerPorId(id);



        actual.setMes(colegiatura.getMes());

        actual.setCiclo(colegiatura.getCiclo());

        actual.setMonto(colegiatura.getMonto());

        actual.setPagado(colegiatura.getPagado());

        actual.setFechaPago(colegiatura.getFechaPago());

        actual.setEstudiante(
                colegiatura.getEstudiante()
        );


        return colegiaturaRepository.save(actual);

    }








    @Override
    public void eliminar(Long id){


        ColegiaturaEntity colegiatura =
                obtenerPorId(id);



        colegiatura.setActivo(false);



        colegiaturaRepository.save(colegiatura);


    }



}

