import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

public class MacVerifier {
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

    public static SecretKey loadSecretKey() throws KeyStoreException, FileNotFoundException, UnrecoverableEntryException, NoSuchAlgorithmException {
        char[] storePassChar = storePass.toCharArray();
        char[] keyPassChar = keyPass.toCharArray();

        // Загрузка SecretKey из KeyStore
        KeyStore loadedKeyStore = KeyStore.getInstance(storeAlgorithm);

        try (FileInputStream fis = new FileInputStream(storeFileName)) {
            loadedKeyStore.load(fis, storePassChar);
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException(e);
        }

        // Извлечение SecretKey из KeyStore
        KeyStore.ProtectionParameter keyProtection = new KeyStore.PasswordProtection(keyPassChar);
        KeyStore.SecretKeyEntry loadedSecretKeyEntry = (KeyStore.SecretKeyEntry) loadedKeyStore.getEntry(keyAllias, keyProtection);
        return loadedSecretKeyEntry.getSecretKey();
    }

    public static void main(String[] args) {
        try {
            SecretKey loadedSecretKey = loadSecretKey();
            // Проверка MAC с использованием загруженного ключа
            Mac loadedMac = Mac.getInstance(macAlgorithm);
            loadedMac.init(loadedSecretKey);
            byte[] loadedMacBytes = loadedMac.doFinal(message.getBytes());
            System.out.println("Loaded MAC: " + bytesToHex(loadedMacBytes));
            byte[] macBytes = generateMac(loadedSecretKey);
            System.out.println("Generated MAC: " + bytesToHex(macBytes));

            // Сравнение оригинального и загруженного MAC
            if (MessageDigest.isEqual(macBytes, loadedMacBytes)) {
                System.out.println("MAC verification successful!");
            } else {
                System.out.println("MAC verification failed!");
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | KeyStoreException | FileNotFoundException |
                 UnrecoverableEntryException e) {
            e.printStackTrace();
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
