import java.util.ArrayList;
import java.util.List;

public class ResponseToClientModel {
    public boolean error;
    public String city;
    public ArrayList<Details> details; // one for one different day

    public ResponseToClientModel(){
        this.details = new ArrayList<Details>();
    }

    public static class Details {
        public String day; //Today, Tomorrow, day of week
        public String date;
        public ArrayList<WeatherObject> weatherObjects; //For n timestamp in the list
        public Details(){
            this.weatherObjects = new ArrayList<WeatherObject>();
        }
    }

    public static class WeatherObject {
        public String minTemp;
        public String maxTemp;
        public String temp;
        public String wind;
        public String degree;
        public String humidity;
        public String imageName;
        public String date;
    }
}
