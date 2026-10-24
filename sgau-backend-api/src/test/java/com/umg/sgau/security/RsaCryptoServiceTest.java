package com.umg.sgau.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.umg.sgau.auth.exception.InvalidEncryptedPasswordException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import org.junit.jupiter.api.Test;

class RsaCryptoServiceTest {

    private static final OAEPParameterSpec OAEP_SHA256 = new OAEPParameterSpec(
            "SHA-256",
            "MGF1",
            MGF1ParameterSpec.SHA256,
            PSource.PSpecified.DEFAULT);

    @Test
    void cargaParPkcs8YDescifraRsaOaepSha256() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();

        RsaCryptoService service = new RsaCryptoService(
                pem("PRIVATE KEY", keyPair.getPrivate().getEncoded()).replace("\n", "\\n"),
                pem("PUBLIC KEY", keyPair.getPublic().getEncoded()).replace("\n", "\\n"),
                false);

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic(), OAEP_SHA256);
        String encrypted = Base64.getEncoder().encodeToString(
                cipher.doFinal("Secreto123".getBytes(StandardCharsets.UTF_8)));

        assertEquals("Secreto123", service.decryptPassword(encrypted));
        assertEquals(
                Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()),
                service.getPublicKeyBase64());
    }

    @Test
    void noGeneraClaveEphemeralSinConfiguracionExplicita() {
        assertThrows(
                IllegalStateException.class,
                () -> new RsaCryptoService("", "", false));
    }

    @Test
    void generaClaveEphemeralSoloCuandoSeSolicita() throws Exception {
        RsaCryptoService service = new RsaCryptoService("", "", true);

        assertEquals(2048, decodePublicKey(service.getPublicKeyBase64())
                .getModulus().bitLength());
    }

    @Test
    void rechazaParDeClavesQueNoCorresponde() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair privateKeyPair = generator.generateKeyPair();
        KeyPair publicKeyPair = generator.generateKeyPair();

        assertThrows(
                IllegalStateException.class,
                () -> new RsaCryptoService(
                        pem("PRIVATE KEY", privateKeyPair.getPrivate().getEncoded()),
                        pem("PUBLIC KEY", publicKeyPair.getPublic().getEncoded()),
                        false));
    }

    @Test
    void rechazaCifradoConMgf1Sha1() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        RsaCryptoService service = new RsaCryptoService(
                pem("PRIVATE KEY", keyPair.getPrivate().getEncoded()),
                pem("PUBLIC KEY", keyPair.getPublic().getEncoded()),
                false);

        OAEPParameterSpec oaepSha1 = new OAEPParameterSpec(
                "SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic(), oaepSha1);
        String encrypted = Base64.getEncoder().encodeToString(
                cipher.doFinal("Secreto123".getBytes(StandardCharsets.UTF_8)));

        assertThrows(
                InvalidEncryptedPasswordException.class,
                () -> service.decryptPassword(encrypted));
    }

    private RSAPublicKey decodePublicKey(String value) throws Exception {
        return (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(value)));
    }

    private String pem(String type, byte[] encodedKey) {
        return "-----BEGIN " + type + "-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.US_ASCII))
                        .encodeToString(encodedKey)
                + "\n-----END " + type + "-----";
    }
}
