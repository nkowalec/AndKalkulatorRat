package com.nicotec.kr.kalkulatorrat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class StartForm extends AppCompatActivity {

    EditText procBaza;
    EditText procSum;
    EditText kwotaEdit;
    EditText ileRatEdit;
    EditText dataEdit;
    EditText wynikEdit;
    EditText rataEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_form);
        procBaza = (EditText)findViewById(R.id.ProcBaza);
        procSum = (EditText)findViewById(R.id.ProcSum);
        kwotaEdit = (EditText) findViewById(R.id.editText2);
        final EditText tv = (EditText) findViewById(R.id.editText);
        ileRatEdit = (EditText) findViewById(R.id.IloscRat);
        dataEdit = (EditText) findViewById(R.id.dateLabel);
        wynikEdit = (EditText) findViewById(R.id.WynikEdit);
        rataEdit = (EditText) findViewById(R.id.RataEdit);
    //    dateLabel = (EditText) findViewById(R.id.dateLabel);

        procBaza.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!procBaza.getText().toString().equals("") && !tv.getText().toString().equals(""))
                {
                    Double baza = Double.parseDouble(procBaza.getText().toString().replace(',', '.'));
                    Double wibor = Double.parseDouble(tv.getText().toString().replace(',', '.'));
                    Double sum = baza + wibor;
                    procSum.setText( sum.toString() );
                }
            }
        });

        initDefaultValues();

        tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!procBaza.getText().toString().equals("") && !tv.getText().toString().equals(""))
                {
                    Double baza = Double.parseDouble(procBaza.getText().toString().replace(',', '.'));
                    Double wibor = Double.parseDouble(tv.getText().toString().replace(',', '.'));
                    Double sum = baza + wibor;
                    procSum.setText( sum.toString() );
                }
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

    private void initDefaultValues() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        kwotaEdit.setText(sp.getString(getString(R.string.P_KWOTA), "35 000"));
        procBaza.setText(sp.getString(getString(R.string.P_PROCENT), "4.29"));
        ileRatEdit.setText(sp.getString(getString(R.string.P_ILERAT), "48"));
        dataEdit.setText(sp.getString(getString(R.string.P_DATA), "2016-10-10"));
    }

    public void OnDateEditClick(View v){
        MyDatePicker picker = new MyDatePicker();
        picker.show(getFragmentManager(), "DatePicker");
    }

    public void OnDownBtnClick(View view){
        try {
            EditText tv = (EditText) findViewById(R.id.editText);
           // tv.setText("???");
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

    public void OnWynikBtnClick(View view) throws ParseException {
        Double kwota = Double.parseDouble(kwotaEdit.getText().toString().replace(',', '.').replace(" ", ""));
        int ileRat = Integer.parseInt(ileRatEdit.getText().toString());
        Double procent = Double.parseDouble(procSum.getText().toString()) / 100;

        Double rataBezProc = kwota / ileRat;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Date dataRaty = df.parse(dataEdit.getText().toString());
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);

        c.setTime(dataRaty);
        int rataYear = c.get(Calendar.YEAR);
        int rataMonth = c.get(Calendar.MONTH);

        Integer numerRaty = ((currentYear - rataYear) * 12 + (currentMonth - rataMonth)) + 1;

        Double kwotaTmp = kwota;
        Double rata = 0.0;

        for( int i = 0; i < numerRaty; i++)
        {
            rata = ((kwotaTmp * procent) / 12) + rataBezProc;
            kwotaTmp -= rataBezProc;
        }

        DecimalFormat dformt = new DecimalFormat("0.00");
        wynikEdit.setText(dformt.format(rata));

        rataEdit.setText(numerRaty.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void OnMenuSettingsClick(MenuItem item){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
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
