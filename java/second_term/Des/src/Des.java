import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;


public class Des {
    static Cipher cipher;
    static final String algorithm = "3DES";

    public static void main(String[] args) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        keyGenerator.init(56); // block size is 128bits
        SecretKey secretKey = keyGenerator.generateKey();

        cipher = Cipher.getInstance(algorithm);

        String plainText = "TheThe real name of Satoshi Nakomoto is ..The real name of Satoshi Nakomoto is ..The real name of Satoshi Nakomoto is ..The real name of Satoshi Nakomoto is ..The real name of Satoshi Nakomoto is ..The real name of Satoshi Nakomoto is ..The real name of Satoshi Nakomoto is ......... real name of Satoshi Nakomoto is ...";
        System.out.println("Первоначальный текст: " + plainText);

        String encryptedText = encrypt(plainText, secretKey);
        System.out.println("Зашифрованный текст: " + encryptedText);

        String decryptedText = decrypt(encryptedText, secretKey);
        System.out.println("Расшифрованный текст: " + decryptedText);
    }

    public static String encrypt(String plainText, SecretKey secretKey)
            throws Exception {
        byte[] plainTextByte = plainText.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByte = cipher.doFinal(plainTextByte);
        Base64.Encoder encoder = Base64.getEncoder();
        String encryptedText = encoder.encodeToString(encryptedByte);
        return encryptedText;
    }

    public static String decrypt(String encryptedText, SecretKey secretKey)
            throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
        String decryptedText = new String(decryptedByte);
        return decryptedText;
    }
}