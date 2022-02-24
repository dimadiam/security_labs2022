package lab56;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/*
 * https://www.javainterviewpoint.com/java-aes-256-gcm-encryption-and-decryption/
 */

class AES_GCM_256 {
    public static final int AES_KEY_SIZE = 256;
    public static final int GCM_IV_LENGTH = 12;
    public static final int GCM_TAG_LENGTH = 16;

    /*
     * "main" just generate new pair key and IV
     */
    public static void main(String[] args) throws Exception
    {
        // Generate Key
        byte[] key = generateKey();

        // Generate IV (initialization vector)
        byte[] IV = generateIV();

        System.out.println("AES IV : " + Storage.bytesToHexString(IV));
        System.out.println("AES KEY: " + Storage.bytesToHexString(key));
    }

    public static byte[] generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(AES_KEY_SIZE);
            SecretKey key = keyGenerator.generateKey();
            return key.getEncoded();
        }
        catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            throw new RuntimeException("AES.generateKey failed");
        }
    }

    public static byte[] generateIV() {
        byte[] IV = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);
        return IV;
    }

    public static byte[] encrypt(byte[] plaintext, byte[] key, byte[] IV) {
        try {
            // Get Cipher Instance
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            // Create SecretKeySpec
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

            // Create GCMParameterSpec
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);

            // Initialize Cipher for ENCRYPT_MODE
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);

            // Perform Encryption
            byte[] cipherText = cipher.doFinal(plaintext);

            return cipherText;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AES.encrypt failed");
        }
    }

    public static byte[] decrypt(byte[] cipherText, byte[] key, byte[] IV) {
        try {
            // Get Cipher Instance
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            // Create SecretKeySpec
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

            // Create GCMParameterSpec
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);

            // Initialize Cipher for DECRYPT_MODE
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

            // Perform Decryption
            byte[] decryptedText = cipher.doFinal(cipherText);

            return decryptedText;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AES.decrypt failed");
        }
    }
}
