package Sample;

import java.io.*;
import java.net.*;

// ikaros:  DESKTOP-5T6K2UA         10.24.89.239
// joey:    Joeys-MacBook-Pro.local 10.24.85.80
// michael: MacBook-Pro-5.local     10.24.80.218

public class JabberClient {
    public static void main(String[] args) throws IOException {
        InetAddress addr = InetAddress.getByName("localhost");
        if (args.length > 0) {
            addr = InetAddress.getByName(args[0]);
        }
        System.out.println("addr = " + addr);
        Socket socket = new Socket(addr, JabberServer.PORT);
        try {
            System.out.println("socket = " + socket);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())
            ), true);
            for (int i = 0; i < 10; i++) {
                out.println("tintin " + i);
                String str = in.readLine();
                System.out.println(str);
            }
            out.println("END");
        } finally {
            System.out.println("closing...");
            socket.close();
        }
    }
}
