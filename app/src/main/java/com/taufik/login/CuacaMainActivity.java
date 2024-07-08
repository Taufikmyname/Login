package com.taufik.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class CuacaMainActivity extends AppCompatActivity {

    private RecyclerView _recyclerView2;
    private CuacaRootModel rm;
    private TextView _totaltextView;
    private TextView _textViewCityInfo;
    private SwipeRefreshLayout _swipeRefreshLayout2;

    private Button _buttonViewCityInfo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuaca_main);

        _recyclerView2 = findViewById(R.id.recyclerView2);
        _totaltextView = findViewById(R.id.totalTextView);
        initSwipeRefreshLayout();
        initRecyclerView2();
        initButtonViewCityInfo();
    }

    private void initButtonViewCityInfo() {
        _buttonViewCityInfo = findViewById(R.id.buttonView_cityInfo);

        _buttonViewCityInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CuacaCityModel cm = rm.getCityModel();
                CuacaCoordModel com = cm.getCoordModel();
                double latitude = com.getLat();
                double longitude = com.getLon();

                Bundle param = new Bundle();
                param.putDouble("lat", latitude);
                param.putDouble("lon", longitude);


                Intent intent = new Intent(CuacaMainActivity.this, CuacaGpsActivity.class);
                intent.putExtra("param", param);
                startActivity(intent);
            }
        });
    }


    private void initSwipeRefreshLayout() {
        _swipeRefreshLayout2 = findViewById(R.id.swipeRefreshLayout2);

        _swipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initRecyclerView2();
                _swipeRefreshLayout2.setRefreshing(false);
            }
        });
    }

    private void initRecyclerView2() {
        String url = "http://api.openweathermap.org/data/2.5/forecast?id=1630789&appid=4a7d57248f59c47f26c4c210c5643311";
        AsyncHttpClient ahc =   new AsyncHttpClient();

        ahc.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new Gson();
                rm = gson.fromJson(new String(responseBody), com.taufik.login.CuacaRootModel.class);

                initCityInfo();

                RecyclerView.LayoutManager lm = new LinearLayoutManager(CuacaMainActivity.this);
                CuacaAdapter ca = new CuacaAdapter(CuacaMainActivity.this, rm);

                _recyclerView2.setLayoutManager(lm);
                _recyclerView2.setAdapter(ca);
                _totaltextView.setText("Total Record : " + ca.getItemCount());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initCityInfo() {
        CuacaCityModel cm = rm.getCityModel();
        long sunrise = cm.getSunrise();
        long sunset = cm.getSunset();
        String cityName = cm.getName();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String sunriseTime = sdf.format(new Date(sunrise * 1000));
        String sunsetTime = sdf.format(new Date(sunset * 1000));

        String cityInfo = "Kota: " + cityName + "\n" +
                "Matahari Terbit: " + sunriseTime + " (Lokal)\n" +
                "Matahari Terbenam: " + sunsetTime + " (Lokal)";

        _buttonViewCityInfo.setText(cityInfo);
    }


}