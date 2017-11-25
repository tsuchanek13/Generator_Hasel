package com.example.thomas.generator_hasel;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteTransactionListener;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

public class Generator_Hasla extends AppCompatActivity {

    private final String REST_API_PASSWORD = "http://www.sethcardoza.com/api/rest/tools/random_password_generator/length:30";
    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS HASLO(WARTOSC VARCHAR);";
    private SQLiteDatabase bazaDanych;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_generator__hasla);

        bazaDanych = openOrCreateDatabase("haslaDB", MODE_PRIVATE, null);
        bazaDanych.execSQL(CREATE_TABLE);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Button przycisk = (Button) findViewById(R.id.pobierzHaslo_button);
        final TextView oknoTekstu = (TextView) findViewById(R.id.text_okno);


        przycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pobranyTekst = getResponse(REST_API_PASSWORD);
                oknoTekstu.setText(pobranyTekst);
            }
        });

        Button przyciskZapiszDoBazy = findViewById(R.id.zapisz_do_bazy);
        przyciskZapiszDoBazy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tekstZEkranu = oknoTekstu.getText().toString();
                bazaDanych.execSQL("INSERT INTO HASLO VALUES('"+tekstZEkranu+"');");
                oknoTekstu.setText("");
            }
        });
        Button przyciskUsunBaze = findViewById(R.id.usunBaze);
        przyciskUsunBaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bazaDanych.execSQL("DELETE FROM HASLO");
                oknoTekstu.setText("BAZA ZOSTALA WYCZYSZCZONA");
            }
        });

        Button przyciskWyswieltBaze = findViewById(R.id.wyswietlBazeButton);
        przyciskWyswieltBaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oknoTekstu.setText("");
                StringBuilder wynik = new StringBuilder();
                int i = 1;

                Cursor resultSet = bazaDanych.rawQuery("SELECT * from HASLO", null);
                resultSet.moveToFirst();

                if(resultSet.getCount()==0){
                    wynik.append("BAZA JEST PUSTA");
                }
                do {
                    if (!(resultSet.getCount() == 0)) {
                        wynik.append(i + ". ");
                        wynik.append(resultSet.getString(0) + "\n");
                        i++;
                    }
                }while(resultSet.moveToNext());

                oknoTekstu.setText(wynik.toString());


            }
        });
        Button przyciskWyczyscEkran = (Button) findViewById(R.id.wyczysc_ekran);

        przyciskWyczyscEkran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oknoTekstu.setText("");
            }
        });
    }

    public static String getResponse(String url) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            URI uri = new URL(url).toURI();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(uri));

            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
