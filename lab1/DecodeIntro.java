package lab1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class DecodeIntro {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("src/lab1/intro-binary.txt");

        String taskContent = null;
        try {
            taskContent = Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.toString());
            return;
        }

        StringBuilder decodedTaskBuilder = new StringBuilder();

        int index = 0;
        while (index + 8 <= taskContent.length()) {
            String part = taskContent.substring(index, index + 8);
            int partInt = Integer.parseInt(part, 2);
            decodedTaskBuilder.append((char)partInt);
            index += 8;
        }

        if (index < taskContent.length()) {
            String part = taskContent.substring(index);
            int partInt = Integer.parseInt(part, 2);
            decodedTaskBuilder.append((char)partInt);
        }

        String decodedIntro = decodedTaskBuilder.toString();
        System.out.println(decodedIntro);
        BufferedWriter writer = new BufferedWriter(new FileWriter("src/lab1/intro-decoded-base64.txt"));
        writer.write(decodedIntro);
        writer.close();
        byte[] decodedBytes = Base64.getDecoder().decode(decodedIntro);
        decodedIntro = new String(decodedBytes);
        System.out.println();
        System.out.println(decodedIntro);
        writer = new BufferedWriter(new FileWriter("src/lab1/intro-decoded.txt"));
        writer.write(decodedIntro);
        writer.close();
    }

}