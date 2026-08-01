package com.umg.sgau.docente.serviceimpl;

import com.umg.sgau.docente.entity.DocenteEntity;
import com.umg.sgau.docente.exception.DocenteNoEncontradoException;
import com.umg.sgau.docente.repository.DocenteRepository;
import com.umg.sgau.docente.service.DocenteService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocenteServiceImpl implements DocenteService {
	
	private final DocenteRepository docenteRepository;
	
	@Override
    public DocenteEntity registrarDocente(DocenteEntity nuevoDocente) {
        boolean yaExisteDpiOEmail = docenteRepository.findAll().stream()
                .anyMatch(d -> d.getDpi().equals(nuevoDocente.getDpi()) || 
                               d.getEmailInstitucional().equalsIgnoreCase(nuevoDocente.getEmailInstitucional()));

        if (yaExisteDpiOEmail) {
            throw new IllegalArgumentException("El DPI o el correo institucional ya se encuentran registrados.");
        }
        return docenteRepository.save(nuevoDocente);
    }

    @Override
    public List<DocenteEntity> obtenerDocentesActivos() {
        return docenteRepository.findAll().stream()
        		.filter(DocenteEntity::getActivo)
        		.collect(Collectors.toList());
    }

    @Override
    public DocenteEntity buscarPorDpi(String dpi) {
        return docenteRepository.findAll().stream()
        		.filter(docente ->docente.getDpi().equals(dpi))
        		.findFirst()
        		.orElseThrow(() -> new RuntimeException("No se encontró ningún docente con el DPI: " + dpi));
    }
    
    @Override
    public DocenteEntity buscarPorId(Long id) {
        return docenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró ningún docente con el ID: " + id));
    }
    
    @Override
    public List<DocenteEntity> obtenerTodos(){
    	return docenteRepository.findAll();
    }

    @Override
    public List<DocenteEntity> buscarPorEspecialidad(String especialidad) {
        return docenteRepository.findAll().stream()
        		.filter(docente -> docente.getEspecialidad() !=null &&
        				docente.getEspecialidad().toLowerCase().contains(especialidad.toLowerCase()))
        		.collect(Collectors.toList());
    }
    
    @Override
    public DocenteEntity actualizar(Long id, DocenteEntity docente) {
    	return docenteRepository.findById(id)
    			.map(docenteActual-> {
    		    	docenteActual.setNombre(docente.getNombre());
    		    	docenteActual.setApellido(docente.getApellido());
    		    	docenteActual.setEmailInstitucional(docente.getEmailInstitucional());
    		    	docenteActual.setEmailPersonal(docente.getEmailPersonal());
    		    	docenteActual.setDpi(docente.getDpi());
    		    	docenteActual.setTelefono(docente.getTelefono());
    		    	docenteActual.setEspecialidad(docente.getEspecialidad());
    		    	docenteActual.setFechaContratacion(docente.getFechaContratacion());
    		    
    		    	return docenteRepository.save(docenteActual);   		
    			})
    			.orElseThrow(() -> new DocenteNoEncontradoException(id));
    	
	}
    
    @Override
    public void eliminar(Long id) {
        docenteRepository.findById(id)
                .map(docente -> {
                    docente.setActivo(false);
                    return docenteRepository.save(docente);
                })
                .orElseThrow(() -> new DocenteNoEncontradoException(id));
    }
    
    @Override
    public void habilitar(Long id) {
        docenteRepository.findById(id)
                .map(docente -> {
                    docente.setActivo(true);
                    return docenteRepository.save(docente);
                })
                .orElseThrow(() -> new DocenteNoEncontradoException(id));
    }

    @Override
    public Page<DocenteEntity> obtenerTodosPaginados(Pageable pageable) {
        return docenteRepository.findAll(pageable);
    }

    @Override
    public Page<DocenteEntity> buscarPorFiltros(String filtro, Pageable pageable) {
    	return docenteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrEmailInstitucionalContainingIgnoreCaseOrEspecialidadContainingIgnoreCaseOrDpiContaining(
    		    filtro, filtro, filtro, filtro, filtro, pageable
    		);
    }
}