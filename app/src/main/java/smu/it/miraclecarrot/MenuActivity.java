package smu.it.miraclecarrot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private MenuFrag1 menuFrag1;
    private MenuFrag2 menuFrag2;
    private MenuFrag3 menuFrag3;
    private MenuFrag4 menuFrag4;

    // 파이어베이스 사용
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private long backBtnTime;
    private TimePickerDialog timePickerDialog;
    private PendingIntent pendingIntent;
    private Intent intent;
    private AlarmManager alarmManager;
    private Date date, getTimee, timee, datetime;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormatDay, simpleDateFormatTime, dateFormat;
    private String setTime, getDay, getTime, pushAlarm;
    private Bundle bundle;
    private Handler handler = new Handler();

    // MenuActivity의 변수 공유
    public static Context context_main;
    public String nickName;
    public MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        context_main = this;

        // 현재 로그인 중인 사용자의 닉네임을 받아옴
        intent = getIntent();
        nickName = intent.getStringExtra("nickName");

        // 타임 피커를 통해 알림을 위해 설정해둔 시간을 데이터베이스에 저장해둔 후, 재 로그인 시 시간을 읽어와 리마인드 알림 설정 (로그아웃을 할 경우 이전 사용자의 리마인드 알림을 울리지 않아야 하므로)
        readTime();

        menuFrag1 = new MenuFrag1();
        menuFrag2 = new MenuFrag2();
        menuFrag3 = new MenuFrag3();
        menuFrag4 = new MenuFrag4();
        setFrag(0); // 첫 Fragment 화면 지정

        // 네비게이션 메뉴바를 위함
        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_schedule:  // 일정 관리 클릭 시, Fragment 화면 전환
                        setFrag(0);
                        break;
                    case R.id.action_note:  // 일기장 클릭 시, Fragment 화면 전환
                        setFrag(1);
                        break;
                    case R.id.action_carrot:  // 당근 키우기 클릭 시, Fragment 화면 전환
                        setFrag(2);
                        break;
                    case R.id.action_clothes:  // 오늘의 추천 클릭 시, Fragment 화면 전환
                        setFrag(3);
                        break;
                }
                return true;
            }
        });

        // Bundle을 이용해 현재 사용자의 닉네임을 Fragment로 전달
        bundle = new Bundle();
        bundle.putString("nickName", nickName);
        menuFrag1.setArguments(bundle);
        menuFrag2.setArguments(bundle);
        menuFrag3.setArguments(bundle);
        menuFrag4.setArguments(bundle);

        // 리마인드 알림을 설정하기 위해 시간 선택
        timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                // 리마인드 알림 설정 시 원래 있던 알림을 지운 후 설정
                cancelAlarm();

                setTime = hour+":"+minute+":"+"00";

                // 선택한 시간을 데이터베이스에 저장
                Map<String, Object> map = new HashMap<>();
                map.put("alarm", setTime);
                databaseReference.child(nickName).child("alarms").setValue(map);
            }
        }, 0, 0, true);
    }

    // Fragment 교체가 일어나는 실행문
    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        switch (n) {
            case 0:
                ft.replace(R.id.main_frame, menuFrag1);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, menuFrag2);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, menuFrag3);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.main_frame, menuFrag4);
                ft.commit();
                break;
        }
    }

    // 옵션 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        menuItem = menu.findItem(R.id.submenu1);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1:  // 로그아웃 클릭 시 리마인드 알림을 취소하고 (로그아웃을 할 경우 이전 사용자의 리마인드 알림을 울리지 않아야 하므로) 데이터베이스에 현재 사용자를 지워줌
                cancelAlarm();
                databaseReference.child("login").child("nickName").setValue("");
                databaseReference.child("login").child("userLogin").setValue(false);
                break;
            case R.id.submenu2:  // 리마인드 알림 - 알림 설정 클릭 시 타임피커를 열어 리마인드 알림을 설정하도록 함
                timePickerDialog.show();
                break;
            case R.id.submenu3:  // 리마인드 알림 - 알림 삭제 시 데이터베이스에서 알림을 설정한 시간을 지워주고 알림 삭제
                cancelAlarm();
                databaseReference.child(nickName).child("alarms").child("alarm").removeValue();
                menuItem.setTitle("알림 없음");
                Toast.makeText(this, "알림이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 뒤로 가기 버튼을 두번 클릭 시 앱 종료
    @Override
    public void onBackPressed() {
        backBtnTime = 0;
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

    // 리마인드 알림을 위한 푸시 알림 설정
    private void startAlarm(String time){
        // 알람매니저를 사용해 푸시 알림 - AlarmReceiver에 값 전달
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(this, AlarmReceiver.class);
        // PendingIntent : 가지고 있는 인텐트를 보류하고 특정 시점에 작업을 요청하도록 함
        pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        long now = System.currentTimeMillis(); // 시스템의 현재 시간 불러오기
        date = new Date(now);

        simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormatTime = new SimpleDateFormat("HH:mm:ss");
        getDay = simpleDateFormatDay.format(date); // 현재 날짜인 년, 월, 일을 String 형식의 getDay에 저장
        getTime = simpleDateFormatTime.format(date);  // 현재 시간인 시, 분을 String 형식의 getTime에 저장

        pushAlarm = getDay+" "+time;  // 오늘의 날짜와 푸시 알림을 설정하기 위해 타임 피커로 지정한 시간

        try {
            getTimee = simpleDateFormatTime.parse(getTime);  // 현재 시간
            timee = simpleDateFormatTime.parse(time);  // 타임피커로 선택한 시간

            // 타임 피커로 선택한 시간과 현재 시간과 비교해 선택한 시간이 현재 시간보다 미래일 경우에만 알림을 하도록 함 (과거 시간에 알림을 할 필요가 없으므로)
            if (timee.after(getTimee) == true) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                datetime = null;
                try {
                    datetime = dateFormat.parse(pushAlarm);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar = Calendar.getInstance();
                calendar.setTime(datetime);

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                Toast.makeText(this, nickName+"님의 알림이 설정되었습니다. "+pushAlarm, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "알림을 받을 미래의 시간을 선택하세요.", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // 리마인드 알림을 위한 푸시 알림 취소
    private void cancelAlarm(){
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    // 재로그인 시 설정해뒀던 리마인드 알림을 위해 저장한 시간을 읽어온 후, 알림 설정 및 알림 시간 옵션 메뉴의 서브 메뉴에 출력
    private void readTime() {
        databaseReference.child(nickName).child("alarms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot timeData : snapshot.getChildren()) {
                    setTime = timeData.getValue().toString();
                    // 1초 후 데이터베이스에서 가져온 시간으로 알림 설정
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            menuItem.setTitle(setTime);
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