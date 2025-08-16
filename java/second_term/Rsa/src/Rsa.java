import javax.crypto.Cipher;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Rsa {

    public static String encryptDataRsa(String plainData, PublicKey publicKey) throws Exception {
        Cipher inputCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        inputCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.getEncoder().encodeToString(inputCipher.doFinal(plainData.getBytes()));
    }

    public static String decryptDataRsa(String encryptedDataBase64, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(encryptedDataBase64));
        return new String(bytes);
    }

    // Функция генерации пары ключей
    public static KeyPair generateRsaKeyPair(int rsaKeySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(rsaKeySize);
        return keyPairGenerator.genKeyPair();
    }
    public static RSAPrivateKey base64StringToPrivateKey(String base64PrivateKey) throws Exception {
        byte [] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(spec);
    }

    public static RSAPublicKey base64StringToPublicKey(String base64PublicKey) throws Exception {
        byte [] keyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(x509EncodedKeySpec);
    }

    public static void main(String[] args) throws IOException {

        try {
            int rsaKeySize = 4096;
            KeyPair keyPair = generateRsaKeyPair(rsaKeySize);

            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            //Конвертируем в base64
            String base64PublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String base64PrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

            String plainSecretData = "Текст для тестирования шифра ..";

            // Шифруем
            RSAPublicKey rsaPublicKey = base64StringToPublicKey(base64PublicKey);
            String encryptedData = encryptDataRsa(plainSecretData, rsaPublicKey);

            // Расшифровываем
            RSAPrivateKey rsaPrivateKey = base64StringToPrivateKey(base64PrivateKey);
            String decryptedData = decryptDataRsa(encryptedData, rsaPrivateKey);


            System.out.println("Зашифрованный текст: " + plainSecretData);
            System.out.println("Расшифрованный текст: " + decryptedData);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
