package lab2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DecryptSalsa20 {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("src/lab2/salsa20-ciphertext.txt");

        String ciphertext = null;
        try {
            ciphertext = Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.toString());
            return;
        }
        String[] ciphertextLines = ciphertext.split(System.lineSeparator());
        ArrayList<char[]> ciphertextLinesDecoded = new ArrayList<char[]>();

        for (String line : ciphertextLines) {
            char[] lineChars = stringToHex(line);
            ciphertextLinesDecoded.add(lineChars);
        }

        ArrayList<char[]> xoredCiphertexts = doXorAllCiphertextLines(ciphertextLinesDecoded);

        ArrayList<char[]> opentexts =  decrypt(xoredCiphertexts, "and include ");

        System.out.println("Opentexts:");
        for (int i = 0; i < opentexts.size(); i++) {
            System.out.println("#" + i + " :  " + new String(opentexts.get(i)));
        }

    }

    private static char[] stringToHex(String sHex) {
        int indexBytes = 0;
        char[] bytes = new char[sHex.length()/2];

        int index = 0;
        while (index + 2 <= sHex.length()) {
            String part = sHex.substring(index, index + 2);
            int partInt = Integer.parseInt(part, 16);
            bytes[indexBytes++] = (char)partInt;
            index += 2;
        }
        return bytes;
    }

    private static ArrayList<char[]> doXorAllCiphertextLines(ArrayList<char[]> ciphertexts) {
        ArrayList<char[]> results = new ArrayList<char[]>();

        for (int i = 0; i < ciphertexts.size(); i++) {
            for (int j = i + 1; j < ciphertexts.size(); j++) {
                char[] ciphertext1 = null;
                char[] ciphertext2 = null;
                if (ciphertexts.get(i).length >= ciphertexts.get(j).length) {
                    ciphertext1 = ciphertexts.get(i);
                    ciphertext2 = ciphertexts.get(j);
                } else {
                    ciphertext1 = ciphertexts.get(j);
                    ciphertext2 = ciphertexts.get(i);
                }
                char[] resXOR = new char[ciphertext1.length];
                for (int b = 0; b < ciphertext1.length; b++) {
                    resXOR[b] = (char) (ciphertext1[b] ^ ciphertext2[b % ciphertext2.length]);
                }
                results.add(resXOR);
            }
        }
        return results;
    }

    private static ArrayList<char[]> decrypt(ArrayList<char[]> xoredCiphertexts, String opentext) {
        char[] opentextChars = opentext.toCharArray();
        ArrayList<char[]> decryptedTexts = new ArrayList<char[]>();

        for (char[] ciphertext : xoredCiphertexts) {
            char[] decryptedChars = new char[ciphertext.length];
            for (int i = 0; i < ciphertext.length; i++) {
                decryptedChars[i] = (char)(ciphertext[i] ^ opentextChars[i % opentextChars.length]);
            }
            decryptedTexts.add(decryptedChars);
        }
        return decryptedTexts;
    }
}