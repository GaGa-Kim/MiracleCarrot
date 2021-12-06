package smu.it.miraclecarrot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText enterNickname;
    private Button enterButton;

    // 파이어베이스 사용
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    String nickName;
    User user;
    Intent intent1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        enterNickname = findViewById(R.id.enterNickname);

        enterButton = findViewById(R.id.enterButton);
        // 닉네임 입력 후, 버튼 클릭 시 인텐트를 이용해 닉네임을 넘겨주며, MenuActivity로 화면 이동
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nickName = enterNickname.getText().toString();
                user = new User(nickName, true);  // 데이터베이스에 닉네임과 현재 로그인 상태 (true) 를 저장
                databaseReference.child("login").setValue(user);

                intent1 = new Intent(getApplicationContext(), MenuActivity.class);
                intent1.putExtra("nickName", nickName);
                startActivity(intent1);

                enterNickname.setText(null);
            }
        });
    }
}