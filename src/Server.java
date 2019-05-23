import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class Server {
    @Override
    public String toString() {
        return "Server{" +
                "port=" + port +
                ", balance=" + balance +
                ", server=" + server +
                ", nAcceptedRequest=" + nAcceptedRequest +
                '}';
    }

    private int port;
   private int balance;

   ServerSocket server;
   int nAcceptedRequest;

    public Server(int port){
       this.port = port;
    }

    public void go(){

        try {
            server = new ServerSocket(port);
            System.out.println("Starting server on port "+port);
            new Thread(new periodicSave()).start();
        } catch (IOException e) {
            System.out.println("Cannot start server on port "+port);
            e.printStackTrace();
            System.exit(-1);

        }

        while (true){
            try {
                System.out.println("Ready to accept connections...");
                Socket client = server.accept();
                nAcceptedRequest++;
                System.out.println("Accepted connection request n."+nAcceptedRequest+ " from:"+client.getRemoteSocketAddress());

                InnerClientManager cm = new InnerClientManager(client);

                Thread t = new Thread(cm,"Thread"+String.valueOf(nAcceptedRequest));
                t.start();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    public class InnerClientManager implements Runnable {
    Socket assigned_client;
    String name;

    @Override
    public void run() {
        this.name = Thread.currentThread().getName();
        System.out.println("Starting thread ClientManager: "+name);

        try {
            // var br = new BufferedReader(new InputStreamReader(assigned_client.getInputStream()));

            Scanner in = new Scanner(assigned_client.getInputStream());
            PrintWriter out = new PrintWriter(assigned_client.getOutputStream());

            while (in.hasNextLine()){
                String message = in.nextLine();
                System.out.println(name+":Received message: "+message);

                if (message.startsWith("INC")){
                    int incVal = Integer.parseInt(message.substring(4));
                    setBalance(getBalance()+incVal);
                    System.out.println(name+" increasing balance of "+incVal+", new balance:"+getBalance());

                    out.println("OK");
                    out.flush();
                }
                else
                if (message.equals("QUIT")){
                    System.out.println(name+": Terminating ClientManager");
                    assigned_client.close();
                    break;
                }
                else {
                    System.out.println(name+":Not a protocol  message: "+message);
                    // potrebbe anche essere diverso, es, ERROR
                    out.println("OK");
                    out.flush();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InnerClientManager(Socket s){
        this.assigned_client = s;
    }

}


    class periodicSave implements Runnable {
        @Override
        public void run() {
            System.out.println("Periodic Save started...");
            File f = new File("balancelog.txt");
            try {
                FileWriter fw = new FileWriter(f);
                while (true) {
                    Thread.sleep(10000);
                    fw.write(new Date().toString()+" **** balance = "+getBalance());
                    fw.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized  public int getBalance() {
        return balance;
    }

    synchronized public void setBalance(int balance) {
        this.balance = balance;
    }
}
