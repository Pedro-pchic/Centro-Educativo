package com.umg.sgau.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank
    private String username;

    /**
     * Base64 RSA-OAEP SHA-256 ciphertext. This is the preferred login field.
     */
    @Size(max = 4096)
    private String passwordEncrypted;

    /**
     * Temporary backwards-compatible field. Its use is controlled by the
     * app.auth.login.allow-legacy-password property.
     */
    @Size(max = 1024)
    private String password;
}
