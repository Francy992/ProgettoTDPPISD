import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args){

        String address = "localhost";
        int port = 8888;
        if (args.length == 2){
            try{
                address = args[0];
                port = Integer.parseInt(args[1]);
            }
            catch(Exception e){
                System.out.println("Please specify addresss and port as arguments!");
                System.exit(-1);
            }
        }

        Socket socket = null;
        try {
            socket = new Socket(address,port);
        } catch (IOException e) {
            System.out.println("Unreachable host!");
            e.printStackTrace();
            System.exit(-1);
        }

        Scanner scanner = new Scanner(System.in);

        while (true){
            String message;
            System.out.print("Insert message for server->");
            message = scanner.nextLine();

            System.out.println("Sending message \""+message+"\" to "+socket.getRemoteSocketAddress());

            try {
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                Scanner in = new Scanner(socket.getInputStream());

                pw.println(message);
                pw.flush();

                if (message.equals("QUIT")){
                    System.out.println("Quitting client...");
                    socket.close();
                    break;
                }

                // se siamo qui, non era quit

                String answer = in.nextLine();

                if (answer.equals("OK")){
                    System.out.println("Confirmed command "+message);
                }
                // da qui in poi, eventualmente gestire altre possibili risposte
                else{
                    System.out.println("Unexpected answer from server: "+answer);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }






    }
}
