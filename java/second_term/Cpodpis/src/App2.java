import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class App2 {
    static final String keyPairAlgorithm = "DSA";
    static final String signatureAlgorithm = "SHA256withDSA";
    static final String fileMessageName = "message.txt";
    static final String signatureFileName = "digital_signature_2";
    static final String publicKeyFileName = "public.key";

    public static void main(String[] args) throws IOException {

        try {
            // Извлекаем ключ из хранилища и проверяем его
            byte[] encodedKey = Files.readAllBytes(Paths.get(publicKeyFileName));

            KeyFactory keyFactory = KeyFactory.getInstance(keyPairAlgorithm, "SUN");
            X509EncodedKeySpec pkSpec = new X509EncodedKeySpec(
                    encodedKey);
            PublicKey publicKey = keyFactory.generatePublic(pkSpec);

            Signature signature = Signature.getInstance(signatureAlgorithm);
            signature.initVerify(publicKey);
            byte[] messageBytes = Files.readAllBytes(Paths.get(fileMessageName));
            signature.update(messageBytes);
            byte[] encryptedMessageHash =
                    Files.readAllBytes(Paths.get(signatureFileName));
            boolean isCorrect = signature.verify(encryptedMessageHash);
            if (isCorrect) {
                System.out.println("Проверка хеша завершена успешно");
            } else {
                System.out.println("Ошибка проверки хеша");
            }
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
