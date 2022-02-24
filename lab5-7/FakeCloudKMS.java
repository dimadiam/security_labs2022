package lab56;

import javax.crypto.Cipher;

public class FakeCloudKMS {
    // return AES key
    public static byte[] getKEKkey() {
        String kek = "4f75a4ff8e4b488d1893ceeb766415e9b47965442cab72d456ddb37f735c26bb";
        byte[] kekBytes = Storage.hexStringToBytes(kek);
        return kekBytes;
    }

    // return IV (initialization vector)
    public static byte[] getKEKiv() {
        String IV = "115e95c18ef4cc715e1d9b55";
        byte[] kekIV = Storage.hexStringToBytes(IV);
        return kekIV;
    }
}
