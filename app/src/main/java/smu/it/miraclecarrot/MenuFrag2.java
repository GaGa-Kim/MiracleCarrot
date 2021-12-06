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

public class MenuFrag2 extends Fragment {

    private View view;
    private EditText editText;
    private Button btnAdd, btnDelete;
    private TextView calendarView;

    // 파이어베이스 사용
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private String nickName, date, getdiaryText;
    private Bundle bundle;
    private Calendar calendar;
    private int mYear, mMonth, mDay;
    private Diary diary;
    private DatePickerDialog datePickerDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag2, container, false);

        // MenuActivity에서 넘겨준 현재 사용자의 닉네임을 가져와서 적어줌
        bundle = this.getArguments();
        if(bundle != null) {
            bundle = getArguments();
            nickName = bundle.getString("nickName");
        }

        // 오늘 날짜 출력
        calendarView = view.findViewById(R.id.calendarView);
        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DATE);
        if (mDay < 10) {
            calendarView.setText(mYear + " - " + (mMonth + 1) + " - 0" + mDay); // 오늘 날짜 출력
        }
        else {
            calendarView.setText(mYear + " - " + (mMonth + 1) + " - " + mDay); // 오늘 날짜 출력
        }
        readDiary();

        // 데이트피커를 이용해 일기를 작성할 날짜를 변경
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                if (day < 10) {
                    calendarView.setText(year + " - " + (month + 1) + " - 0" + day);
                }
                else {
                    calendarView.setText(year + " - " + (month + 1) + " - " + day);
                }
                readDiary();
                Toast.makeText(getActivity().getApplicationContext(), "날짜가 변경되었습니다.",Toast.LENGTH_SHORT).show();
            }
        }, mYear, mMonth, mDay);

        // 날짜를 클릭 시 데이터피커를 불러옴
        calendarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(calendarView.isClickable()) {
                    datePickerDialog.show();
                }
            }
        });

        editText = view.findViewById(R.id.editText);
        editText.setSelection(editText.getText().length());
        btnAdd = view.findViewById(R.id.btnAdd);
        // 버튼 클릭 시 그 날의 일기 저장
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getdiaryText = editText.getText().toString();
                writeNewDiary(getdiaryText);
                readDiary();
            }
        });

        btnDelete = view.findViewById(R.id.btnDelete);
        // 버튼 클릭 시 그 날의 일기 삭제
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date = calendarView.getText().toString();
                editText.setText(null);
                databaseReference.child(nickName).child("diarys").child(date).removeValue();
                Toast.makeText(getActivity(), "일기를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                readDiary();
            }
        });
        return view;
    }

    // 데이터베이스에 일기 데이터 추가
    private void writeNewDiary(String text) {
        diary = new Diary(text);
        date = calendarView.getText().toString();
        databaseReference.child(nickName).child("diarys").child(date).setValue(diary)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "일기 저장을 완료했습니다.", Toast.LENGTH_SHORT).show();
                        editText.setSelection(editText.getText().length());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "일기 저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 데이터베이스에서 일기 데이터 가져오기
    private void readDiary() {
        date = calendarView.getText().toString();
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

