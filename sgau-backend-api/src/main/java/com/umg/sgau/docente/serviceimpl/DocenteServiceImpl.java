package com.umg.sgau.docente.serviceimpl;

import com.umg.sgau.docente.entity.DocenteEntity;
import com.umg.sgau.docente.exception.DocenteNoEncontradoException;
import com.umg.sgau.docente.repository.DocenteRepository;
import com.umg.sgau.docente.service.DocenteService;
import com.umg.sgau.usuario.exception.AsociacionAcademicaException;
import com.umg.sgau.usuario.service.IdentidadAcademicaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DocenteServiceImpl implements DocenteService {
	
	private final DocenteRepository docenteRepository;
	private final IdentidadAcademicaService identidadAcademicaService;

	@Autowired
	public DocenteServiceImpl(
			DocenteRepository docenteRepository,
			IdentidadAcademicaService identidadAcademicaService) {
		this.docenteRepository = docenteRepository;
		this.identidadAcademicaService = identidadAcademicaService;
	}

	public DocenteServiceImpl(DocenteRepository docenteRepository) {
		this(docenteRepository, null);
	}
	
	@Override
    public DocenteEntity registrarDocente(DocenteEntity nuevoDocente) {
		requerirAdmin();
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
		requerirAdmin();
        return docenteRepository.findAll().stream()
        		.filter(DocenteEntity::getActivo)
        		.collect(Collectors.toList());
    }

    @Override
    public DocenteEntity buscarPorDpi(String dpi) {
		requerirAdmin();
        return docenteRepository.findAll().stream()
        		.filter(docente ->docente.getDpi().equals(dpi))
        		.filter(DocenteEntity::getActivo)
        		.findFirst()
        		.orElseThrow(() -> new RuntimeException("No se encontró ningún docente con el DPI: " + dpi));
    }
    
    @Override
    public DocenteEntity buscarPorId(Long id) {
		validarAccesoDocente(id);
        return docenteRepository.findById(id)
                .filter(docente -> Boolean.TRUE.equals(docente.getActivo()))
                .orElseThrow(() -> new RuntimeException("No se encontró ningún docente con el ID: " + id));
    }
    
    @Override
    public List<DocenteEntity> obtenerTodos(){
		requerirAdmin();
    	return docenteRepository.findAll().stream()
    			.filter(DocenteEntity::getActivo)
    			.collect(Collectors.toList());
    }

    @Override
    public List<DocenteEntity> buscarPorEspecialidad(String especialidad) {
		requerirAdmin();
        return docenteRepository.findAll().stream()
        		.filter(docente -> docente.getEspecialidad() !=null &&
        				docente.getEspecialidad().toLowerCase().contains(especialidad.toLowerCase()))
        		.filter(DocenteEntity::getActivo)
        		.collect(Collectors.toList());
    }
    
    @Override
    public DocenteEntity actualizar(Long id, DocenteEntity docente) {
		requerirAdmin();
		DocenteEntity docenteActual = docenteRepository.findById(id)
				.filter(docenteEncontrado -> Boolean.TRUE.equals(docenteEncontrado.getActivo()))
				.orElseThrow(() -> new DocenteNoEncontradoException(id));

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
		requerirAdmin();
        docenteRepository.findById(id)
                .map(docente -> {
                    docente.setActivo(false);
                    return docenteRepository.save(docente);
                })
                .orElseThrow(() -> new DocenteNoEncontradoException(id));
    }
    
    @Override
    public void habilitar(Long id) {
		requerirAdmin();
        docenteRepository.findById(id)
                .map(docente -> {
                    docente.setActivo(true);
                    return docenteRepository.save(docente);
                })
                .orElseThrow(() -> new DocenteNoEncontradoException(id));
    }

    @Override
    public Page<DocenteEntity> obtenerTodosPaginados(Pageable pageable) {
		requerirAdmin();
        return docenteRepository.findByActivoTrue(pageable);
    		
    }

    @Override
    public Page<DocenteEntity> buscarPorFiltros(String filtro, Pageable pageable) {
		requerirAdmin();
    	return docenteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrEmailInstitucionalContainingIgnoreCaseOrEspecialidadContainingIgnoreCaseOrDpiContaining(
    		    filtro, filtro, filtro, filtro, filtro, pageable
			);
    }

	@Override
	public DocenteEntity obtenerAutenticado() {
		if (identidadAcademicaService == null) {
			throw new AsociacionAcademicaException(
					"No se configuró el resolver de identidad académica.");
		}
		return identidadAcademicaService.obtenerDocenteAutenticado();
	}

	private void requerirAdmin() {
		if (identidadAcademicaService != null
				&& !identidadAcademicaService.esAdmin()) {
			throw new AsociacionAcademicaException(
					"Solo ADMIN puede administrar docentes.");
		}
	}

	private void validarAccesoDocente(Long id) {
		if (identidadAcademicaService == null) {
			return;
		}

		if (identidadAcademicaService.esAdmin()) {
			return;
		}

		if (identidadAcademicaService.obtenerDocenteAutenticado()
				.getId().equals(id)) {
			return;
		}

		throw new AsociacionAcademicaException(
				"No tiene permisos para consultar este docente.");
	}
}
