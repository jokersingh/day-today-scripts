package org.example.fidility;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AESExample {

    public static void main(String[] args) {
        try {
            String originalString = "Hello, World! This is a secret message.";
            String password = "MySecretPassword";
            AESExample aesExample = new AESExample();
            // Encrypt the string
            String encryptedString = aesExample.encrypt(originalString, password);
            //1Yx33+GkktzXacq5iUq9Qbtvg4SN1fBNujrSqjG+DFXyarrb7EUk/bpz3PYykGOI

            System.out.println("Encrypted string: " + encryptedString);

            // Decrypt the encrypted string
            String decryptedString = aesExample.decrypt(encryptedString, password);

            System.out.println("Decrypted string: " + decryptedString);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String strToEncrypt, String password) {
        try {
            // Generate a random key and IV
            byte[] key = generateRandomKey();
            byte[] iv = generateRandomIV();
            // Hash Key using key and IV
            byte[] hashedKey = hashPassword(password, key);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(hashedKey, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            //Encrypted text
            byte[] encryptedBytes = cipher.doFinal(strToEncrypt.getBytes());
            //Adding Key, IV and Text
            byte[] finalEncrypted = joinByteArray(key, iv, encryptedBytes);
            return Base64.getEncoder().encodeToString(finalEncrypted);
        }catch (Exception e){
            //Log Exception
        }

        return "";
    }

    public String decrypt(String strToDecrypt, String password) {
        try {
            //Decrypting text
            byte[] finalBytes = Base64.getDecoder().decode(strToDecrypt);
            //Breaking text to find key, IV and Actual Encrypted Text
            ByteBuffer bb = ByteBuffer.wrap(finalBytes);
            byte[] key = new byte[16];
            byte[] nonce = new byte[16];
            byte[] actualText = new byte[finalBytes.length - 32];
            bb.get(key, 0, key.length);
            bb.get(nonce, 0, nonce.length);
            bb.get(actualText, 0, actualText.length);
            //Generating Hash Key using public key and salt
            byte[] hashedKey = hashPassword(password, key);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(hashedKey, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(nonce);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decryptedBytes = cipher.doFinal(actualText);
            return new String(decryptedBytes);
        }catch (Exception e){
            //Log Exception
        }

        return "";
    }

    private static byte[] hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(salt);
        return digest.digest(password.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] generateRandomKey() {
        byte[] key = new byte[16];
        new SecureRandom().nextBytes(key);
        return key;
    }

    private static byte[] generateRandomIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    private static byte[] joinByteArray(byte[] byte1, byte[] byte2, byte[] byte3) {

        return ByteBuffer.allocate(byte1.length + byte2.length+byte3.length)
                .put(byte1)
                .put(byte2)
                .put(byte3)
                .array();

    }
}


