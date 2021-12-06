package smu.it.miraclecarrot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MenuFrag4 extends Fragment {

    private View view;

    private TextView dayView, timeView, cityView, weatherView, tempView, clothesTypeView;
    private ImageView weatherIconView, clothesView;
    private static RequestQueue requestQueue;
    private Date date;
    private SimpleDateFormat simpleDateFormatDay, simpleDateFormatTime;
    private String getDay, getTime, city, weather, weatherIcon, imgUrl;
    private JSONArray weatherJson;
    private JSONObject weatherObj, tempK;

    Integer[] clothes = {R.string.clothes0, R.string.clothes1, R.string.clothes2, R.string.clothes3, R.string.clothes4, R.string.clothes5, R.string.clothes6, R.string.clothes7};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag4, container, false);

        dayView = view.findViewById(R.id.dayView);
        timeView = view.findViewById(R.id.timeView);
        cityView = view.findViewById(R.id.cityView);
        weatherView = view.findViewById(R.id.weatherView);
        weatherIconView = view.findViewById(R.id.weatherIconView);
        tempView = view.findViewById(R.id.tempView);
        clothesTypeView = view.findViewById(R.id.clothesTypeView);
        clothesView = view.findViewById(R.id.clothesView);

        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        }
        CurrentWeather();

        return view;
    }

    // 날씨 API를 이용해 현재의 시각의 날씨를 가져와서 그 온도에 맞는 오늘의 옷 추천
    public void CurrentWeather() {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=Seoul&appid=09ecc1cb776b0bca6b0fd36293ce2a39";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try {
                    long now = System.currentTimeMillis(); // 시스템의 현재 시간 불러오기
                    date = new Date(now);

                    // SimpleDateFormat을 통해 다양한 현식으로 현재 시간을 받아옴
                    simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
                    simpleDateFormatTime = new SimpleDateFormat("HH:mm:ss");
                    getDay = simpleDateFormatDay.format(date); // 년, 월, 일을 String 형식의 getDay에 저장
                    getTime = simpleDateFormatTime.format(date);  // 시, 분을 String 형식의 getTime에 저장

                    // 현재 날짜, 시간 출력
                    dayView.setText("현재 날짜 : " +getDay);
                    timeView.setText("현재 시간 : "+getTime);

                    JSONObject jsonObject = new JSONObject(response);

                    // 도시 설정 - 서울
                    city = jsonObject.getString("name");
                    cityView.setText("도시 이름 : "+city);

                    //  Json 형태로 정보를 읽어옴
                    // 날씨 설정
                    weatherJson = jsonObject.getJSONArray("weather");
                    weatherObj = weatherJson.getJSONObject(0);
                    weather = weatherObj.getString("description");
                    weatherView.setText("현재 날씨 : " +weather);

                    // 날씨 아이콘 - Glide를 사용해 Thread 사용 없이 바로 image url 사용
                    weatherIcon = weatherObj.getString("icon");
                    imgUrl = "http://openweathermap.org/img/w/" + weatherIcon + ".png";
                    Glide.with(getActivity()).load(imgUrl).into(weatherIconView);

                    // 기온 설정
                    tempK = new JSONObject(jsonObject.getString("main"));
                    double tempDo = (Math.round((tempK.getDouble("temp")-273.15)*100)/100.0);
                    tempView.setText("현재 온도 : " +tempDo +  "°C");

                    // 온도에 따라 if문을 사용해 옷 추천
                    int tempo = (int) tempDo;
                    if (tempDo >= 27) {
                        clothesTypeView.setText(clothes[0]);
                        clothesView.setImageResource(R.drawable.clothes0);
                    }
                    if (tempDo>=23 || tempo <=26) {
                        clothesTypeView.setText(clothes[1]);
                        clothesView.setImageResource(R.drawable.clothes1);
                    }

                    if (tempDo>=20 || tempo <=22) {
                        clothesTypeView.setText(clothes[2]);
                        clothesView.setImageResource(R.drawable.clothes2);
                    }

                    if (tempDo>=17 || tempo <=19) {
                        clothesTypeView.setText(clothes[3]);
                        clothesView.setImageResource(R.drawable.clothes3);
                    }

                    if (tempDo>=12 || tempo <=16) {
                        clothesTypeView.setText(clothes[4]);
                        clothesView.setImageResource(R.drawable.clothes4);
                    }

                    if (tempDo>=10 || tempo <=11) {
                        clothesTypeView.setText(clothes[5]);
                        clothesView.setImageResource(R.drawable.clothes5);
                    }

                    if (tempDo>=6 || tempo <=9) {
                        clothesTypeView.setText(clothes[6]);
                        clothesView.setImageResource(R.drawable.clothes6);
                    }

                    if (tempDo<=5) {
                        clothesTypeView.setText(clothes[7]);
                        clothesView.setImageResource(R.drawable.clothes7);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        };

        request.setShouldCache(false);
        requestQueue.add(request);
    }
}
