package smu.it.miraclecarrot;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Frag1 extends Fragment {

    private View view;
    private ListView listView;
    private EditText editText;
    private Button btnAdd, btnTime;
    private TextView tv, timeView, userNameView;

    ArrayList<String> schedule_list = new ArrayList<String>();  // 일정 목록
    ArrayList<String> key_list = new ArrayList<String>();  // 키 값 목록
    ArrayList<Boolean> check_list = new ArrayList<Boolean>(); // 일정 체크박스 목록
    ArrayAdapter<String> adapter;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    int pos = 0;
    int currentPoint;

    String nickName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag1, container, false);

        Bundle extra = this.getArguments();
        if(extra != null) {
            extra = getArguments();
            nickName = extra.getString("nickName");
        }

        readPoint();

        userNameView = view.findViewById(R.id.userNameView);
        userNameView.setText(nickName+"님의 미라클당근");

        listView = view.findViewById(R.id.listView);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, schedule_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

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
        readToDo();
        bringKey();

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                if (day < 10) {
                    tv.setText(year + " - " + (month + 1) + " - 0" + day);
                }
                else {
                    tv.setText(year + " - " + (month + 1) + " - " + day);
                }
                readToDo();
                bringKey();
                Toast.makeText(getActivity().getApplicationContext(), "날짜가 변경되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }, mYear, mMonth, mDay);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 달력 텍스트뷰 클릭 시
                if (tv.isClickable()) {
                    datePickerDialog.show();
                }
            }
        });

        // 일정 체크 리스트

        btnTime = view.findViewById(R.id.btnTime);
        timeView = view.findViewById(R.id.timeView);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                if(hour > 10) {
                    Toast.makeText(getActivity().getApplicationContext(), "오전 10시 이후의 일정은 작성 불가능합니다. 시간을 다시 선택하세요.", Toast.LENGTH_SHORT).show();
                }
                else if (hour == 10) {
                    if (minute > 10) {
                        timeView.setText("AM "+hour+ ":" + minute+" ");
                        Toast.makeText(getActivity().getApplicationContext(), "시간이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                    else if (minute < 10) {
                        timeView.setText("AM "+hour+ ":0" + minute+" ");
                        Toast.makeText(getActivity().getApplicationContext(), "시간이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    if (minute > 10) {
                        timeView.setText("AM 0"+hour+ ":" + minute+" ");
                        Toast.makeText(getActivity().getApplicationContext(), "시간이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                    else if (minute < 10) {
                        timeView.setText("AM 0"+hour+ ":0" + minute+" ");
                        Toast.makeText(getActivity().getApplicationContext(), "시간이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, 0, 0, true);

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });

        editText = view.findViewById(R.id.editText);
        btnAdd = view.findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = tv.getText().toString();
                String gettodoText = editText.getText().toString();
                String gettodoTime = timeView.getText().toString();
                Boolean gettodoCheck = false;

                if(btnAdd.getText() == "수정 완료") {
                    if(btnTime.getText() == "시간 수정") {
                        databaseReference.child(nickName).child("todos").child(date).child(key_list.get(pos)).child("toDoTime").setValue(gettodoTime);
                        editText.setVisibility(view.VISIBLE);
                        btnTime.setText("시간 설정");
                        btnAdd.setText("일정 추가");
                    }

                    else {
                        databaseReference.child(nickName).child("todos").child(date).child(key_list.get(pos)).child("toDoText").setValue(gettodoText);
                        btnTime.setVisibility(view.VISIBLE);
                        editText.setHint("할 일을 입력하세요.");
                        btnAdd.setText("일정 추가");
                    }
                }

                else if (gettodoTime == null) {
                        ToDo todo1 = new ToDo(gettodoText, gettodoCheck);
                        databaseReference.child(nickName).child("todos").child(date).push().setValue(todo1);
                }

                else {
                    ToDo todo2 = new ToDo(gettodoText, gettodoTime, gettodoCheck);
                    databaseReference.child(nickName).child("todos").child(date).push().setValue(todo2);
                }

                editText.setText(null);
                timeView.setText(null);

                // 일정 추가 완료 후 키보드 내리기
                InputMethodManager mInputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            }
        });

        readToDo();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String date = tv.getText().toString();
                boolean gettodoCheck = listView.isItemChecked(position);

                databaseReference.child(nickName).child("todos").child(date).child(key_list.get(position)).child("toDoCheck").setValue(gettodoCheck);

                if (check_list.get(position) == false) {
                    Toast.makeText(getActivity(), "일정 완료! 5 포인트 획득", Toast.LENGTH_SHORT).show();
                    readPoint();
                    Map<String, Object> map = new HashMap<>();
                    map.put("point", currentPoint+5);
                    databaseReference.child(nickName).child("points").setValue(map);
                }
                else if (check_list.get(position) == true) {
                    Toast.makeText(getActivity(), "일정 완료 취소! 5 포인트 회수", Toast.LENGTH_SHORT).show();
                    readPoint();
                    Map<String, Object> map = new HashMap<>();
                    map.put("point", currentPoint-5);
                    databaseReference.child(nickName).child("points").setValue(map);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu popup = new PopupMenu(getActivity(), view);
                new MenuInflater(getActivity()).inflate(R.menu.listview_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String date = tv.getText().toString();
                        pos = position;
                        switch (menuItem.getItemId()) {
                            case R.id.modify:
                                editText.setHint("수정하세요.");
                                btnAdd.setText("수정 완료");
                                btnTime.setVisibility(View.INVISIBLE);
                                // 키보드 올리기
                                InputMethodManager mInputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                mInputMethodManager.showSoftInput(editText, 0);
                                break;
                            case R.id.delete:
                                databaseReference.child(nickName).child("todos").child(date).child(key_list.get(position)).child("toDoText").setValue("");
                                break;
                            case R.id.modifyTime:
                                editText.setVisibility(view.INVISIBLE);
                                btnAdd.setText("수정 완료");
                                btnTime.setText("시간 수정");
                                break;
                            case R.id.deleteTime:
                                databaseReference.child(nickName).child("todos").child(date).child(key_list.get(position)).child("toDoTime").setValue("");
                                break;
                            case R.id.deleteAll:
                                databaseReference.child(nickName).child("todos").child(date).child(key_list.get(position)).removeValue();
                                if(check_list.get(pos) == true) {
                                    readPoint();
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("point", currentPoint-5);
                                    databaseReference.child(nickName).child("points").setValue(map);
                                }
                                break;
                        }
                        return false;
                    }
                });

                popup.show();
                return true;
            }
        });

        readToDo();

        return view;
    }

    private void readToDo() {
        String date = tv.getText().toString();

        databaseReference.child(nickName).child("todos").child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                schedule_list.clear();
                check_list.clear();

                for (DataSnapshot textData : snapshot.getChildren()) {
                    String todotime = textData.child("toDoTime").getValue().toString();
                    String todotext = textData.child("toDoText").getValue().toString();
                    Boolean todocheck = (Boolean) textData.child("toDoCheck").getValue();

                    if (todotime != null) {
                        schedule_list.add(todotime+todotext);
                    }
                    else {
                        schedule_list.add(todotext);
                    }

                    check_list.add(todocheck);
                }
                listView.setAdapter(adapter);

                for(int i = 0; i < check_list.size(); i++) {
                    listView.setItemChecked(i,check_list.get(i));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FireBaseData", "loadPost:onCancelled", error.toException());
            }
        });
    }

    private void bringKey() {
        String date = tv.getText().toString();

        databaseReference.child(nickName).child("todos").child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                key_list.clear();
                for (DataSnapshot keyData : snapshot.getChildren()) {
                    String todokey = keyData.getKey();
                    key_list.add(todokey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FireBaseData", "loadPost:onCancelled", error.toException());
            }
        });
    }

    private void readPoint() {
        databaseReference.child(nickName).child("points").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot pointData : snapshot.getChildren()) {
                    currentPoint = Integer.parseInt(pointData.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FireBaseData", "loadPost:onCancelled", error.toException());
            }
        });
    }
}
