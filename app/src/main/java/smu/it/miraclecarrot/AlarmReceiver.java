package smu.it.miraclecarrot;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.app.PendingIntent.*;

public class AlarmReceiver extends BroadcastReceiver {

    public AlarmReceiver(){ }

    NotificationManager notificationManager;
    NotificationCompat.Builder builder;

    //오레오 이상은 반드시 채널을 설정해줘야 Notification이 작동함
    private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "Channel1";

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    MenuItem timeItem;

    @Override
    public void onReceive(Context context, Intent intent) {
        
        System.out.println("알림");

        String nickName = intent.getStringExtra("nickName");
        System.out.println(nickName);

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        builder = null;
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            );
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        //알림창 클릭 시 activity 화면 부름
        Intent intent2 = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = getActivity(context,0, intent2, 0);

        //알림창 제목
        builder.setContentTitle(nickName+"님의 미라클 당근 : 오늘의 일정을 확인하세요!");
        //알림창 아이콘
        builder.setSmallIcon(R.drawable.carrot);
        //알림창 터치시 자동 삭제
        builder.setAutoCancel(true);

        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        notificationManager.notify(1,notification);

        databaseReference.child(nickName).child("alarms").child("alarm").removeValue();  // 푸시 알림 후 알림 삭제
    }
}