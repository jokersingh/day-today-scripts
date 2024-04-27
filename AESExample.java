package org.example.fidility;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AESExample {

    public static void main(String[] args) {
        try {
            String originalString = "Hello, World! This is a secret message.";
            String password = "MySecretPassword";

            // Generate a random key and IV

            byte[] publicKey = generatePublicKey(password);
            //byte[] salt = joinByteArray(key, publicKey);

            // Encrypt the string
            String encryptedString = encrypt(originalString, password);
            //1Yx33+GkktzXacq5iUq9Qbtvg4SN1fBNujrSqjG+DFXyarrb7EUk/bpz3PYykGOI

            System.out.println("Encrypted string: " + encryptedString);

            // Decrypt the encrypted string
            String decryptedString = decrypt(encryptedString, password);

            System.out.println("Decrypted string: " + decryptedString);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] hashPassword(String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            digest.update(salt);
            byte[] hashedPassword = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return hashedPassword;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encrypt(String strToEncrypt, String password) throws Exception {
        byte[] key = generateRandomKey();
        byte[] iv = generateRandomIV();
        byte[] hashedKey = hashPassword(password, key);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(hashedKey, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(strToEncrypt.getBytes());
        byte[] finalEncrypted = joinByteArray(key, iv, encryptedBytes);
        return Base64.getEncoder().encodeToString(finalEncrypted);
    }



    public static String decrypt(String strToDecrypt, String password) throws Exception {
        byte[] finalBytes = Base64.getDecoder().decode(strToDecrypt);
        ByteBuffer bb = ByteBuffer.wrap(finalBytes);
        byte[] salt = new byte[16];
        byte[] nonce = new byte[16];
        byte[] actualText = new byte[finalBytes.length-32];
        bb.get(salt, 0, salt.length);
        bb.get(nonce, 0, nonce.length);
        bb.get(actualText, 0, actualText.length);
        byte[] hashedKey = hashPassword(password, salt);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(hashedKey, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(nonce);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decryptedBytes = cipher.doFinal(actualText);
        return new String(decryptedBytes);
    }


    public static byte[] generatePublicKey(String publicKey) {
        byte[] bytes = publicKey.getBytes();
        byte[] key = Arrays.copyOf(bytes, 16);
        return key;
    }

    public static byte[] generateRandomKey() {
        byte[] key = new byte[16];
        new SecureRandom().nextBytes(key);
        return key;
    }

    public static byte[] generateRandomIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public static byte[] joinByteArray(byte[] byte1, byte[] byte2, byte[] byte3) {

        return ByteBuffer.allocate(byte1.length + byte2.length+byte3.length)
                .put(byte1)
                .put(byte2)
                .put(byte3)
                .array();

    }
}


