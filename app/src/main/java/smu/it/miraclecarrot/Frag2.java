package smu.it.miraclecarrot;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;

public class Frag2 extends Fragment {

    private View view;

    private EditText editText;
    private Button btnAdd, btnDelete;
    private TextView tv;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    String nickName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag2, container, false);

        Bundle extra = this.getArguments();
        if(extra != null) {
            extra = getArguments();
            nickName = extra.getString("nickName");
        }

        // 달력 날짜 출력
        tv = (TextView) view.findViewById(R.id.calendarView);
        Calendar cal = Calendar.getInstance();
        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DATE);
        if (mDay < 10) {
            tv.setText(mYear + " - " + (mMonth + 1) + " - 0" + mDay); // 오늘 날짜 출력
        }
        else {
            tv.setText(mYear + " - " + (mMonth + 1) + " - " + mDay); // 오늘 날짜 출력
        }
        readDiary();
        Toast.makeText(getActivity(), tv.getText().toString(), Toast.LENGTH_SHORT).show();

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                if (day < 10) {
                    tv.setText(year + " - " + (month + 1) + " - 0" + day);
                }
                else {
                    tv.setText(year + " - " + (month + 1) + " - " + day);
                }
                readDiary();
                // Toast.makeText(getActivity().getApplicationContext(), "날짜가 변경되었습니다.",Toast.LENGTH_SHORT).show();
            }
        }, mYear, mMonth, mDay);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 달력 텍스트뷰 클릭 시
                if(tv.isClickable()) {
                    datePickerDialog.show();
                }
            }
        });

        editText = view.findViewById(R.id.editText);
        btnAdd = view.findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getdiaryText = editText.getText().toString();

                writeNewDiary(getdiaryText);
            }
        });

        btnDelete = view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = tv.getText().toString();
                editText.setText(null);
                databaseReference.child(nickName).child("diarys").child(date).removeValue();
                Toast.makeText(getActivity(), "일기를 삭제했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // 데이터베이스에 일기 데이터 추가
    private void writeNewDiary(String text) {
        Diary diary = new Diary(text);
        String date = tv.getText().toString();

        databaseReference.child(nickName).child("diarys").child(date).setValue(diary)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "저장을 완료했습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 데이터베이스에서 일기 데이터 가져오기
    private void readDiary() {
        String date = tv.getText().toString();

        databaseReference.child(nickName).child("diarys").child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue(Diary.class) != null) {
                    Diary post = snapshot.getValue(Diary.class);
                    editText.setText(post.getDiaryText());
                    btnAdd.setText("일기 수정");
                    Log.w("FireBaseData", "getData" +post.toString());
                } else {
                    editText.setText(null);
                    Toast.makeText(getActivity(), "일기 없음", Toast.LENGTH_SHORT).show();
                    btnAdd.setText("일기 추가");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 실패 시 로그 출력
                Log.w("FireBaseData", "loadPost:onCancelled", error.toException());
            }
        });
    }
}

