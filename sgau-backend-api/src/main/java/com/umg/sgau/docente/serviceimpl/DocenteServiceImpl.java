package com.umg.sgau.docente.serviceimpl;

import com.umg.sgau.docente.entity.DocenteEntity;
import com.umg.sgau.docente.exception.DocenteNoEncontradoException;
import com.umg.sgau.docente.repository.DocenteRepository;
import com.umg.sgau.docente.service.DocenteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocenteServiceImpl implements DocenteService {
	
	private final DocenteRepository docenteRepository;
	
	public DocenteServiceImpl(DocenteRepository docenteRepository) {
		this.docenteRepository = docenteRepository;
	}
	
    @Override
    public DocenteEntity registrarDocente(DocenteEntity nuevoDocente) {
        Optional<DocenteEntity> existenteDpi = docenteRepository.findByDpi(nuevoDocente.getDpi());
        if (existenteDpi.isPresent()) {
            throw new IllegalArgumentException("Ya existe un docente con el DPI: " + nuevoDocente.getDpi());
        }

        Optional<DocenteEntity> existenteCorreo = docenteRepository.findByEmailInstitucional(nuevoDocente.getEmailInstitucional());
        if (existenteCorreo.isPresent()) {
            throw new IllegalArgumentException("El correo institucional ya está en uso.");
        }

        return docenteRepository.save(nuevoDocente);
    }

    @Override
    public List<DocenteEntity> obtenerDocentesActivos() {
        return docenteRepository.findByActivoTrue();
    }

    @Override
    public DocenteEntity buscarPorDpi(String dpi) {
        return docenteRepository.findByDpi(dpi)
                .orElseThrow(() -> new RuntimeException("No se encontró ningún docente con el DPI: " + dpi));
    }
    
    @Override
    public DocenteEntity buscarPorId(String id) {
        return docenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró ningún docente con el ID: " + id));
    }
    
    @Override
    public List<DocenteEntity> obtenerTodos(){
    	return docenteRepository.findAll();
    }

    @Override
    public List<DocenteEntity> buscarPorEspecialidad(String especialidad) {
        return docenteRepository.findByEspecialidadContainingIgnoreCase(especialidad);
    }
    
    @Override
    public DocenteEntity actualizar(Long id, DocenteEntity docente) {
    	Optional<DocenteEntity> docenteExistente = docenteRepository.findById(id);
    
    	if (docenteExistente.isEmpty()) {
    		throw new DocenteNoEncontradoException(id);
    	}
    	
    	DocenteEntity docenteActual = docenteExistente.get();
    	docenteActual.setNombre(docente.getNombre());
    	docenteActual.setApellido(docente.getApellido());
    	docenteActual.setEmailInstitucional(docente.getEmailInstitucional());
    	docenteActual.setEmailPersonal(docente.getEmailPersonal());
    	docenteActual.setDpi(docente.getDpi());
    	docenteActual.setTelefono(docente.getTelefono());
    	docenteActual.setEspecialidad(docente.getEspecialidad());
    	docenteActual.setFechaContratacion(docente.getFechaContratacion());
    
    	
    	return docenteRepository.save(docenteActual);
	}
    
    @Override
    public void eliminar(Long id) {
    	DocenteEntity docenteExistente = docenteRepository.findById(id)
    			.orElseThrow(() -> new DocenteNoEncontradoException(id));
    	
    	docenteExistente.setActivo(false);
    	docenteRepository.save(docenteExistente);
    }
    
    public void habilitar(Long id) {
    	DocenteEntity docenteExistente = docenteRepository.findById(id)
    			.orElseThrow(() -> new DocenteNoEncontradoException(id));
    	
    	docenteExistente.setActivo(true);
    	docenteRepository.save(docenteExistente);
    }

    @Override
    public Page<DocenteEntity> obtenerTodosPaginados(Pageable pageable) {
        return docenteRepository.findAll(pageable);
    }

    @Override
    public Page<DocenteEntity> buscarPorFiltros(String filtro, Pageable pageable) {
        return docenteRepository.buscarPorMultiplesFiltros(filtro, pageable);
    }
}