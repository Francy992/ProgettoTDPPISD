import java.util.ArrayList;

public class ResponseModel {
    public String code;
    public double message;
    public int cnt;
    public ArrayList<List> list;
    public City city;
    //public String country;

    public class City{
        public int id;
        public String name;
        public String country;
        public coordinate coord;
        public int timezone;
    }
    public class coordinate{
        public double lon;
        public double lat;
        @Override
        public String toString() {
            return "[Longitudine: " + this.lon + ", Latitudine: " + this.lat + "]";
        }
    }
    public class List{
        public long dt; //Datetima
        public MainModel main;
        public ArrayList<Weather> weather;
        public Clouds clouds;
        public Wind wind;
        public Sys sys;
        public String dt_txt;
    }
    public class MainModel{//Temperature.
        public double temp;
        public double temp_min;
        public double temp_max;
        public double pressure;
        public double sea_level;
        public double grnd_level;
        public double humidity;
        public double temp_kf;
    }
    public class Weather{//Condizioni del tempo
        public int id;
        public String main;
        public String description;
        public String icon;
    }
    public class Clouds{//nuvole
        public int all;
    }
    public class Wind{//vento
        public double speed;
        public double deg;//direzione
    }
    public class Sys{ //non specificato
        public String pod;
    }


}
