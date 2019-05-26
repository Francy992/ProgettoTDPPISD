import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
                CommandModel messageReceived = parseMessageToModel(message);
                if (messageReceived.choose == 4){
                    System.out.println("Terminating ClientManager");
                    cont = false;
                    assigned_client.close();
                }
                //Check that message is a good city.
                HttpClient getCall = new HttpClient();
                try{
                    ResponseModel model;
                    CityList.City city = cityList.getCityHashMap(messageReceived.city);
                    if (city == null){
                        pw.println("Errore nell'estrazione dell'id, città non presente");
                    }
                    else{
                        //Before call i check that weather do not already in the object, in this case if it is in the object from of less ten minute i return this and do not call api
                        Calendar calendar = new GregorianCalendar();
                        if(city.timeStampeResponseModel != null && Duration.between(calendar.toInstant(), city.timeStampeResponseModel.toInstant()).getSeconds() < (600)){
                            model =  city.responseModel;
                        }
                        else{
                            model =  getCall.sendGet(city.id);
                            //update responseModel in hashmap
                            cityList.setResponseModeHashMap(model);
                        }
                        //String response = prepareResponse(messageReceived, model);
                        ResponseToClientModel responseComplete = prepareResponseToClient(model, messageReceived.choose);
                        Gson gson = new Gson();
                        pw.println(gson.toJson(responseComplete));
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


    private ResponseToClientModel prepareResponseToClient(ResponseModel responseModel, int typeOfRequest){
        ResponseToClientModel response = new ResponseToClientModel();
        response.city = responseModel.city.name;
        response.error = false;
        switch (typeOfRequest){
            case 1:
                response.details.add(getDetails(responseModel.list.get(0), 0));
                break;
            default:
                response.details = null;
                break;
        }
        return response;
    }

    private ResponseToClientModel.Details getDetails(ResponseModel.List responseModelList, int index){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ResponseToClientModel.Details  detail = new ResponseToClientModel.Details();
        detail.date = responseModelList.dt_txt;
        detail.day = getDayForDetails(responseModelList.dt_txt, responseModelList.dt);
        detail.weatherObjects.add(getWeatherObject(responseModelList));
        return detail;
    }

    private String getDayForDetails(String data_txt, long timestamp){ // compare timestamp with day date
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Date today;
        today = new Date(ts.getTime());

        SimpleDateFormat sd1 = new SimpleDateFormat("dd");
        String provv = sd1.format(today);
        String dateFromTimestamp = data_txt.substring(8,10);
        if(dateFromTimestamp.equals(sd1.format(today)))
            return "Today";//If day in timestamp and today is some number
        else if(Integer.parseInt(dateFromTimestamp) == (Integer.parseInt(sd1.format(today)) + 1))
            return "Tomorrow";
        else{ //return day of week from timestamp.
            GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
            Timestamp time = new Timestamp(timestamp);
            return (new SimpleDateFormat("EEEE")).format(time.getTime()); // "Day of week"
        }
    }

    private ResponseToClientModel.WeatherObject getWeatherObject(ResponseModel.List responseModelList){
        ResponseToClientModel.WeatherObject weatherObject = new ResponseToClientModel.WeatherObject();
        weatherObject.imageName = responseModelList.weather.get(0).icon;
        weatherObject.maxTemp = responseModelList.main.temp_max + "°C";
        weatherObject.minTemp = responseModelList.main.temp_min+ "°C";
        weatherObject.temp = responseModelList.main.temp+ "°C";
        weatherObject.wind = responseModelList.wind.speed + "Km/h";
        return weatherObject;
    }

    private CommandModel parseMessageToModel(String message){
        Gson gson = new Gson();
        return gson.fromJson(String.valueOf(message), CommandModel.class);
    }
/**
    private String prepareResponse(CommandModel model, ResponseModel responseModel){
        String response = "";
        switch(model.choose){
            case 1:
                response = getOneHourFromList(responseModel.list[0], true);
                break;
            case 2:
                response += "Tomorrow's wheather:\n";
                for(int i = 0; i<7;i++){
                    response += getOneHourFromList(responseModel.list[i], false);
                }
                break;
            case 3:
                for(int i = 0; i<responseModel.list.length;i++){
                    response += getOneHourFromList(responseModel.list[i], false);
                }
                response += "Finish\n";
                break;
            default:
                break;
        }
        return response;
    }

    private String getOneHourFromList(ResponseModel.List list, boolean now){
        String response = "";
        response += now ? "" : "Prevision for: " + list.dt_txt + "\n";
        response += "Current weather is:" + list.weather[0].description + "\n";
        response += "Current temp about: " + list.main.temp + "°C\n";
        response += "Max temp expected: " + list.main.temp_max + " °C\n";
        response += "Min temp expected: " + list.main.temp_min + "°C\n";
        response += "Humidity : " + list.main.humidity + "\n\n";
        return response;
    }*/

}
