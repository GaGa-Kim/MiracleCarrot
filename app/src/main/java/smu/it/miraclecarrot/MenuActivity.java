package smu.it.miraclecarrot;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Frag1 frag1;
    private Frag2 frag2;
    private Frag3 frag3;
    private Frag4 frag4;

    private long backBtnTime = 0;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    String nickName;
    String setTime;

    TimePickerDialog timePickerDialog;

    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        nickName = intent.getStringExtra("nickName");

        readTime();

        timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                cancelAlarm();

                setTime = hour+":"+minute+":"+"00";

                Map<String, Object> map = new HashMap<>();
                map.put("alarm", setTime);
                databaseReference.child(nickName).child("alarms").setValue(map);
            }
        }, 0, 0, true);

        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_schedule:
                        setFrag(0);
                        break;
                    case R.id.action_note:
                        setFrag(1);
                        break;
                    case R.id.action_carrot:
                        setFrag(2);
                        break;
                    case R.id.action_clothes:
                        setFrag(3);
                        break;
                }

                return true;
            }

        });
        frag1 = new Frag1();
        frag2 = new Frag2();
        frag3 = new Frag3();
        frag4 = new Frag4();
        setFrag(0); // 첫 Fragment 화면 지정

        Bundle bundle = new Bundle();
        bundle.putString("nickName", nickName);
        frag1.setArguments(bundle);
        frag2.setArguments(bundle);
        frag3.setArguments(bundle);
        frag4.setArguments(bundle);
    }

    // Fragment 교체가 일어나는 실행문
    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        switch (n) {
            case 0:
                ft.replace(R.id.main_frame, frag1);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, frag2);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, frag3);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.main_frame, frag4);
                ft.commit();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1:
                cancelAlarm();
                databaseReference.child("login").child("nickName").setValue("");
                databaseReference.child("login").child("userLogin").setValue(false);
                break;
            case R.id.submenu1:
                timePickerDialog.show();
                break;
            case R.id.submenu2:
                databaseReference.child(nickName).child("alarms").child("alarm").removeValue();
                cancelAlarm();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
     if(System.currentTimeMillis() > backBtnTime + 2000) {
         backBtnTime = System.currentTimeMillis();
         Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
         return;
     }

     if(System.currentTimeMillis() <= backBtnTime + 2000) {
         ActivityCompat.finishAffinity(this);
         System.exit(0);
     }
    }

    // 그 날의 일정 리마인드 알림을 보낼 수 있는 기능
    private void startAlarm(String time){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("nickName", nickName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        long now = System.currentTimeMillis(); // 시스템의 시간 불러오기
        Date date = new Date(now);

        SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm:ss");
        String getDay = simpleDateFormatDay.format(date); // 년, 월, 일을 String 형식의 getDay에 저장
        String getTime = simpleDateFormatTime.format(date);  // 시, 분을 String 형식의 getTime에 저장

        String from = getDay+" "+time;

        try {
            Date getTimee = simpleDateFormatTime.parse(getTime);
            Date timee = simpleDateFormatTime.parse(time);

            // 현재 시간과 비교해서 현재 시간보다 미래일 경우에만 알림을 하도록 함 (과거 시간에 알림을 할 필요가 없으므로)
            if (timee.after(getTimee) == true) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date datetime = null;
                try {
                    datetime = dateFormat.parse(from);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(datetime);

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                System.out.println("알림이 설정되었습니다. "+from);
            }
            else {
                Toast.makeText(this, "알람을 받을 미래의 시간을 선택하세요.", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);
        System.out.println("알림이 취소되었습니다.");
    }

    private void readTime() {
        databaseReference.child(nickName).child("alarms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot timeData : snapshot.getChildren()) {
                    setTime = timeData.getValue().toString();
                    System.out.println(setTime);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startAlarm(setTime);
                        }
                    }, 1000);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FireBaseData", "loadPost:onCancelled", error.toException());
            }
        });
    }
}