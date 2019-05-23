import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientManager implements Runnable {
    Socket assigned_client;
    // soluzione n.2
    //Server my_gen;
    @Override
    public void run() {
        System.out.println("Starting thread ClientManager");

        try {
            var br = new BufferedReader(new InputStreamReader(assigned_client.getInputStream()));
            boolean cont = true;
            while (cont){
                String message = br.readLine();
                System.out.println("Received message: "+message);

                if (message.equalsIgnoreCase("quit")){
                    System.out.println("Terminating ClientManager");
                    cont = false;
                    assigned_client.close();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientManager(Socket s){
        this.assigned_client = s;
    }

    // soluzione n.2 (al balance)
    /*
    public ClientManager(Socket s, Server genitore) {
        this.assigned_client = s;
        this.my_gen = genitore;
    };

     */


}
