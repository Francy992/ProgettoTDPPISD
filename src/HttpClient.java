import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpClient {

    private final String USER_AGENT = "Mozilla/5.0";

    // HTTP GET request
    public ResponseModel sendGet(int cityId) throws Exception {
        String keyApi = "d7e9e670c8248069375e01fe693a0268";
        //System.out.println("Prima di chiamare, city id vale: " + cityId);
        String url = "http://api.openweathermap.org/data/2.5/forecast?id="+ cityId + "&APPID="+keyApi+"&units=Metric";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        if(responseCode != 200) //an error occured
            return null;

        //System.out.println("Response Code : " + responseCode);

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(con.getInputStream()));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();

        Gson gson = new Gson();
        ResponseModel weathers = gson.fromJson(String.valueOf(response), ResponseModel.class);
        return weathers;
    }
}