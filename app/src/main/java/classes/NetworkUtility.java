package classes;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.http.Url;

import static android.content.ContentValues.TAG;

public class NetworkUtility {
    private final static String WEATHER_BASE_URL =
            "http://dataservice.accuweather.com/forecasts/v1/daily/5day/306633";

    private final static String API_KEY = "sjOqhYuiJPlNFPEsu5uch2N6CmkCcqGV";

    private final static String PARAM_API_KEY = "apikey";

    private final static String PARAM_METRIC = "metric";
    private final static String METRIC_VALUE = "false";

    public static URL buildUrlForWeather(){
        Uri builtUri = Uri.parse(WEATHER_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY,API_KEY)
                .appendQueryParameter(PARAM_METRIC, METRIC_VALUE)
                .build();

        URL url = null;
        try{
            url = new URL(builtUri.toString());
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try{
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if(hasInput){
                return scanner.next();
            }else{
                return null;
            }
        }finally {
            urlConnection.disconnect();
        }
    }
}
