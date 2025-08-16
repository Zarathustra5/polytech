import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

public class SSLServer {
    public static void main(String[] args) throws Exception {
        int port = 8443;

        // Настройка SSL-контекста
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("serverkeystore.jks"), "123456".toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, "123456".toCharArray());

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(kmf.getKeyManagers(), null, null);

        SSLServerSocketFactory ssf = sc.getServerSocketFactory();
        SSLServerSocket s = (SSLServerSocket) ssf.createServerSocket(port);
        System.out.println("Сервер запущен на порту " + port);

        SSLSocket c = (SSLSocket) s.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
        String msg = in.readLine();
        System.out.println("Получено сообщение: " + msg);

        in.close();
        c.close();
        s.close();
    }
}
