import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.*;


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
                System.out.println("Received message: " + message);
                if (message.equalsIgnoreCase("quit")){
                    System.out.println("Terminating ClientManager");
                    cont = false;
                    assigned_client.close();
                }
                //Check that message is a good city.
                HttpClient getCall = new HttpClient();
                try{
                    ResponseModel model;
                    CityList.City city = cityList.getCityHashMap(message);
                    if (city == null){
                        pw.println("Errore nell'estrazione dell'id, città non presente");
                    }
                    else{
                        //Before call i check that weather do not already in the object, in this case if it is in the object from of less ten minute i return this and do not call api
                        Calendar calendar = new GregorianCalendar();
                        if(city.timeStampeResponseModel != null && Duration.between(calendar.toInstant(), city.timeStampeResponseModel.toInstant()).getSeconds() < (600)){
                            pw.println("Qui devo ritornare quello che ho già dentro");
                        }
                        else{
                            model =  getCall.sendGet(city.id);
                            //update responseModel in hashmap
                            cityList.setResponseModeHashMap(model);
                            pw.println("Model vale: "+ model.list[0].weather[0].description + ", la temperatura è: " + model.list[0].main.temp);
                        }
                    }
                    pw.flush();
                }catch(Exception e){
                    pw.println("Errore durante la chiamata api, probabilmente è stato superato il limite massimo di chiamate.");
                    pw.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
