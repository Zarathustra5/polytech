import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class MacGenerate {
    static final String message = "Hello, world!";
    static final String keyPass = "keyPassword";
    static final String keyAllias = "mySecretKey";
    static final String macAlgorithm = "HmacSHA256";
    static final String storeAlgorithm = "JCEKS";
    static final String storePass = "storePassword";
    static final String storeFileName = "keystore.jceks";

    public static byte[] generateMac(SecretKey secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(macAlgorithm);
        mac.init(secretKey);
        return mac.doFinal(message.getBytes());
    }

    public static void saveToKeyStore(SecretKey secretKey) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableEntryException, InvalidKeyException {
        char[] storePassChar = storePass.toCharArray();
        char[] keyPassChar = keyPass.toCharArray();

        // Создание или загрузка KeyStore
        KeyStore keyStore = KeyStore.getInstance(storeAlgorithm); // Используем JCEKS
        keyStore.load(null, storePassChar); // Создание нового KeyStore

        // Сохранение SecretKey в KeyStore
        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
        KeyStore.ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(keyPassChar);
        keyStore.setEntry(keyAllias, secretKeyEntry, protectionParameter);

        // Сохранение KeyStore в файл
        try (FileOutputStream fos = new FileOutputStream(storeFileName)) {
            keyStore.store(fos, storePassChar);
        }
        System.out.println("SecretKey saved to "+storeFileName);
    }

    public static void main(String[] args) {

        try {
            // Генерация SecretKey
            KeyGenerator keyGen = KeyGenerator.getInstance(macAlgorithm);
            SecretKey secretKey = keyGen.generateKey();

            // Создание MAC
            byte[] macBytes = generateMac(secretKey);
            System.out.println("MAC: " + bytesToHex(macBytes));

            // Сохранение SecretKey в KeyStore (JCEKS)
            saveToKeyStore(secretKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException | KeyStoreException | CertificateException | IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            throw new RuntimeException(e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
