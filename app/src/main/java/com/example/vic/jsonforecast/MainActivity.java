package com.example.vic.jsonforecast;


import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lvTomorrow,lvAfterTomorrow;
    public static String LOG_TAG = "JSON_result";

    ArrayList<HashMap<String, String>> weatherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherList = new ArrayList<>();
        lvTomorrow = (ListView) findViewById(R.id.lvTomorrow);

        new GetDatosWeather().execute();
    }

    private class GetDatosWeather extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(MainActivity.this, "Json Data is downloading", Toast.LENGTH_LONG).show();


        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            //url de ciudad elegido:
           // String url = "http://api.openweathermap.org/data/2.5/weather?q=Vitoria-Gasteiz,es&appid=e25c9e1eb33eefc821749053b8257ae8&units=metric";
             String url = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Vitoria-Gasteiz,%20es&mode=json&appid=e25c9e1eb33eefc821749053b8257ae8&units=metric&cnt=2";

            //datos recibidos:
            String jsonStr = sh.makeServiceCall(url);
            // выводим целиком полученную json-строку
            // Log.d(LOG_TAG, jsonStr);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null){
                try {
                    JSONObject jsonDataGeneral = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray list = jsonDataGeneral.getJSONArray("list");

                    JSONObject listTomorrow=list.getJSONObject(0);
                    JSONObject listAfterTomorrow=list.getJSONObject(1);

                    JSONObject tempTomorrow=listTomorrow.getJSONObject("temp");
                    String tempDayTomorrow=tempTomorrow.getString("day");

                    JSONArray weatherTomorrow=listTomorrow.getJSONArray("weather");

                    JSONObject weatherOfTomorrow=weatherTomorrow.getJSONObject(0);
              //      JSONObject weatherIdTomorrow=weatherTomorrow.getJSONArray(0);

                   String descriptionTomorrow = weatherOfTomorrow.getString("description");
                    String weatherIdTomorrow = weatherOfTomorrow.getString("id");

                    JSONObject city = jsonDataGeneral.getJSONObject("city");
                    String  name=city.getString("name");

                    String cod=jsonDataGeneral.getString("cod");

                   String pressureTomorrow=listTomorrow.getString("pressure");


                    HashMap<String, String> dataOut = new HashMap<>();

                    dataOut.put("name",name);
                    dataOut.put("cod",cod);
                    dataOut.put("pressure",pressureTomorrow);
                    dataOut.put("temp",tempDayTomorrow);
                    dataOut.put("weatherId",weatherIdTomorrow);

                    weatherList.add(dataOut);



                }catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
            else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;

        }



        protected void onPostExecute(Void result) {
            //   protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ListAdapter adapter = new SimpleAdapter(MainActivity.this, weatherList,
                    R.layout.list_item_tomorrow, new String[]{"name", "temp","cod","weatherId","pressure"},
                    new int[]{R.id.name,R.id.temp, R.id.cod,R.id.weatherId, R.id.pressure});
            lvTomorrow.setAdapter(adapter);
        }




    }

}
