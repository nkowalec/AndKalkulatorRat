package com.nicotec.kr.kalkulatorrat;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class StartForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_form);
        final EditText procBaza = (EditText)findViewById(R.id.ProcBaza);
        final EditText procSum = (EditText)findViewById(R.id.ProcSum);
        final EditText tv = (EditText) findViewById(R.id.editText);

        procBaza.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                procSum.setText(Integer.parseInt(procBaza.getText().toString()) + Integer.parseInt(tv.getText().toString()));
            }
        });
        Button btn = (Button)findViewById(R.id.DownBtn);

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();

        if(info == null || !info.isConnected())
        {
            Toast.makeText(this, "Najpierw połącz się z siecią", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Najpierw nawiąż połączenie z internetem");
            builder.setTitle("Kalkulator Rat");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setCancelable(false);
            builder.create().show();

        }else
        {
            btn.callOnClick();
        }


    }

    public void OnDownBtnClick(View view){
        try {
            EditText tv = (EditText) findViewById(R.id.editText);
            tv.setText("???");
            String dane = new GetSite().execute("http://wibor3m.pl").get();
            Document html = Jsoup.parse(dane);
            Elements elems = html.getElementsByClass("Value");
            Element elem = elems.get(0);
            String wartosc;
            wartosc = elem.text();

            tv.setText(wartosc.trim().replace(" ", "").replace("\r", "").replace("\n", "").replace("%", ""));

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void OnWynikBtnClick(View view)
    {

    }

    protected class GetSite extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute(){

        }

        @Override
        protected void onPostExecute(String result){

        }

        @Override
        public String doInBackground(String... _url){
            URL url = null;
            StringBuilder sb = new StringBuilder();
            try
            {
                url = new URL(_url[0]);

                URLConnection conn = url.openConnection();

                InputStream is = conn.getInputStream();
                InputStreamReader iread = new InputStreamReader(is);
                BufferedReader reader = new BufferedReader(iread);
                String linia;
                while ((linia = reader.readLine()) != null){
                    sb.append(linia);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }
    }
}
