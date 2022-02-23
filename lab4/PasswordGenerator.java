package lab4;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.security.SecureRandom;

public class PasswordGenerator {
    public static void main(String[] args) throws IOException {
        init();

        BufferedWriter md5Writer = new BufferedWriter(new FileWriter("src/lab4/weakHashes.csv"));
        md5Writer.write("hash");
        md5Writer.write(System.lineSeparator());
        BufferedWriter sha1Writer = new BufferedWriter(new FileWriter("src/lab4/strongHashes.csv"));
        sha1Writer.write("hash,salt");
        sha1Writer.write(System.lineSeparator());

        int amountPasswords = 100000;
        String password;
        int number;
        for (int i = 0; i < amountPasswords; i++) {
            number = generateRandomNumber(1, 100);

            // 1 - 10    - 10%
            // 10 - 90   - 80 %
            // 90 - 95   - 5%
            // 95 - 100  - 5%

            if (number < 10) { // 10%
                password = generatePasswordFromTop100();
            } else if (number < 90) { // 80%
                password = generatePasswordFromTop1M();
            } else if (number < 95) { // 5%
                password = generateHumanRandomPassword();
            } else { // other 5%
                password = generateStrongPassword();
            }
            String md5Str = getMD5Hash(password);
            byte[] salt = generateSHA1Salt();
            String saltStr = bytesToHexString(salt);
            String sha1Str = getSHA1Hash(password, salt);
            System.out.print(password);
            System.out.print("  md5: " + md5Str);
            System.out.print("  sha1: " + sha1Str);
            System.out.println("  (salt: " + saltStr + ")");

            md5Writer.write(md5Str);
            md5Writer.write(System.lineSeparator());
            sha1Writer.write(sha1Str);
            sha1Writer.write(",");
            sha1Writer.write(saltStr);
            sha1Writer.write(System.lineSeparator());
        }

        md5Writer.close();
        sha1Writer.close();
    }

    private static MessageDigest mdMD5;
    private static MessageDigest mdSHA1;
    private static SecureRandom srSHA1;
    private static final StringBuilder hexBuilder = new StringBuilder();

    private static String bytesToHexString(byte[] bytes) {
        hexBuilder.setLength(0);

        for (byte b : bytes) {
            hexBuilder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return hexBuilder.toString();
    }

    private static String getMD5Hash(String password) {
        mdMD5.reset();
        byte[] digest = mdMD5.digest(password.getBytes());
        return bytesToHexString(digest);
    }

    private static String getSHA1Hash(String password, byte[] salt) {
        mdSHA1.reset();
        mdSHA1.update(salt);
        byte[] digest = mdSHA1.digest(password.getBytes());
        return bytesToHexString(digest);
    }

    private static byte[] generateSHA1Salt() {
        byte[] salt = new byte[16];
        srSHA1.nextBytes(salt);
        return salt;
    }


    private static int generateRandomNumber(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private static void init() {
        readFileWithData("src/lab4/englishWords.txt", englishWords);
        readFileWithData("src/lab4/top100passwords.txt", top100Passwords);
        // read top 1M common passwords. Source: https://github.com/danielmiessler/SecLists/tree/master/Passwords
        readFileWithData("src/lab4/top1Mpasswords.txt", top1MPasswords);

        try {
            mdMD5 = MessageDigest.getInstance("MD5");
            mdSHA1 = MessageDigest.getInstance("SHA-1");
            srSHA1 = SecureRandom.getInstance("SHA1PRNG");
            srSHA1.setSeed(System.currentTimeMillis());
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Could not create MessageDigest: " + ex);
            ex.printStackTrace();
            throw new RuntimeException("Could not create MessageDigest");
        }
    }

    private static void readFileWithData(String pathStr, ArrayList<String> container) {
        Path path = Paths.get(pathStr);
        try {
            String text = Files.readString(path, StandardCharsets.UTF_8);
            String[] lines = text.split(System.lineSeparator());
            for (String line : lines) {
                line = line.strip();
                if (!line.isEmpty()) {
                    container.add(line);
                }
            }
        } catch (IOException ex) {
            System.out.println("Could not read " + pathStr);
            ex.printStackTrace();
        }
    }

    private static final ArrayList<String> top100Passwords = new ArrayList<>();

    private static String generatePasswordFromTop100() {
        int index = generateRandomNumber(0, top100Passwords.size() - 1);
        return top100Passwords.get(index);
    }

    private static final ArrayList<String> top1MPasswords = new ArrayList<>();

    private static String generatePasswordFromTop1M() {
        int index = generateRandomNumber(0, top1MPasswords.size() - 1);
        return top1MPasswords.get(index);
    }

    private static final StringBuilder passwordBuilder = new StringBuilder();
    private static final String alphabetLowerCase = "abcdefghijklmnopqrstuvwxyz";
    private static final String alphabetUpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String numbers = "0123456789";
    private static final String specialChars = "!#()&@%[]{}_-+*?<>|";

    private static final ArrayList<String> englishWords = new ArrayList<>();

    private static String generateHumanRandomPassword() {
        passwordBuilder.setLength(0);

        int amountWords = englishWords.size();

        while (passwordBuilder.length() < 6) {
            String word = englishWords.get(generateRandomNumber(0, amountWords - 1));

            int index = generateRandomNumber(0, word.length() - 1);
            word = word.replace(word.charAt(index), Character.toUpperCase(word.charAt(index)));

            if (generateRandomNumber(0, 1) > 0) {
                word = word.replace('o', '0');
                word = word.replace('a', '@');
                word = word.replace('l', '1');
                word = word.replace('s', '$');
            }
            passwordBuilder.append(word);

            int numberCount = generateRandomNumber(1, 3); // from 1 to 3 numbers
            while (numberCount-- > 0) {
                passwordBuilder.append(numbers.charAt(generateRandomNumber(0, numbers.length() - 1)));
            }
        }
        return passwordBuilder.toString();
    }

    private static String generateStrongPassword() {
        passwordBuilder.setLength(0);
        int len = generateRandomNumber(8, 12);

        while (passwordBuilder.length() < len) {
            int uppercaseLetterCount = generateRandomNumber(0, 1);
            while (uppercaseLetterCount-- > 0) {
                passwordBuilder.append(alphabetUpperCase.charAt(generateRandomNumber(0, alphabetUpperCase.length() - 1)));
            }

            int lowercaseLetterCount = generateRandomNumber(2, 4);
            while (lowercaseLetterCount-- > 0) {
                passwordBuilder.append(alphabetLowerCase.charAt(generateRandomNumber(0, alphabetLowerCase.length() - 1)));
            }
            uppercaseLetterCount = generateRandomNumber(0, 1);
            while (uppercaseLetterCount-- > 0) {
                passwordBuilder.append(alphabetUpperCase.charAt(generateRandomNumber(0, alphabetUpperCase.length() - 1)));
            }

            int numberCount = generateRandomNumber(0, 2);
            while (numberCount-- > 0) {
                passwordBuilder.append(numbers.charAt(generateRandomNumber(0, numbers.length() - 1)));
            }
            uppercaseLetterCount = generateRandomNumber(0, 1);
            while (uppercaseLetterCount-- > 0) {
                passwordBuilder.append(alphabetUpperCase.charAt(generateRandomNumber(0, alphabetUpperCase.length() - 1)));
            }

            int specialCount = generateRandomNumber(0, 1);
            while (specialCount-- > 0) {
                passwordBuilder.append(specialChars.charAt(generateRandomNumber(0, specialChars.length() - 1)));
            }
        }
        passwordBuilder.setLength(len);
        return passwordBuilder.toString();
    }
}
