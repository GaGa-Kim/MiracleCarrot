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
    private JSONObject jsonObject, weatherObj, tempK;

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

    // ?????? API??? ????????? ????????? ????????? ????????? ???????????? ??? ????????? ?????? ????????? ??? ??????
    public void CurrentWeather() {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=Seoul&appid=09ecc1cb776b0bca6b0fd36293ce2a39";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try {
                    long now = System.currentTimeMillis(); // ???????????? ?????? ?????? ????????????
                    date = new Date(now);

                    // SimpleDateFormat??? ?????? ????????? ???????????? ?????? ????????? ?????????
                    simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
                    simpleDateFormatTime = new SimpleDateFormat("HH:mm:ss");
                    getDay = simpleDateFormatDay.format(date); // ???, ???, ?????? String ????????? getDay??? ??????
                    getTime = simpleDateFormatTime.format(date);  // ???, ?????? String ????????? getTime??? ??????

                    // ?????? ??????, ?????? ??????
                    dayView.setText("?????? ?????? : " +getDay);
                    timeView.setText("?????? ?????? : "+getTime);

                    jsonObject = new JSONObject(response);

                    // ?????? ?????? - ??????
                    city = jsonObject.getString("name");
                    cityView.setText("?????? ?????? : "+city);

                    //  Json ????????? ????????? ?????????
                    // ?????? ??????
                    weatherJson = jsonObject.getJSONArray("weather");
                    weatherObj = weatherJson.getJSONObject(0);
                    weather = weatherObj.getString("description");
                    weatherView.setText("?????? ?????? : " +weather);

                    // ?????? ????????? - Glide??? ????????? Thread ?????? ?????? ?????? image url ??????
                    weatherIcon = weatherObj.getString("icon");
                    imgUrl = "http://openweathermap.org/img/w/" + weatherIcon + ".png";
                    Glide.with(getActivity()).load(imgUrl).into(weatherIconView);

                    // ?????? ??????
                    tempK = new JSONObject(jsonObject.getString("main"));
                    double tempDo = (Math.round((tempK.getDouble("temp")-273.15)*100)/100.0);
                    tempView.setText("?????? ?????? : " +tempDo +  "??C");

                    // ????????? ?????? if?????? ????????? ??? ??????
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
