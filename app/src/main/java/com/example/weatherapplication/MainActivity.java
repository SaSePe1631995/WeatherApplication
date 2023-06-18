package com.example.weatherapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private class JSONRequest extends AsyncTask <String, String, String>{

        protected void onPreExecute(){

            super.onPreExecute();
            resultView.setText(R.string.waiting_for_result_russian);
            actionButton.setVisibility(View.INVISIBLE);

        }

        @Override
        protected String doInBackground(String... strings) {

            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            try {

                URL urlAddress = new URL(strings[0]);
                urlConnection = (HttpURLConnection) urlAddress.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line;

                while ((line = bufferedReader.readLine()) != null) stringBuffer.append(line).append("\n");

                return stringBuffer.toString();

            } catch (IOException e) {

                e.printStackTrace();

            } finally {

                if (urlConnection != null) urlConnection.disconnect();
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            return null;

        }

        protected  void onPostExecute ( String result){

            super.onPostExecute(result);
            if (result == null) resultView.setText(R.string.error_incorrect_string_russian);
            else {

                try {

                    JSONObject jsonObject = new JSONObject(result);

                    resultView.setText("Сейчас " + jsonObject.getJSONArray("weather").getJSONObject(0).getString("description") +
                            "\n\nТемпература от " + Math.round(Math.floor(jsonObject.getJSONObject("main").getDouble("temp_min"))) +
                            " до " + Math.round(Math.ceil(jsonObject.getJSONObject("main").getDouble("temp_max"))) +
                            " °C\nОщущается как " + Math.round(jsonObject.getJSONObject("main").getDouble("feels_like")) +
                            " °C\n\n" +
                            "Скорость ветра " +  Math.round(Math.floor(jsonObject.getJSONObject("wind").getDouble("speed"))) +
                            " м/с\n\n" +
                            "Атмосферное давление "+ Math.round(jsonObject.getJSONObject("main").getDouble("pressure")) +
                            " мм\nОтносительная влажность "+ Math.round(jsonObject.getJSONObject("main").getDouble("humidity")) +
                            " %");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            actionButton.setVisibility(View.VISIBLE);

        }

    }

    private EditText cityName;
    private TextView resultView;
    private  Button actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityName);
        actionButton = findViewById(R.id.actionButton);
        resultView = findViewById(R.id.resultsView);

        actionButton.setOnClickListener(v -> {

            if (cityName.getText().toString().trim().equals("")) resultView.setText(R.string.error_empty_string_russian);
            else new JSONRequest().execute("https://api.openweathermap.org/data/2.5/weather?q="+cityName.getText().toString().trim()+"&appid=69dec8d6167e28db082402cc92ffc895&units=metric&lang=ru");

        });

    }

}