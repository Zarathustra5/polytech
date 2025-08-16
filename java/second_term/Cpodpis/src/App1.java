import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public class App1 {
    static final String keyPairAlgorithm = "DSA";
    static final int keyPairAlgorithmSize = 1024;
    static final String signatureAlgorithm = "SHA256withDSA";
    static final String fileMessageName = "message.txt";
    static final String signatureFileName = "digital_signature_2";
    static final String publicKeyFileName = "public.key";

    // Функция сохранения цп в keystore
    public static void savePublicKey(PublicKey publicKey) {
        try {
            X509EncodedKeySpec x509ks = new X509EncodedKeySpec(
                    publicKey.getEncoded());
            FileOutputStream fos = new FileOutputStream(publicKeyFileName);
            fos.write(x509ks.getEncoded());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {

        try {
            // Генерация пары ключей
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyPairAlgorithm);
            keyPairGenerator.initialize(keyPairAlgorithmSize);

            KeyPair pair = keyPairGenerator.generateKeyPair();
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();

            Signature signature = Signature.getInstance(signatureAlgorithm);
            signature.initSign(privateKey);
            byte[] messageBytes = Files.readAllBytes(Paths.get(fileMessageName));
            signature.update(messageBytes);
            byte[] digitalSignature = signature.sign();
            Files.write(Paths.get(signatureFileName), digitalSignature);

            // Сохранение SecretKey в KeyStore (JCEKS)
            savePublicKey(publicKey);
            System.out.println("Ключ сохранен успешно");
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }
}
