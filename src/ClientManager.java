import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;



public class ClientManager implements Runnable {
    Socket assigned_client;
    CityList cityList;
    public ClientManager(Socket client, CityList cityList){
        assigned_client = client;
        this.cityList = cityList;
    }
    @Override
    public void run() {
        System.out.println("Starting thread ClientManager");

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(assigned_client.getInputStream()));
            PrintWriter pw = new PrintWriter(assigned_client.getOutputStream());
            boolean cont = true;
            while (cont){
                String message = br.readLine();
                CommandModel messageReceived = parseMessageToModel(message);
                if (messageReceived != null && messageReceived.choose == 4){
                    System.out.println("Terminating ClientManager");
                    cont = false;
                    assigned_client.close();
                }
                else{
                    HandleClientRequest hcr = new HandleClientRequest(cityList);
                    String messageForClient = hcr.HandleRequest(messageReceived.choose, message);//choose is 1/2/3
                    pw.println(messageForClient);
                    pw.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CommandModel parseMessageToModel(String message){
        Gson gson = new Gson();
        return gson.fromJson(String.valueOf(message), CommandModel.class);
    }
}
