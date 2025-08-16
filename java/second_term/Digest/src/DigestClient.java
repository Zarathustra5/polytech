import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Scanner;

// Первый и третий участник цепочки
public class DigestClient {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 5002;

    public static void main(String[] args) throws ClassNotFoundException {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {
            while (true) {
                // get the output stream from the socket.
                OutputStream outputStream = socket.getOutputStream();
                // create an object output stream from the output stream so we can send an object through it
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

                BlockPojo blockPojo = new BlockPojo();
                System.out.println("Введите строку для отправки на сервер:");
                blockPojo.message = scanner.nextLine();

                // Вычисляем хэш строки
                System.out.println("Сообщение клиента: " + blockPojo.message);
                blockPojo.hash = hashString(blockPojo.message);
                System.out.println("Хэш клиента: " + blockPojo.hash);

                // Отправляем строку и её хэш на сервер
                System.out.println("Sending messages to Server");
                objectOutputStream.writeObject(blockPojo);

                // Получаем ответ от сервера
                InputStream inputStream = socket.getInputStream();
                System.out.println("new stream");
                // create a DataInputStream so we can read data from it.
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                System.out.println("Reading object");
                BlockPojo prevBlockPojo = (BlockPojo) objectInputStream.readObject();
                String generatedHash = hashString(prevBlockPojo.message);
                System.out.println("Полученная от сервера строка: " + prevBlockPojo.message);
                System.out.println("Полученный от сервера хеш: " + prevBlockPojo.hash);
                System.out.println("Сгенерированный хеш: " + generatedHash);
                if (generatedHash.equals(prevBlockPojo.hash)) {
                    System.out.println("Хэш совпадает");
                } else {
                    System.out.println("Хеш поврежден!");
                }
            }

        } catch (IOException e) {
            System.err.println("Ошибка подключения к серверу: " + e.getMessage());
        }
    }

    // Функция хеширования строки (SHA-256)
    private static String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка хеширования", e);
        }
    }
}