package com.grupo1.gimnasio.gimnasio_backend.services.impl;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cifrado de contraseñas con hash + salt (PBKDF2-HMAC-SHA256, incluido en el JDK).
 * Las contraseñas NUNCA se guardan en texto plano. Formato guardado: "salt:hash" en Base64.
 * En el Entregable 6 se puede sustituir por BCrypt de Spring Security.
 */
final class HashContrasenas {

    private static final int ITERACIONES = 120_000;
    private static final int LARGO_BITS = 256;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private HashContrasenas() {
    }

    static String cifrar(String contrasena) {
        byte[] salt = new byte[16];
        ALEATORIO.nextBytes(salt);
        byte[] hash = derivar(contrasena, salt);
        Base64.Encoder b64 = Base64.getEncoder();
        return b64.encodeToString(salt) + ":" + b64.encodeToString(hash);
    }

    static boolean coincide(String contrasena, String guardado) {
        String[] partes = guardado.split(":");
        byte[] salt = Base64.getDecoder().decode(partes[0]);
        byte[] esperado = Base64.getDecoder().decode(partes[1]);
        // Comparación en tiempo constante para no filtrar información por el tiempo de respuesta
        return MessageDigest.isEqual(esperado, derivar(contrasena, salt));
    }

    private static byte[] derivar(String contrasena, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(contrasena.toCharArray(), salt, ITERACIONES, LARGO_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo cifrar la contraseña", e);
        } finally {
            spec.clearPassword();
        }
    }
}
