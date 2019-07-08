import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;



public class ClientManager implements Runnable {
    Socket assigned_client;
    CityList cityList;
    int number;
    public ClientManager(Socket client, CityList cityList, int number){
        assigned_client = client;
        this.cityList = cityList;
        this.number = number;
    }
    @Override
    public void run() {
        System.out.println("Starting thread ClientManager");

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(assigned_client.getInputStream()));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(assigned_client.getOutputStream()));
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
        } catch (Exception e) {
            //e.printStackTrace();
        }
        System.out.println("Close ClientManager for Client number: " + this.number);
    }

    private CommandModel parseMessageToModel(String message){
        Gson gson = new Gson();
        return gson.fromJson(String.valueOf(message), CommandModel.class);
    }
}
