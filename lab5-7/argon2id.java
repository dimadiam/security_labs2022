package lab56;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;


// https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html#argon2id

// https://gist.github.com/ensingerphilipp/1b41b0f3650a53172752e5a99c7246be
class argon2id {
    public static byte[] hash(String password, byte[] salt, byte[] pepper) {
        Security.addProvider(new BouncyCastleProvider());
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        Argon2Parameters.Builder builder = new Argon2Parameters.Builder();

        //Choose a hash length - for security reason choose 128 for password hashing (can be lowered to 16 for non-secure applications)
        byte[] hash = new byte[128];

        //Set the number of Iterations each call -> More Iterations = Better Security + more Hashing Time
        // > 3 Iterations recommended
        builder.withIterations(1);

        //Figure out how much memory each call can afford (memory_cost).
        //The RFC recommends 4 GB for backend authentication and 1 GB for frontend authentication.
        //The APIs uses Kibibytes (1024 bytes) as base unit.
        //https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html#argon2id
        builder.withMemoryAsKB(37 * 1024);

        //Choose the Number of CPU-Threads you can afford each call (2 Cores = 4 Threads)
        builder.withParallelism(2);

        //Choose a "salt" which can be stored non-secure or with the password Hash
        builder.withSalt(salt);

        //Choose a Secret "pepper" which has to be stored in a different secure location from the password hashes
        builder.withSecret(pepper);

        //Choose whether you want  Argon2d: 0, Argon2i: 1, Argon2id: 2, Argon2_version_10: 16, Argon2_version_13: 19
        //Argon2i is recommended for password Hashing
        builder.withVersion(2);

        Argon2Parameters parameters = builder.build();

        generator.init(parameters);
        generator.generateBytes(password.toCharArray(), hash);
        return hash;
    }

    public static byte[] generateSalt() {
        //Salt and Pepper Length should be at least 8 Byte, 16 bytes is sufficient for all applications
        byte[] salt = new byte[16];
        try {
            SecureRandom sr = SecureRandom.getInstanceStrong();
            sr.nextBytes(salt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return salt;
    }

    public static byte[] generatePeper() {
        //Salt and Pepper Length should be at least 8 Byte, 16 bytes is sufficient for all applications
        byte[] pepper = new byte[16];
        try {
            SecureRandom sr = SecureRandom.getInstanceStrong();
            sr.nextBytes(pepper);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return pepper;
    }

}
