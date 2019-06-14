import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
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
                CommandModel messageReceived = parseMessageToModel(message);
                if (messageReceived != null && messageReceived.choose == 4){
                    System.out.println("Terminating ClientManager");
                    cont = false;
                    assigned_client.close();
                }
                else{
                    HandleClientRequest hcr = new HandleClientRequest(cityList);
                    String messageForClient = hcr.HandleRequest(messageReceived.choose, message);//choose is 1/2/3
                    Gson gson = new Gson();
                    pw.println(messageForClient);
                    pw.flush();
                }
                /*if(messageReceived != null && messageReceived.choose == 1){
                    Gson gson = new Gson();
                    pw.println(gson.toJson(getStringForJson(false, "Utente registrato correttamente.")));
                    pw.flush();
                }
                //Check that message is a good city.
                HttpClient getCall = new HttpClient();
                try{
                    ResponseModel model;
                    CityList.City city = cityList.getCityHashMap(messageReceived.city);
                    if (city == null){
                        Gson gson = new Gson();
                        pw.println(gson.toJson(getStringForJson(true, "Requested city not present in db.")));
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
                    Gson gson = new Gson();
                    pw.println(gson.toJson(getStringForJson(true, "Eccezione nel server.")));
                    pw.flush();
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String register(String message){
        //Controllare che tutte e 3 le città esistino
        //Se esistono registrare in un file?
        //Ritornare registrato correttamente.
        return "";
    }

    private ErrorModel getStringForJson(boolean errorBool, String message){
        ErrorModel error = new ErrorModel();
        error.error = errorBool;
        error.messageError = message;
        return error;
    }

    private ResponseToClientModel prepareResponseToClient(ResponseModel responseModel, int typeOfRequest) throws Exception {
        ResponseToClientModel response = new ResponseToClientModel();
        response.city = responseModel.city.name;
        response.error = false;
        switch (typeOfRequest){
            case 1:
                //for(int i = 0; i < responseModel.list.size(); i++){
                response.details = getDetails(responseModel.list);
                break;
            default:
                response.details = null;
                break;
        }
        return response;
    }

    private ArrayList<ResponseToClientModel.Details> getDetails(ArrayList<ResponseModel.List> responseModelList)throws Exception{
        //build object for response
        ArrayList<ResponseToClientModel.Details> details = new ArrayList<>();
        ResponseToClientModel.Details  detail = new ResponseToClientModel.Details();
        for(int i = 0; i < responseModelList.size(); i++){
            String day = getDayForDetails(responseModelList.get(i).dt_txt, responseModelList.get(i).dt);
            if(detail.day != null && !detail.day.equalsIgnoreCase(day) && i != 0){
                //devo creare un nuovo detail e l'attuale lo devo aggiungere nell'oggetto details, ho finito le informazioni per quel giorno.
                details.add(detail);
                detail = new ResponseToClientModel.Details();
            }
            detail.date = responseModelList.get(i).dt_txt;
            detail.day = day;
            detail.weatherObjects.add(getWeatherObject(responseModelList.get(i)));
        }
        //last object
        details.add(detail);
        return details;
    }


    private ResponseToClientModel.WeatherObject getWeatherObject(ResponseModel.List responseModelList){
        //build Weatherobject for response.
        ResponseToClientModel.WeatherObject weatherObject = new ResponseToClientModel.WeatherObject();
        weatherObject.imageName = responseModelList.weather.get(0).icon;
        weatherObject.maxTemp =  Math.round(responseModelList.main.temp_max)+ "°C";
        weatherObject.minTemp =  Math.round(responseModelList.main.temp_min)+ "°C";
        weatherObject.temp = Math.round(responseModelList.main.temp) + "°C";
        weatherObject.wind = responseModelList.wind.speed + "Km/h";
        weatherObject.date = responseModelList.dt_txt;
        weatherObject.humidity = responseModelList.main.humidity + "%";
        weatherObject.degree = degToCompass(responseModelList.wind.deg);
        return weatherObject;
    }
    private String degToCompass(double num) {
        double val = Math.floor((num / 22.5) + 0.5);
        String [] arr = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        int valInt = (int)Math.round(val);
        return arr[(valInt % 16)];
    }
    private String getDayForDetails(String data_txt, long timestamp) throws Exception{ // compare timestamp with day date, today, tomorrow or day of week
        //get day from data_txt, get day from timestamp and compare it.
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Date today;
        today = new Date(ts.getTime());

        SimpleDateFormat sd1 = new SimpleDateFormat("dd");
        String dateFromTimestamp = data_txt.substring(8,10);
        if(dateFromTimestamp.equals(sd1.format(today)))
            return "Today";//If day in timestamp and today is some number
        else if(Integer.parseInt(dateFromTimestamp) == (Integer.parseInt(sd1.format(today)) + 1))
            return "Tomorrow";
        else{ //return day of week from timestamp.
            String input_date = data_txt.substring(0,10).replace("-","/");
            SimpleDateFormat format1=new SimpleDateFormat("dd/MM/yyyy");
            Date dt1= null;
            dt1 = format1.parse(input_date);
            DateFormat format2=new SimpleDateFormat("EEEE");
            String finalDay=format2.format(dt1);
            finalDay = finalDay.substring(0,1).toUpperCase() + finalDay.substring(1);
            return (finalDay); // "Day of week"
        }
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
