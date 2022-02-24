package lab56;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class Storage {
    private final String usersDbPath = "src/lab56/db/users.db";
    private final String usersPeperDataPath = "src/lab56/configs/peper.data";
    private final byte[] usersPeperData;

    private final String personalInfoDbPath = "src/lab56/db/personalInfo.db";


    public Storage() throws IOException {
        File f = new File(usersDbPath);
        if (f.exists()) {
            System.out.println("Storage file 'users.db' is already exist");
        } else {
            System.out.println("Storage file 'users.db' does not exist. Creating it.");
            boolean success = f.createNewFile();
            if (success) {
                System.out.println("DB file 'users.db' was created");
            } else {
                System.out.println("Could not create DB file 'users.db'");
            }
        }
        f = new File(personalInfoDbPath);
        if (f.exists()) {
            System.out.println("Storage file 'personalInfo.db' is already exist");
        } else {
            System.out.println("Storage file 'personalInfo.db' does not exist. Creating it");
            boolean success = f.createNewFile();
            if (success) {
                System.out.println("DB file 'personalInfo.db' was created");
            } else {
                System.out.println("Could not create DB file 'personalInfo.db'");
            }
        }

        f = new File(usersPeperDataPath);
        if (f.exists()) {
            System.out.println("Read peper file");
            Path path = Paths.get(usersPeperDataPath);
            String sPeper = Files.readString(path, StandardCharsets.UTF_8);
            sPeper = sPeper.strip();
            usersPeperData = hexStringToBytes(sPeper);
        } else {
            System.out.println("Create peper file");
            usersPeperData = argon2id.generatePeper();
            BufferedWriter writer = new BufferedWriter(new FileWriter(usersPeperDataPath));
            writer.write(bytesToHexString(usersPeperData));
            writer.close();
        }
        System.out.println("Peper: " + bytesToHexString(usersPeperData));
    }


    /*
     **************************************************************************************************
     *  For lab5
     */
    public void storeNewUser(String username, String password) throws IOException {
        assert usersPeperData != null;

        byte[] salt = argon2id.generateSalt();
        String sSalt = bytesToHexString(salt);
        byte[] hash = argon2id.hash(password, salt, usersPeperData);
        String sHash = bytesToHexString(hash);
        System.out.println("User: " + username + " salt: " + sSalt + " hash: " + sHash);

        BufferedWriter writer = new BufferedWriter(new FileWriter(usersDbPath, true));
        writer.write(username);
        writer.write(":");
        writer.write(sHash);
        writer.write(":");
        writer.write(sSalt);
        writer.write(System.lineSeparator());
        writer.close();
    }

    public boolean doesUserExist(String username) throws IOException {
        try {
            Path path = Paths.get(usersDbPath);
            String text = Files.readString(path, StandardCharsets.UTF_8);
            String[] lines = text.split(System.lineSeparator());
            for (String line : lines) {
                String[] parts = line.split(":");  // username:password
                if (username.equals(parts[0])) {
                    return true;
                }
            }
            System.out.println("doesUserExist: User '" + username + "' was not found");
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean verifyUserPassword(String username, String password) throws IOException {
        assert usersPeperData != null;

        Path path = Paths.get(usersDbPath);
        String text = Files.readString(path, StandardCharsets.UTF_8);
        String[] lines = text.split(System.lineSeparator());
        for (String line : lines) {
            String[] parts = line.split(":");
            if (username.equals(parts[0])) {
                byte[] salt = hexStringToBytes(parts[2]);
                byte[] hash = argon2id.hash(password, salt, usersPeperData);
                String sHash = bytesToHexString(hash);
                System.out.println("User: " + username + " salt: " + parts[2] + " hash: " + sHash);

                if (sHash.equals(parts[1])) {
                    System.out.println("Password is correct for user " + username);
                    return true;
                } else {
                    System.out.println("Password is not correct for user " + username);
                    return false;
                }
            }
        }
        System.out.println("verifyUserPassword: User '" + username + "' was not found");
        return false;
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder hexBuilder = new StringBuilder();

        for (byte b : bytes) {
            hexBuilder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return hexBuilder.toString();
    }

    public static byte[] hexStringToBytes(String sHex) {
        int indexBytes = 0;
        byte[] bytes = new byte[sHex.length()/2];

        int index = 0;
        while (index + 2 <= sHex.length()) {
            String part = sHex.substring(index, index + 2);
            int partInt = Integer.parseInt(part, 16);
            bytes[indexBytes++] = (byte)partInt;
            index += 2;
        }
        return bytes;
    }


    /*
     **************************************************************************************************
     *  For lab6
     */
    public void storeUserData(String username, String phone, String address) throws IOException {
        System.out.println("storeUserData: user=" + username);
        System.out.println("storeUserData: phone=" + phone);
        System.out.println("storeUserData: address=" + address);

        byte[] DEK = AES_GCM_256.generateKey();
        byte[] IV = AES_GCM_256.generateIV();

        byte[] phoneCT = AES_GCM_256.encrypt(phone.getBytes(), DEK, IV);
        byte[] addressCT = AES_GCM_256.encrypt(address.getBytes(), DEK, IV);
        byte[] dekCT = AES_GCM_256.encrypt(DEK, FakeCloudKMS.getKEKkey(), FakeCloudKMS.getKEKiv());
        byte[] ivCT = AES_GCM_256.encrypt(IV, FakeCloudKMS.getKEKkey(), FakeCloudKMS.getKEKiv());

        BufferedWriter writer = new BufferedWriter(new FileWriter(personalInfoDbPath));
        Path path = Paths.get(personalInfoDbPath);
        String text = Files.readString(path, StandardCharsets.UTF_8);
        String[] lines = text.split(System.lineSeparator());
        String prefix = username + ":";
        for (String line : lines) {
            if (line.startsWith(prefix)) {
                System.out.println("Found personal info. Delete it");
            } else if (line.isEmpty()) {
                System.out.println("Skip empty line");
            } else {
                writer.write(line);
                writer.write(System.lineSeparator());
            }
        }

        // add new personal information
        writer.write(username);
        writer.write(":");
        writer.write(bytesToHexString(phoneCT));
        writer.write(":");
        writer.write(bytesToHexString(addressCT));
        writer.write(":");
        writer.write(bytesToHexString(dekCT));
        writer.write(":");
        writer.write(bytesToHexString(ivCT));
        writer.write(System.lineSeparator());
        writer.close();
    }

    public UserPrivateData getUserData(String username) throws IOException {
        UserPrivateData upd = new UserPrivateData();
        upd.username = username;

        Path path = Paths.get(personalInfoDbPath);
        String text = Files.readString(path, StandardCharsets.UTF_8);
        String[] lines = text.split(System.lineSeparator());
        for (String line : lines) {
            String[] parts = line.split(":");
            if (username.equals(parts[0])) {
                byte[] phoneCT = hexStringToBytes(parts[1]);
                byte[] addressCT = hexStringToBytes(parts[2]);
                byte[] dekCT = hexStringToBytes(parts[3]);
                byte[] ivCT = hexStringToBytes(parts[4]);

                // decrypt DEK and IV
                byte[] DEK = AES_GCM_256.decrypt(dekCT, FakeCloudKMS.getKEKkey(), FakeCloudKMS.getKEKiv());
                byte[] IV = AES_GCM_256.decrypt(ivCT, FakeCloudKMS.getKEKkey(), FakeCloudKMS.getKEKiv());

                // decrypt user personal data
                byte[] phoneBytes = AES_GCM_256.decrypt(phoneCT, DEK, IV);
                byte[] addressBytes = AES_GCM_256.decrypt(addressCT, DEK, IV);
                upd.phone = new String(phoneBytes);
                upd.address = new String(addressBytes);
                System.out.println("Found data for User: " + username);

            }
        }
        System.out.println("No any data for User: " + username);
        return upd;
    }

}
