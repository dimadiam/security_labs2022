package lab4;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class Part2Decode {
    public static void main(String[] args) throws IOException {
        System.out.println("read and decode not-my-weakHashes.csv");
        BufferedWriter writer = new BufferedWriter(new FileWriter("src/lab4/not-my-weakHashes-decoded.csv"));

        Path path = Paths.get("src/lab4/not-my-weakHashes.csv");

        String text = Files.readString(path, StandardCharsets.UTF_8);
        String[] lines = text.split(System.lineSeparator());
        for (String line : lines) {
            line = line.strip();
            if (line.isEmpty()) {
                continue;
            }

            byte[] decodedLine = Base64.getDecoder().decode(line);
            String hexLine = bytesToHexString(decodedLine);
            writer.write(hexLine);
            writer.write(System.lineSeparator());
        }
        writer.close();


        System.out.println("read and decode not-my-strongHashes.csv");
        writer = new BufferedWriter(new FileWriter("src/lab4/not-my-strongHashes-decoded.csv"));

        path = Paths.get("src/lab4/not-my-strongHashes.csv");

        text = Files.readString(path, StandardCharsets.UTF_8);
        lines = text.split(System.lineSeparator());
        for (String line : lines) {
            line = line.strip();
            if (line.isEmpty()) {
                continue;
            }

            String[] lineParts = line.split(" ");
            if (lineParts.length != 4) {
                System.out.println("Invalid line: " + line);
                continue;
            }
            String hash = lineParts[1];
            String salt = lineParts[3];
            byte[] decodedHash = Base64.getDecoder().decode(hash);
            byte[] decodedSalt = Base64.getDecoder().decode(salt);
            String hexHash = bytesToHexString(decodedHash);
            String hexSalt = bytesToHexString(decodedSalt);
            writer.write(hexHash);
            writer.write(":");
            writer.write(hexSalt);
            writer.write(System.lineSeparator());
        }
        writer.close();
    }


    private static final StringBuilder hexBuilder = new StringBuilder();

    private static String bytesToHexString(byte[] bytes) {
        hexBuilder.setLength(0);

        for (byte b : bytes) {
            hexBuilder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return hexBuilder.toString();
    }
}
