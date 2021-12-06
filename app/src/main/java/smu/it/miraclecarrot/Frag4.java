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

public class Frag4 extends Fragment {

    private View view;

    private TextView textView, dayView, timeView, cityView, weatherView, tempView, clothesTypeView;
    private ImageView weatherIconView, clothesView;
    static RequestQueue requestQueue;

    String[] clothes = {"민소매티, 반바지, 반팔티",
                        "반팔티, 얇은 셔츠, 얇은 긴팔, 반바지, 면바지",
                        "긴팔티, 얇은 가디건, 면바지, 후드티, 청바지, 슬랙스",
                        "얇은 니트, 얇은 재킷, 가디건, 맨투맨, 면바지, 청바지",
                        "얇은 재킷, 가디건, 간절기 야상, 맨투맨, 니트",
                        "재킷, 트렌치 코트, 니트, 면바지, 청바지",
                        "코트, 가죽 재킷, 니트, 스카프, 두꺼운 바지",
                        "패딩, 두꺼운 코트, 목도리, 기모 제품"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag4, container, false);

        // 날씨 불러오기
        textView = (TextView) view.findViewById(R.id.tempView);
        dayView = (TextView) view.findViewById(R.id.dayView);
        timeView = (TextView) view.findViewById(R.id.timeView);
        cityView = (TextView) view.findViewById(R.id.cityView);
        weatherView = (TextView) view.findViewById(R.id.weatherView);
        weatherIconView = (ImageView) view.findViewById(R.id.weatherIconView);
        tempView = (TextView) view.findViewById(R.id.tempView);
        clothesTypeView = (TextView) view.findViewById(R.id.clothesTypeView);
        clothesView = (ImageView) view.findViewById(R.id.clothesView);

        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        }

        CurrentCall();

        return view;
    }

    public void CurrentCall() {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=Seoul&appid=09ecc1cb776b0bca6b0fd36293ce2a39";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try {
                    long now = System.currentTimeMillis(); // 시스템의 시간 불러오기
                    Date date = new Date(now);

                    // SimpleDateFormat을 통해 다양한 현식으로 현재 시간을 받아옴
                    SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm:ss");
                    String getDay = simpleDateFormatDay.format(date); // 년, 월, 일을 String 형식의 getDay에 저장
                    String getTime = simpleDateFormatTime.format(date);  // 시, 분을 String 형식의 getTime에 저장

                    // 날짜, 시간 출력
                    dayView.setText("현재 날짜 : " +getDay);
                    timeView.setText("현재 시간 : "+getTime);

                    JSONObject jsonObject = new JSONObject(response);

                    // 도시 설정
                    String city = jsonObject.getString("name");
                    cityView.setText("도시 이름 : "+city);

                    // 날씨 설정
                    JSONArray weatherJson = jsonObject.getJSONArray("weather");
                    JSONObject weatherObj = weatherJson.getJSONObject(0);
                    String weather = weatherObj.getString("description");
                    weatherView.setText("현재 날씨 : " +weather);

                    // 날씨 아이콘 : Glide를 사용해 Thread 사용 없이 바로 image url 사용
                    JSONArray weatherIconJson = jsonObject.getJSONArray("weather");
                    JSONObject weatherIconObj = weatherIconJson.getJSONObject(0);
                    String weatherIcon = weatherObj.getString("icon");
                    String imgUrl = "http://openweathermap.org/img/w/" + weatherIcon + ".png";
                    Glide.with(getActivity()).load(imgUrl).into(weatherIconView);

                    // 기온 설정
                    JSONObject tempK = new JSONObject(jsonObject.getString("main"));
                    double tempDo = (Math.round((tempK.getDouble("temp")-273.15)*100)/100.0);
                    tempView.setText("현재 온도 : " +tempDo +  "°C");

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
