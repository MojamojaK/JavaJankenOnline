package Sample;

import java.net.InetAddress;

public class WhoAmI {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: Sample.WhoAmI <MachineName>");
            System.exit(1);
        }
        InetAddress addr = InetAddress.getByName(args[0]);
        System.out.println(addr);
    }
}
