import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SymmetricEncryption {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION_ECB = "AES/ECB/PKCS5Padding";
    private static final String TRANSFORMATION_CBC = "AES/CBC/PKCS5Padding";
    private static final String TRANSFORMATION_CFB = "AES/CFB/PKCS5Padding";
    private static final String TRANSFORMATION_OFB = "AES/OFB/PKCS5Padding";

    public static void main(String[] args) {
        try {
            // Создаем файл с открытым текстом
            String originalText = "Это тестовый текст для шифрованияЭто тестовый текст для шифрованияЭто тестовый текст для шифрованияЭто тестовый текст для шифрованияЭто тестовый текст для шифрованияЭто тестовый текст для шифрованияЭто тестовый текст для шифрованияЭто тестовый текст для шифрованияЭто тестовый текст для шифрованияЭто тестовый текст для шифрованияЭто тестовый текст для шифрования";
            writeToFile("original.txt", originalText);

            // Генерируем ключ
            SecretKey key = generateKey();

            // Шифруем в разных режимах
            encryptAndDecrypt(TRANSFORMATION_ECB, key, null, "ECB");
            encryptAndDecrypt(TRANSFORMATION_CBC, key, generateIV(), "CBC");
            encryptAndDecrypt(TRANSFORMATION_CFB, key, generateIV(), "CFB");
            encryptAndDecrypt(TRANSFORMATION_OFB, key, generateIV(), "OFB");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(256); // 256 бит для AES
        return keyGen.generateKey();
    }

    private static IvParameterSpec generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private static void encryptAndDecrypt(String transformation, SecretKey key, IvParameterSpec iv, String mode)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException {

        // Читаем исходный текст
        String originalText = readFromFile("original.txt");

        // Шифруем
        Cipher cipher = Cipher.getInstance(transformation);
        if (iv != null) {
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        }

        byte[] encryptedBytes = cipher.doFinal(originalText.getBytes());
        String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
        writeToFile("encrypted_" + mode + ".txt", encryptedText);

        // Сохраняем ключ и IV
        saveKeyAndIV(key, iv, mode);

        // Дешифруем
        cipher = Cipher.getInstance(transformation);
        if (iv != null) {
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key);
        }

        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        String decryptedText = new String(decryptedBytes);
        writeToFile("decrypted_" + mode + ".txt", decryptedText);

        // Проверяем результат
        System.out.println("Режим " + mode + ":");
        System.out.println("Исходный текст: " + originalText);
        System.out.println("Зашифрованный текст: " + encryptedText);
        System.out.println("Расшифрованный текст: " + decryptedText);
        System.out.println("Совпадение: " + originalText.equals(decryptedText));
        System.out.println("----------------------------------------");
    }

    private static void writeToFile(String filename, String content) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
        }
    }

    private static String readFromFile(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }

    private static void saveKeyAndIV(SecretKey key, IvParameterSpec iv, String mode) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("key_" + mode + ".dat"))) {
            // Сохраняем ключ
            oos.writeObject(key);
            // Сохраняем IV как массив байтов
            if (iv != null) {
                oos.writeObject(iv.getIV());
            }
        }
    }

}