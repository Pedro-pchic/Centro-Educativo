package com.umg.sgau.security;

import com.umg.sgau.auth.exception.InvalidEncryptedPasswordException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * RSA-OAEP SHA-256 crypto operations used by the authentication flow.
 *
 * <p>The public key is encoded as X.509 SubjectPublicKeyInfo (SPKI), which is
 * the format accepted by the Web Crypto API. Private keys are expected in
 * PKCS#8 PEM format and must be supplied outside the repository.</p>
 */
@Service
public class RsaCryptoService {

    private static final int MINIMUM_KEY_SIZE_BITS = 2048;
    private static final int MAX_CIPHERTEXT_BASE64_LENGTH = 4096;
    private static final String GENERIC_CONFIGURATION_ERROR =
            "La configuración criptográfica RSA no es válida";

    private static final OAEPParameterSpec OAEP_SHA256 = new OAEPParameterSpec(
            "SHA-256",
            "MGF1",
            MGF1ParameterSpec.SHA256,
            PSource.PSpecified.DEFAULT);

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final String publicKeyBase64;

    public RsaCryptoService(
            @Value("${app.auth.rsa.private-key:}") String privateKeyPem,
            @Value("${app.auth.rsa.public-key:}") String publicKeyPem,
            @Value("${app.auth.rsa.allow-ephemeral:false}") boolean allowEphemeralKey) {
        KeyPair keyPair = loadKeyPair(privateKeyPem, publicKeyPem, allowEphemeralKey);
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
        this.publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public String getPublicKeyBase64() {
        return publicKeyBase64;
    }

    public String decryptPassword(String encryptedPassword) {
        if (encryptedPassword == null
                || encryptedPassword.isBlank()
                || encryptedPassword.length() > MAX_CIPHERTEXT_BASE64_LENGTH) {
            throw new InvalidEncryptedPasswordException();
        }

        byte[] ciphertext = null;
        byte[] plaintext = null;
        try {
            ciphertext = Base64.getDecoder().decode(encryptedPassword);
            int expectedCiphertextLength = (((RSAPublicKey) publicKey)
                    .getModulus()
                    .bitLength() + Byte.SIZE - 1) / Byte.SIZE;
            if (ciphertext.length != expectedCiphertextLength) {
                throw new InvalidEncryptedPasswordException();
            }

            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey, OAEP_SHA256);
            plaintext = cipher.doFinal(ciphertext);
            if (plaintext.length == 0) {
                throw new InvalidEncryptedPasswordException();
            }
            return decodeUtf8(plaintext);
        } catch (IllegalArgumentException
                | GeneralSecurityException
                | CharacterCodingException exception) {
            throw new InvalidEncryptedPasswordException();
        } finally {
            if (ciphertext != null) {
                Arrays.fill(ciphertext, (byte) 0);
            }
            if (plaintext != null) {
                Arrays.fill(plaintext, (byte) 0);
            }
        }
    }

    private KeyPair loadKeyPair(
            String privateKeyPem,
            String publicKeyPem,
            boolean allowEphemeralKey) {
        boolean hasPrivateKey = hasText(privateKeyPem);
        boolean hasPublicKey = hasText(publicKeyPem);

        try {
            if (!hasPrivateKey && !hasPublicKey) {
                if (!allowEphemeralKey) {
                    throw new IllegalStateException(GENERIC_CONFIGURATION_ERROR);
                }
                return generateEphemeralKeyPair();
            }
            if (!hasPrivateKey || !hasPublicKey) {
                throw new IllegalStateException(GENERIC_CONFIGURATION_ERROR);
            }

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey loadedPrivateKey = keyFactory.generatePrivate(
                    new PKCS8EncodedKeySpec(decodePem(privateKeyPem)));
            PublicKey loadedPublicKey = keyFactory.generatePublic(
                    new X509EncodedKeySpec(decodePem(publicKeyPem)));
            validateKeyPair(loadedPrivateKey, loadedPublicKey);
            return new KeyPair(loadedPublicKey, loadedPrivateKey);
        } catch (GeneralSecurityException | IllegalArgumentException exception) {
            throw new IllegalStateException(GENERIC_CONFIGURATION_ERROR, exception);
        }
    }

    private KeyPair generateEphemeralKeyPair() throws GeneralSecurityException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(MINIMUM_KEY_SIZE_BITS);
        KeyPair keyPair = generator.generateKeyPair();
        validateKeyPair(keyPair.getPrivate(), keyPair.getPublic());
        return keyPair;
    }

    private void validateKeyPair(PrivateKey privateKey, PublicKey publicKey) {
        if (!(privateKey instanceof RSAPrivateKey rsaPrivateKey)
                || !(publicKey instanceof RSAPublicKey rsaPublicKey)
                || rsaPrivateKey.getModulus().bitLength() < MINIMUM_KEY_SIZE_BITS
                || rsaPublicKey.getModulus().bitLength() < MINIMUM_KEY_SIZE_BITS
                || !rsaPrivateKey.getModulus().equals(rsaPublicKey.getModulus())) {
            throw new IllegalArgumentException(GENERIC_CONFIGURATION_ERROR);
        }
    }

    private byte[] decodePem(String value) {
        String normalized = value.replace("\\n", "\n").trim();
        normalized = normalized
                .replaceAll("-----BEGIN [^-]+-----", "")
                .replaceAll("-----END [^-]+-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(normalized);
    }

    private String decodeUtf8(byte[] value) throws CharacterCodingException {
        return StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT)
                .decode(ByteBuffer.wrap(value))
                .toString();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
