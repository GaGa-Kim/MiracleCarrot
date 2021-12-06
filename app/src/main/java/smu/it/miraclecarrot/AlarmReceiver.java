package smu.it.miraclecarrot;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.view.MenuItem;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.app.PendingIntent.*;

public class AlarmReceiver extends BroadcastReceiver {

    public AlarmReceiver(){ }

    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder = null;

    private static String CHANNEL_ID = "channel";
    private static String CHANNEL_NAME = "channel";

    // 파이어베이스 사용
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private String nickName;
    private AlarmManager alarmManager;
    private Intent intent2;
    private PendingIntent pendingIntent;
    private MenuItem menuItem;

    @Override
    public void onReceive(Context context, Intent intent) {
        // MenuActivity의 nickName 변수 접근
        nickName = ((MenuActivity)MenuActivity.context_main).nickName;

        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        // 오레오 이상은 채널을 설정해줘야 Notification이 작동하므로 채널 설정
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT));
            // 채널 생성 후 해당 채널로 Notification 생성
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        // 알림창 클릭 시 MainActivity 화면으로 이동
        intent2 = new Intent(context, MainActivity.class);
        pendingIntent = getActivity(context,0, intent2, 0);

        // Notification 설정 - 알림창 제목, 아이콘, 터치 시 자동 삭제 설정
        builder.setContentTitle(nickName+"님의 미라클 당근 : 오늘의 일정을 확인하세요!");
        builder.setSmallIcon(R.drawable.carrot);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);;

        notificationManager.notify(1, builder.build());

        // 푸시 알림 후 데이터베이스에서 알림 시간 삭제
        databaseReference.child(nickName).child("alarms").child("alarm").removeValue();

        // MenuActivity의 menuItem 변수 접근
        menuItem = ((MenuActivity)MenuActivity.context_main).menuItem;
        // 푸시 알림 후 알림 시간 옵션 메뉴의 서브 메뉴 수정
        menuItem.setTitle("알림 없음");
    }
}