import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class CityList {

    private HashMap<String, City> cityList;

    public CityList(){
        this.cityList = new HashMap<>();
        //Inizialize CityList, read json and fill HashMap

        try {
            String path = "listaCitta.json";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            Gson gson = new Gson();
            City [] cities = gson.fromJson(bufferedReader, City[].class);
            for(City city : cities){
                this.cityList.put(city.name.toLowerCase().replace(" ",""), city);
            }
            System.out.println(cityList.get("San francisco".toLowerCase().replace(" ","")));
        } catch (FileNotFoundException e) {
            System.out.println("Error read city list");
            e.printStackTrace();
        }
    }

    public City getCityHashMap(String Name){
        Name = Name.toLowerCase().replace(" ", "");
        return this.cityList.get(Name) != null ? this.cityList.get(Name) : null;
    }

    public void setResponseModeHashMap(ResponseModel res){
        this.cityList.get(res.city.name.replace(" ", "").toLowerCase()).responseModel = res;
        this.cityList.get(res.city.name.replace(" ", "").toLowerCase()).timeStampeResponseModel = new GregorianCalendar();
    }

    public class City {
        public int id;
        public String name;
        public String country;
        public coordinate coord;
        public ResponseModel responseModel;
        public Calendar timeStampeResponseModel;

        @Override
        public String toString() {
            return "Nome città: " + this.name + ", paese: " + this.country + ", id: " + this.id + ", coordinate: " + this.coord.toString();
        }

    }

    public class coordinate{
        public double lon;
        public double lat;
        @Override
        public String toString() {
            return "[Longitudine: " + this.lon + ", Latitudine: " + this.lat + "]";
        }
    }
}