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

    EditText enterNickname;
    Button enterButton;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    String nickName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        enterNickname = findViewById(R.id.enterNickname);

        enterButton = findViewById(R.id.enterButton);
        enterButton.setOnClickListener(new View.OnClickListener() {  // 닉네임 입력 후, 버튼 클릭 시 인텐트
            @Override
            public void onClick(View view) {
                String nickName = enterNickname.getText().toString();
                User user = new User(nickName, true);

                databaseReference.child("login").setValue(user);

                Intent intent1 = new Intent(getApplicationContext(), MenuActivity.class);
                intent1.putExtra("nickName", nickName);
                startActivity(intent1);

                enterNickname.setText(null);
            }
        });
    }
}