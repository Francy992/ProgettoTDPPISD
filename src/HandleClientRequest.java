import com.google.gson.Gson;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

public class HandleClientRequest {
    public CityList cityList;
    public HandleClientRequest(CityList cityList){
        this.cityList = cityList;
    }
    public String HandleRequest(int request, String message){
        String messRet = "";
        Gson gson = new Gson();
        try {
            switch(request){
                case 1: //register case
                    System.out.println("Request for Register.");
                    messRet = Register(message);
                    break;
                case 2: //Login case
                    System.out.println("Request for Login.");
                    messRet = Login(message);
                    break;
                case 3://Request city's weather case.
                    System.out.println("Request for Wheather.");
                    messRet = WeatherRequest(message);
                    break;
                default://Return error json
                    return gson.toJson(new FinalResponse(1, true,"Richiesta non gestita."));
            }
        }
        catch(Exception e){

        }
        return messRet;
    }

    private String Register(String message){
        Gson gson = new Gson();
        RegisterModelRequest register =  gson.fromJson(String.valueOf(message), RegisterModelRequest.class);
        //Controllare che tutte e 3 le città esistino
        CityList.City city = cityList.getCityHashMap(register.city1);
        if (city == null) {
            return gson.toJson(new FinalResponse(1, true, "La città numero 1 non è presente nella lista. Registrazione non consentita."));
        }
        city = cityList.getCityHashMap(register.city2);
        if (city == null) {
            return gson.toJson(new FinalResponse(1,true, "La città numero 2 non è presente nella lista. Registrazione non consentita."));
        }
        city = cityList.getCityHashMap(register.city3);
        if (city == null) {
            return gson.toJson(new FinalResponse(1, true,"La città numero 3 non è presente nella lista. Registrazione non consentita."));
        }

        if(UserState.insertNewUser(register.username, register.password, register.city1, register.city2, register.city3))
            //Ritornare registrato correttamente.
            return gson.toJson(new FinalResponse(1, false, "Utente registraro correttamente."));
        else
            return gson.toJson(new FinalResponse(1, true, "Utente già presente."));

    }

    private String Login(String message){
        Gson gson = new Gson();
        //Controllo che username e password esistono. Se esistono recupero le 3 città della persona
        LoginModelRequest login =  gson.fromJson(String.valueOf(message), LoginModelRequest.class);
        String [] userDate = UserState.getUser(login.username);
        if(userDate == null)
            return gson.toJson(new FinalResponse(2, true, "Utente non presente o password non corretta. Riprova."));
        String x = userDate[0];
        if(userDate == null || (!x.equals(login.password)))
            return gson.toJson(new FinalResponse(2, true, "Utente non presente o password non corretta. Riprova."));
        String weatherCity1 = "", weatherCity2 = "", weatherCity3 = "";
        //Recupero il meteo delle 3 città singolarmente e lo ritorno
        weatherCity1 = getWeatherOneShot(userDate[1]);
        weatherCity2 = getWeatherOneShot(userDate[2]);
        weatherCity3 = getWeatherOneShot(userDate[3]);
        return gson.toJson(new FinalResponse(2, false, "Login effettuato con successo", weatherCity1, weatherCity2, weatherCity3));
    }

    private String WeatherRequest(String message){
        HttpClient getCall = new HttpClient();
        try{
            Gson gson = new Gson();
            WeatherModelRequest weather =  gson.fromJson(String.valueOf(message), WeatherModelRequest.class);
            ResponseModel model;
            CityList.City city = cityList.getCityHashMap(weather.city);
            if (city == null){
                return gson.toJson(new FinalResponse(3, true, "Requested city not present in db."));
            }
            else{
                //Before call i check that weather do not already in the object, in this case if it is in the object from of less ten minute i return this and do not call api
                Calendar calendar = new GregorianCalendar();
                if(city.timeStampeResponseModel != null && Duration.between(calendar.toInstant(), city.timeStampeResponseModel.toInstant()).getSeconds() < (600)){
                    model =  city.responseModel;
                }
                else{
                    //get city weather and store in hashmap.
                    model =  getCall.sendGet(city.id);
                    //update responseModel in hashmap
                    cityList.setResponseModeHashMap(model);
                }
                ResponseToClientModel responseToClientModel = prepareResponseToClient(model, 3);
                return gson.toJson(new FinalResponse(3, false, "City found.", responseToClientModel));
            }
        }catch(Exception e){
            Gson gson = new Gson();
            return gson.toJson(new FinalResponse(3, true, "Exception on server."));
        }
    }

    private String getWeatherOneShot(String city){
        CityList.City citta = cityList.getCityHashMap(city);
        if (citta == null){
            return null;
        }
        Calendar calendar = new GregorianCalendar();
        try{
            if(!(citta.timeStampeResponseModel != null && Duration.between(calendar.toInstant(), citta.timeStampeResponseModel.toInstant()).getSeconds() < (600))){
                //get city weather and store in hashmap.
                HttpClient getCall = new HttpClient();
                citta.responseModel =  getCall.sendGet(citta.id);
                //update responseModel in hashmap
                cityList.setResponseModeHashMap(citta.responseModel);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String ret = "";
        ret += "City: " + city + "<br/>";
        ret += "Temp: " + citta.responseModel.list.get(0).main.temp + "°C<br/>";
        ret += "humidity: " + citta.responseModel.list.get(0).main.humidity + "%<br/>";
        ret += "Wind: " + citta.responseModel.list.get(0).wind.speed + " km/h - Direction: "  + degToCompass(citta.responseModel.list.get(0).wind.deg) + "<br/>";
        return ret;
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
    private ResponseToClientModel prepareResponseToClient(ResponseModel responseModel, int typeOfRequest) throws Exception {
        ResponseToClientModel response = new ResponseToClientModel();
        response.city = responseModel.city.name;
        response.error = false;
        switch (typeOfRequest){
            case 3:
                //for(int i = 0; i < responseModel.list.size(); i++){
                response.details = getDetails(responseModel.list);
                break;
            default:
                response.details = null;
                break;
        }
        return response;
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

    public class RegisterModelRequest {
        public int choose;
        public String username;
        public String password;
        public String city1;
        public String city2;
        public String city3;
    }

    public class LoginModelRequest {
        public int choose;
        public String username;
        public String password;
    }

    public class WeatherModelRequest {
        public int choose;
        public String city;
    }

    public class FinalResponse {
        public int choose;
        public boolean error;
        public String messageError;
        public ResponseToClientModel responseAllWeather;
        public String city1;
        public String city2;
        public String city3;

        public FinalResponse(int choose){
            this.choose = choose;
            error = false;
            messageError = "";
            responseAllWeather = null;
            city1 = "";
            city2 = "";
            city3 = "";
        }
        public FinalResponse(int choose, boolean err, String message){
            this.choose = choose;
            error = err;
            messageError = message;
            responseAllWeather = null;
            city1 = "";
            city2 = "";
            city3 = "";
        }
        public FinalResponse(int choose, boolean err, String message, ResponseToClientModel responseAllWeather){
            this.choose = choose;
            error = err;
            messageError = message;
            this.responseAllWeather = responseAllWeather;
            city1 = "";
            city2 = "";
            city3 = "";
        }
        public FinalResponse(int choose, boolean err, String message, String city1, String city2, String city3){
            this.choose = choose;
            error = err;
            messageError = message;
            responseAllWeather = null;
            this.city1 = city1;
            this.city2 = city2;
            this.city3 = city3;
        }
    }

    public static class UserState{
        static Map<String, String[]> userList = new HashMap<String, String[]>();

        public static boolean insertNewUser(String username, String password, String city1, String city2, String city3){
            String [] hmap = new String [] {password, city1, city2, city3};
            String [] userDate = getUser(username);
            if(userDate != null){
                return false; //username già presente
            }
            //Se non è già presente allora inseriamolo
            userList.put(username, hmap);
            return true;
        }

        public static String[] getUser(String username){
            return userList.get(username);
        }
    }
}
