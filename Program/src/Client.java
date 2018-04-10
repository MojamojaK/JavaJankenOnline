import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client{
    public static void main(String[] args)
	throws IOException{
	InetAddress addr=
	    InetAddress.getByName("localhost");
	if(args.length>0){
	    addr=InetAddress.getByName(args[0]);
	}
	System.out.println("addr = " +addr);
	Socket socket=
	    new Socket(addr, 8080);
	try{
	    System.out.println("socket = " + socket);
	    BufferedReader in =
		 new BufferedReader(
			   new InputStreamReader(
						 socket.getInputStream()));
		PrintWriter out=
		    new PrintWriter(
				    new BufferedWriter(
						       new OutputStreamWriter(
									      socket.getOutputStream())), true);
    while(true){
      Scanner sc=new Scanner(System.in);
      String hand = sc.nextLine();          //何を出すかを入力
      out.println(hand);
      String str=in.readLine();
      System.out.println(str);              //勝敗を出力
      if(str.equals("lose") || str.equals("champion")){       //loseが送られてきたら終わり
        break;
      }
    }

		out.println("END");
	}finally{
	    System.out.println("closing..");
	    socket.close();
	}
    }
}
