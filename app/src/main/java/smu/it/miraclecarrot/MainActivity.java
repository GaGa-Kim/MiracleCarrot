package smu.it.miraclecarrot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private String nickName;
    private LinearLayout mainLayout;
    private Intent intent1, intent2, intent3;
    private User user;

    // 파이어베이스 사용
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("미라클 당근 로딩 화면");

        // 메인 레이아웃 터치 시 로그인 정보를 읽어옴
        mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                readLogin();
                return false;
            }
        });
    }

    // 데이터베이스에서 로그인 정보를 얻어오기 위함
    private void readLogin() {
        databaseReference.child("login").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 데이터베이스에 User.class가 있다면
                if(snapshot.getValue(User.class) != null) {
                    user = snapshot.getValue(User.class);
                    // getUserLogin()이 false라면 아무도 로그인을 하지 않은 것이므로 로그인을 하기 위해 인텐트를 이용해 LoginActivity로 화면 이동
                    if(user.getUserLogin() == false) {
                        intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent1);
                    }
                    // getUserLogin()이 true라면 누군가 로그인을 한 것이므로 로그인을 한 닉네임을 getNickName()으로 가져와서 인텐트를 이용해 MenuActivity로 넘겨주며 화면 이동
                    else if (user.getUserLogin() == true) {
                        nickName = user.getNickName();
                        intent2 = new Intent(getApplicationContext(), MenuActivity.class);
                        intent2.putExtra("nickName", nickName);
                        startActivity(intent2);
                    }
                // 데이터베이스에 User.class가 없다면 아무도 로그인을 하지 않은 것이므로 로그인을 하기 위해 인텐트를 이용해 LoginActivity로 화면 이동
                } else {
                    intent3 = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent3);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FireBaseData", "loadPost:onCancelled", error.toException());
            }
        });
    }
}