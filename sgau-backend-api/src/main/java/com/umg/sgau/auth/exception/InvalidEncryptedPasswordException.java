package com.umg.sgau.auth.exception;

/**
 * Indicates that the encrypted password cannot be decoded or decrypted.
 *
 * <p>It deliberately extends {@link IllegalArgumentException} so the global
 * API handler returns a generic 400 response without exposing cryptographic
 * details.</p>
 */
public class InvalidEncryptedPasswordException extends IllegalArgumentException {

    public InvalidEncryptedPasswordException() {
        super("La contraseña cifrada no es válida");
    }
}
