package lab1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecryptTasks {

    public static void main(String[] args) {

        String task1 = "7958401743454e1756174552475256435e59501a5c524e176f786517545e475f5245191772195019175e4317445f58425b531743565c521756174443455e595017d5b7ab5f525b5b58174058455b53d5b7aa175659531b17505e41525917435f52175c524e175e4417d5b7ab5c524ed5b7aa1b174f584517435f5217515e454443175b524343524517d5b7ab5fd5b7aa17405e435f17d5b7ab5cd5b7aa1b17435f5259174f584517d5b7ab52d5b7aa17405e435f17d5b7ab52d5b7aa1b17435f525917d5b7ab5bd5b7aa17405e435f17d5b7ab4ed5b7aa1b1756595317435f5259174f58451759524f4317545f564517d5b7ab5bd5b7aa17405e435f17d5b7ab5cd5b7aa175650565e591b17435f525917d5b7ab58d5b7aa17405e435f17d5b7ab52d5b7aa1756595317445817585919176e5842175a564e17424452175659175e5953524f1758511754585e59545e53525954521b177f565a5a5e595017535e4443565954521b177c56445e445c5e17524f565a5e5956435e58591b17444356435e44435e54565b17435244434417584517405f564352415245175a52435f5853174e5842175152525b174058425b5317445f584017435f52175552444317455244425b4319";
        String task2 = "G0IFOFVMLRAPI1QJbEQDbFEYOFEPJxAfI10JbEMFIUAAKRAfOVIfOFkYOUQFI15ML1kcJFUeYhA4IxAeKVQZL1VMOFgJbFMDIUAAKUgFOElMI1ZMOFgFPxADIlVMO1VMO1kAIBAZP1VMI14ANRAZPEAJPlMNP1VMIFUYOFUePxxMP19MOFgJbFsJNUMcLVMJbFkfbF8CIElMfgZNbGQDbFcJOBAYJFkfbF8CKRAeJVcEOBANOUQDIVEYJVMNIFwVbEkDORAbJVwAbEAeI1INLlwVbF4JKVRMOF9MOUMJbEMDIVVMP18eOBADKhALKV4JOFkPbFEAK18eJUQEIRBEO1gFL1hMO18eJ1UIbEQEKRAOKUMYbFwNP0RMNVUNPhlAbEMFIUUALUQJKBANIl4JLVwFIldMI0JMK0INKFkJIkRMKFUfL1UCOB5MH1UeJV8ZP1wVYBAbPlkYKRAFOBAeJVcEOBACI0dAbEkDORAbJVwAbF4JKVRMJURMOF9MKFUPJUAEKUJMOFgJbF4JNERMI14JbFEfbEcJIFxCbHIJLUJMJV5MIVkCKBxMOFgJPlVLPxACIxAfPFEPKUNCbDoEOEQcPwpDY1QDL0NCK18DK1wJYlMDIR8II1MZIVUCOB8IYwEkFQcoIB1ZJUQ1CAMvE1cHOVUuOkYuCkA4eHMJL3c8JWJffHIfDWIAGEA9Y1UIJURTOUMccUMELUIFIlc=";
        String task3 = "EFFPQLEKVTVPCPYFLMVHQLUEWCNVWFYGHYTCETHQEKLPVMSAKSPVPAPVYWMVHQLUSPQLYWLASLFVWPQLMVHQLUPLRPSQLULQESPBLWPCSVRVWFLHLWFLWPUEWFYOTCMQYSLWOYWYETHQEKLPVMSAKSPVPAPVYWHEPPLUWSGYULEMQTLPPLUGUYOLWDTVSQETHQEKLPVPVSMTLEUPQEPCYAMEWWYTYWDLUULTCYWPQLSEOLSVOHTLUYAPVWLYGDALSSVWDPQLNLCKCLRQEASPVILSLEUMQBQVMQCYAHUYKEKTCASLFPYFLMVHQLUPQLHULIVYASHEUEDUEHQBVTTPQLVWFLRYGMYVWMVFLWMLSPVTTBYUNESESADDLSPVYWCYAMEWPUCPYFVIVFLPQLOLSSEDLVWHEUPSKCPQLWAOKLUYGMQEUEMPLUSVWENLCEWFEHHTCGULXALWMCEWETCSVSPYLEMQYGPQLOMEWCYAGVWFEBECPYASLQVDQLUYUFLUGULXALWMCSPEPVSPVMSBVPQPQVSPCHLYGMVHQLUPQLWLRPOEDVMETBYUFBVTTPENLPYPQLWLRPTEKLWZYCKVPTCSTESQPBYMEHVPETCMEHVPETZMEHVPETKTMEHVPETCMEHVPETT";
        String task4 = "UMUPLYRXOYRCKTYYPDYZTOUYDZHYJYUNTOMYTOLTKAOHOKZCMKAVZDYBRORPTHQLSERUOERMKZGQJOIDJUDNDZATUVOTTLMQBOWNMERQTDTUFKZCMTAZMEOJJJOXMERKJHACMTAZATIZOEPPJKIJJNOCFEPLFBUNQHHPPKYYKQAZKTOTIKZNXPGQZQAZKTOTIZYNIUISZIAELMKSJOYUYYTHNEIEOESULOXLUEYGBEUGJLHAJTGGOEOSMJHNFJALFBOHOKAGPTIHKNMKTOUUUMUQUDATUEIRBKYUQTWKJKZNLDRZBLTJJJIDJYSULJARKHKUKBISBLTOJRATIOITHYULFBITOVHRZIAXFDRNIORLZEYUUJGEBEYLNMYCZDITKUXSJEJCFEUGJJOTQEZNORPNUDPNQIAYPEDYPDYTJAIGJYUZBLTJJYYNTMSEJYFNKHOTJARNLHHRXDUPZIALZEDUYAOSBBITKKYLXKZNQEYKKZTOKHWCOLKURTXSKKAGZEPLSYHTMKRKJIIQZDTNHDYXMEIRMROGJYUMHMDNZIOTQEKURTXSKKAGZEPLSYHTMKRKJIIQZDTNROAUYLOTIMDQJYQXZDPUMYMYPYRQNYFNUYUJJEBEOMDNIYUOHYYYJHAOQDRKKZRRJEPCFNRKJUHSJOIRQYDZBKZURKDNNEOYBTKYPEJCMKOAJORKTKJLFIOQHYPNBTAVZEUOBTKKBOWSBKOSKZUOZIHQSLIJJMSURHYZJJZUKOAYKNIYKKZNHMITBTRKBOPNUYPNTTPOKKZNKKZNLKZCFNYTKKQNUYGQJKZNXYDNJYYMEZRJJJOXMERKJVOSJIOSIQAGTZYNZIOYSMOHQDTHMEDWJKIULNOTBCALFBJNTOGSJKZNEEYYKUIXLEUNLNHNMYUOMWHHOOQNUYGQJKZLZJZLOLATSEHQKTAYPYRZJYDNQDTHBTKYKYFGJRRUFEWNTHAXFAHHODUPZMXUMKXUFEOTIMUNQIHGPAACFKATIKIZBTOTIKZNKKZNLORUKMLLFBUUQKZNLEOHIEOHEDRHXOTLMIRKLEAHUYXCZYTGUYXCZYTIUYXCZYTCVJOEBKOHE";
        System.out.println("Task 3 - Decrypting...");
        DecryptTask3.decryptTask3(task3);

    }


    public static String decryptCaesarWithXOR(String cipherText, int key) {
        StringBuilder textBuilder = new StringBuilder();

        for (char ch : cipherText.toCharArray()) {
            textBuilder.append((char)(ch ^ key));
        }
        return textBuilder.toString();
    }

    public static boolean containsHumanReadableChars(String s, double error) {
        int amountNonHumanChars = 0;
        for (char c : s.toCharArray()) {

            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
                    || c == ' ' || c == '\n' || c == '\r' || c == ',' || c == '.' || c == '-') {
                continue;
            } else {
                amountNonHumanChars++;
            }
        }
        if ((double)amountNonHumanChars / (double)s.length() > error) {
            return false;
        }
        return true;
    }

    public static void decryptTask1(String cipherTextTask1) throws IOException {
        StringBuilder decodedTask1Builder = new StringBuilder();

        int index = 0;
        while (index + 2 <= cipherTextTask1.length()) {
            String part = cipherTextTask1.substring(index, index + 2);
            int partInt = Integer.parseInt(part, 16);
            decodedTask1Builder.append((char)partInt);
            index += 2;
        }

        String decodedCipherText = decodedTask1Builder.toString();

        BufferedWriter writer = new BufferedWriter(new FileWriter("src/lab1/task1-decrypted.txt"));

        for (int key = 1; key < 256; key++) {
            String plainText = decryptCaesarWithXOR(decodedCipherText, key);
            if (containsHumanReadableChars(plainText, 0.2)) {
                System.out.println("key = " + key);
                System.out.println("text = " + plainText);
                writer.write(plainText);
            }
        }
        writer.close();

    }

    public static String decryptCaesarWithXorRepeatingKey(String cipherText, String key) {
        StringBuilder textBuilder = new StringBuilder();

        int keyLen = key.length();
        int keyIndex = 0;

        for (char ch : cipherText.toCharArray()) {
            char plainChar = (char)(ch ^ key.charAt(keyIndex % keyLen));
            textBuilder.append(plainChar);
            keyIndex++;
        }
        return textBuilder.toString();
    }

    public static void generateKeysInner(String currentKey, int leftKeyLen, String possibleSymbols, ArrayList<String> keys) {
        if (leftKeyLen == 0) {
            keys.add(currentKey);
            return;
        }

        leftKeyLen--;
        for (int i = 0; i < possibleSymbols.length(); i++) {
            generateKeysInner(currentKey + possibleSymbols.charAt(i), leftKeyLen, possibleSymbols, keys);
        }
    }

    public static ArrayList<String> generateKeys(String possibleSymbols, int keyLen) {
        ArrayList<String> keys = new ArrayList<String>();
        generateKeysInner("", keyLen, possibleSymbols, keys);
        return keys;
    }

    public static void decryptTask2(String cipherTextTask2) throws IOException {

        byte[] decodedBytes = Base64.getDecoder().decode(cipherTextTask2);
        String decodedTask2 = new String(decodedBytes);
        StringBuilder possibleSymbolsBuilder = new StringBuilder();
        char c = 'a';
        while (c <= 'z') {
            possibleSymbolsBuilder.append(c++);
        }
        c = 'A';
        while (c <= 'Z') {
            possibleSymbolsBuilder.append(c++);
        }
        c = '0';
        while (c <= '9') {
            possibleSymbolsBuilder.append(c++);
        }
        String possibleKeySymbols = possibleSymbolsBuilder.toString();
        System.out.println("Possible key symbols: " + possibleKeySymbols);

        BufferedWriter writer = new BufferedWriter(new FileWriter("src/lab1/task2-decrypted.txt"));

        for (int keylen = 1; keylen < 4; keylen++) {
            System.out.println("Generating keys with len "+ keylen);
            ArrayList<String> keys = generateKeys(possibleKeySymbols, keylen);
            for (String key : keys) {
                String plainText = decryptCaesarWithXorRepeatingKey(decodedTask2, key);
                if (containsHumanReadableChars(plainText, 0.05)) {
                    System.out.printf("key=%s, text=%s\r\n", key, plainText);
                    writer.write("key = " + key);
                    writer.write("\r\n");
                    writer.write(plainText);
                    writer.write("\r\n\r\n");
                }
            }
        }
        writer.close();
    }


}
