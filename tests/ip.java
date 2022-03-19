package tests;

import java.net.Socket;

public class ip {
    public static void main(String[] args) throws Exception {
        Socket s = new Socket("checkip.amazonaws.com", 80);
        s.getOutputStream().write("GET / HTTP/1.0\r\nHost: checkip.amazonaws.com\r\n\r\n".getBytes());
        System.out.println(new String(s.getInputStream().readAllBytes()));
        s.close();
    }
}
