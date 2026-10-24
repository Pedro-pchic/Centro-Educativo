package com.umg.sgau.auth.dto;

public record PublicKeyResponseDTO(
        String publicKey,
        String algorithm,
        String hash,
        String format) {
}
