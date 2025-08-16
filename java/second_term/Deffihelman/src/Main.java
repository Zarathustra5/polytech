import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class Main {

    public static void main(String[] args) {
        try {
            // 1. Генерация параметров DH
            System.out.println("Генерация параметров DH...");
            AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
            paramGen.init(2048);
            AlgorithmParameters params = paramGen.generateParameters();
            DHParameterSpec dhParamSpec = params.getParameterSpec(DHParameterSpec.class);

            // 2. Алиса генерирует свою пару ключей
            System.out.println("\nАлиса генерирует пару ключей...");
            KeyPairGenerator aliceKeyGen = KeyPairGenerator.getInstance("DH");
            aliceKeyGen.initialize(dhParamSpec);
            KeyPair aliceKeyPair = aliceKeyGen.generateKeyPair();

            // 3. Боб генерирует свою пару ключей
            System.out.println("Боб генерирует пару ключей...");
            KeyPairGenerator bobKeyGen = KeyPairGenerator.getInstance("DH");
            bobKeyGen.initialize(dhParamSpec);
            KeyPair bobKeyPair = bobKeyGen.generateKeyPair();

            // 4. Обмен открытыми ключами с использованием X509EncodedKeySpec и KeyFactory
            System.out.println("\nОбмен открытыми ключами...");

            // Алиса получает открытый ключ Боба в формате X509
            byte[] bobPubKeyEnc = bobKeyPair.getPublic().getEncoded();
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(bobPubKeyEnc);

            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            PublicKey bobPubKey = keyFactory.generatePublic(x509KeySpec);

            // Боб получает открытый ключ Алисы в формате X509
            byte[] alicePubKeyEnc = aliceKeyPair.getPublic().getEncoded();
            x509KeySpec = new X509EncodedKeySpec(alicePubKeyEnc);
            PublicKey alicePubKey = keyFactory.generatePublic(x509KeySpec);

            // 5. Создание общего секретного ключа с использованием KeyAgreement
            System.out.println("\nСоздание общего секретного ключа...");

            // Алиса инициализирует KeyAgreement
            KeyAgreement aliceKeyAgree = KeyAgreement.getInstance("DH");
            aliceKeyAgree.init(aliceKeyPair.getPrivate());
            aliceKeyAgree.doPhase(bobPubKey, true);
            byte[] aliceSharedSecret = aliceKeyAgree.generateSecret();

            // Боб инициализирует KeyAgreement
            KeyAgreement bobKeyAgree = KeyAgreement.getInstance("DH");
            bobKeyAgree.init(bobKeyPair.getPrivate());
            bobKeyAgree.doPhase(alicePubKey, true);
            byte[] bobSharedSecret = bobKeyAgree.generateSecret();

            // Проверка совпадения ключей
            System.out.println("Ключи совпадают: " +
                    MessageDigest.isEqual(aliceSharedSecret, bobSharedSecret));

            // 6. Создание AES ключа для шифрования
            System.out.println("\nСоздание AES ключа...");
            SecretKeySpec aliceAesKey = new SecretKeySpec(aliceSharedSecret, 0, 16, "AES");
            SecretKeySpec bobAesKey = new SecretKeySpec(bobSharedSecret, 0, 16, "AES");

            // 7. Шифрование и расшифрование сообщения
            System.out.println("\nШифрование сообщения...");
            String originalMessage = "Секретное сообщение для Боба";
            System.out.println("Оригинальное сообщение: " + originalMessage);

            // Алиса шифрует сообщение
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, aliceAesKey);
            byte[] encrypted = cipher.doFinal(originalMessage.getBytes());
            System.out.println("Зашифрованное сообщение (Base64): " +
                    Base64.getEncoder().encodeToString(encrypted));

            // Боб расшифровывает сообщение
            cipher.init(Cipher.DECRYPT_MODE, bobAesKey);
            byte[] decrypted = cipher.doFinal(encrypted);
            System.out.println("Расшифрованное сообщение: " + new String(decrypted));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}