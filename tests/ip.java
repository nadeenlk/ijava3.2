package tests;

import java.net.InetSocketAddress;
import java.net.Socket;

public class ip {
    public static void main(String[] args) throws Exception {
        Socket s = new Socket();
        System.out.println(s);
        s.connect(new InetSocketAddress("checkip.amazonaws.com", 80), 5000);
        System.out.println(s);
        String d = "GET / HTTP/1.0\r\nHost: checkip.amazonaws.com\r\n\r\n";
        System.out.print(d);
        s.getOutputStream().write(d.getBytes());
        System.out.println(new String(s.getInputStream().readAllBytes()));
        s.close();
    }
}
