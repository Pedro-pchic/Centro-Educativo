package com.umg.sgau.estudiante.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EstudianteRequestDTO {

    @NotBlank(message = "El código del estudiante es obligatorio")
    @Size(max = 20, message = "El código del estudiante no puede superar 20 caracteres")
    private String codigoEstudiante;

    @NotBlank(message = "El carnet es obligatorio")
    @Size(max = 20, message = "El carnet no puede superar 20 caracteres")
    private String carnet;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    @Size(max = 100, message = "El correo no puede superar 100 caracteres")
    private String email;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60, message = "El nombre no puede superar 60 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 60, message = "El apellido no puede superar 60 caracteres")
    private String apellido;

}
