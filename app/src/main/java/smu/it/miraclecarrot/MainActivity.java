package smu.it.miraclecarrot;

import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    String nickName;
    LinearLayout mainLayout;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("미라클 당근 로딩 화면");

        mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                readLogin();
                return false;
            }
        });
    }

    private void readLogin() {
        databaseReference.child("login").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue(User.class) != null) {
                    User post = snapshot.getValue(User.class);
                    if(post.getUserLogin() == false) {
                        Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent2);
                    }
                    else if (post.getUserLogin() == true) {
                        nickName = post.getNickName();
                        System.out.println(nickName);

                        Intent intent1 = new Intent(getApplicationContext(), MenuActivity.class);
                        intent1.putExtra("nickName", nickName);

                        startActivity(intent1);
                    }
                } else {
                    Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent2);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FireBaseData", "loadPost:onCancelled", error.toException());
            }
        });
    }
}