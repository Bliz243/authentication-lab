package app.auth;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

public class SecurePasswordService {

    private static final int SALT_SIZE = 16; // 128 bits
    private static final int PEPPER_SIZE = 8; // 64 bits
    private static final int ITERATIONS = 100000; // Number of iterations for PBKDF2
    private static final int HASH_SIZE = 64; // 512 bits for SHA512
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

    private final byte[] localSecret = "YourLocalSecretHere".getBytes(StandardCharsets.UTF_8);

    private byte[] cryptoHash(String password, byte[] salt, byte[] pepper) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String pepperedPassword = password + new String(pepper, StandardCharsets.UTF_8);
        byte[] saltedSecret = concat(salt, localSecret);

        KeySpec spec = new PBEKeySpec(pepperedPassword.toCharArray(), saltedSecret, ITERATIONS, HASH_SIZE * 8);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);

        return factory.generateSecret(spec).getEncoded();
    }

    public String generateSecurePassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecureRandom random = new SecureRandom();

        byte[] salt = new byte[SALT_SIZE];
        random.nextBytes(salt);

        byte[] pepper = new byte[PEPPER_SIZE];
        random.nextBytes(pepper);

        byte[] hash = cryptoHash(password, salt, pepper);

        return String.join("$", ALGORITHM, String.valueOf(SALT_SIZE), String.valueOf(PEPPER_SIZE),
                String.valueOf(ITERATIONS), String.valueOf(HASH_SIZE),
                Base64.getEncoder().encodeToString(salt), Base64.getEncoder().encodeToString(pepper),
                Base64.getEncoder().encodeToString(hash));
    }

    public boolean verifyPassword(String password, String storedPasswordHash, boolean[] requiresUpdate) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedPasswordHash.split("\\$", 8);
        if (parts.length != 8) {
            throw new IllegalArgumentException("Stored password hash has an invalid format.");
        }

        String storedAlgoName = parts[0];
        int storedSaltSize = Integer.parseInt(parts[1]);
        int storedPepperSize = Integer.parseInt(parts[2]);
        int storedIterations = Integer.parseInt(parts[3]);
        int storedHashSize = Integer.parseInt(parts[4]);

        byte[] salt = Base64.getDecoder().decode(parts[5]);
        byte[] pepper = Base64.getDecoder().decode(parts[6]);
        byte[] storedHash = Base64.getDecoder().decode(parts[7]);

        byte[] testHash = cryptoHash(password, salt, pepper);

        requiresUpdate[0] = !storedAlgoName.equals(ALGORITHM) || storedSaltSize != SALT_SIZE || storedPepperSize != PEPPER_SIZE || storedIterations != ITERATIONS || storedHashSize != HASH_SIZE;

        return Arrays.equals(testHash, storedHash);
    }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}