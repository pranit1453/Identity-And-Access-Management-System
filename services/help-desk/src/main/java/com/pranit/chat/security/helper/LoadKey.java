package com.pranit.chat.security.helper;

import com.pranit.chat.security.exception.KeyExtensionException;
import com.pranit.chat.security.exception.KeyNotLoadedException;
import com.pranit.chat.security.exception.KeyResourceNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class LoadKey {
    private LoadKey() {
    }

    private static void validatePemFile(final String path) {
        if (path == null || path.isBlank())
            throw new KeyResourceNotFoundException("Please provide a valid pem file path");

        if (!path.toLowerCase().endsWith(".pem"))
            throw new KeyExtensionException("Only .pem key files are supported");

    }

    private static String readKeyFromResource(final String pemPath) throws IOException {
        try (final InputStream is = LoadKey.class.getClassLoader().getResourceAsStream(pemPath)) {
            if (is == null) throw new KeyResourceNotFoundException("Resource not found: " + pemPath);
            return new String(is.readAllBytes());
        }
    }

    public static PublicKey loadPublicKey(final String pemPath) {
        validatePemFile(pemPath);
        try {
            final String Key = readKeyFromResource(pemPath)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            final byte[] decoded = Base64.getDecoder().decode(Key);
            final X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new KeyNotLoadedException("Failed to load public key: " + pemPath);
        }
    }
}
