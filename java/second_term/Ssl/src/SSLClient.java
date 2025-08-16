import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class SSLClient {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 8443;

        // Настройка TrustStore (доверие клиенту к серверу)
        KeyStore ts = KeyStore.getInstance("JKS");
        ts.load(new FileInputStream("clienttruststore.jks"), "123456".toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ts);

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, tmf.getTrustManagers(), null);

        SSLSocketFactory ssf = sc.getSocketFactory();
        SSLSocket socket = (SSLSocket) ssf.createSocket(host, port);

        // 🚀 Выполним SSL Handshake вручную, чтобы потом достать сертификаты
        socket.startHandshake();

        // 📜 Получим сертификаты, выданные сервером
        SSLSession session = socket.getSession();
        System.out.println("SSL-соединение установлено с: " + session.getPeerHost());
        System.out.println("Протокол: " + session.getProtocol());
        System.out.println("Цифра: " + session.getCipherSuite());

        Certificate[] serverCerts = session.getPeerCertificates();
        for (int i = 0; i < serverCerts.length; i++) {
            System.out.println("Сертификат " + (i + 1));
            Certificate cert = serverCerts[i];
            System.out.println(cert.toString());

            if (cert instanceof X509Certificate) {
                X509Certificate x509 = (X509Certificate) cert;
                System.out.println("Subject: " + x509.getSubjectX500Principal());
                System.out.println("Issuer : " + x509.getIssuerX500Principal());
                System.out.println("Срок действия: " + x509.getNotBefore() + " - " + x509.getNotAfter());
            }
        }

        // Отправка сообщения
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println("Сообщение от клиента через SSL с проверкой сертификатов!");

        out.close();
        socket.close();
    }
}
