import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Scanner;

// Второй участник цепочки
public class DigestServer {
    private static final int PORT = 5002;

    public static void main(String[] args) throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен, ожидаем подключение...");
            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    System.out.println("Клиент подключился");

                    InputStream inputStream = socket.getInputStream();
                    // create a DataInputStream so we can read data from it.
                    OutputStream outputStream = socket.getOutputStream();
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

                    BlockPojo prevBlockPojo = (BlockPojo) objectInputStream.readObject();
                    System.out.println("Полученная от клиента строка: " + prevBlockPojo.message);

                    // Вычисляем хэш полученной строки
                    String calculatedHash = hashString(prevBlockPojo.message);

                    // Сравниваем хэши
                    if (calculatedHash.equals(prevBlockPojo.hash)) {
                        System.out.println("Хэш совпадает");
                        BlockPojo blockPojo = new BlockPojo();
                        blockPojo.prevBlockPojo = prevBlockPojo;
                        System.out.println("Введите строку для отправки на клиент:");
                        blockPojo.message = scanner.nextLine();

                        // Вычисляем хэш строки
                        System.out.println("Сообщение сервера: " + blockPojo.message);
                        blockPojo.hash = hashString(blockPojo.message);
                        System.out.println("Хэш сервера: " + blockPojo.hash);
                        blockPojo.hash = blockPojo.hash + "broken";
                        System.out.println("Повреждаем хэш сервера: " + blockPojo.hash);

                        // Отправляем блок клиенту
                        System.out.println("Sending messages Client");
                        objectOutputStream.writeObject(blockPojo);
                    } else {
                        out.println("ERROR: Хэш не совпадает");
                    }
                } catch (IOException e) {
                    System.out.println("Ошибка при обработке клиента: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка запуска сервера: " + e.getMessage());
        }
    }

    // Функция для хеширования строки с использованием SHA-256
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