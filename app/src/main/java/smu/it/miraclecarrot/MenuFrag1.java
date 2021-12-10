package smu.it.miraclecarrot;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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

public class MenuFrag1 extends Fragment {

    private View view;
    private ListView listView;
    private EditText editText;
    private Button btnAdd, btnTime;
    private TextView calendarView, timeView, userNameView;

    private ArrayList<String> schedule_list = new ArrayList<String>();  // 일정 목록
    private ArrayList<String> key_list = new ArrayList<String>();  // 일정을 저장해둔 데이터베이스의 키 값 목록
    private ArrayList<Boolean> check_list = new ArrayList<Boolean>(); // 일정 체크박스 목록
    private ArrayAdapter<String> adapter;

    // 파이어베이스 사용
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private int pos = 0, currentPoint;
    private String nickName, date, gettodoText, gettodoTime, todotime, todotext, todokey;
    private Boolean gettodoCheck, todocheck;
    private Bundle bundle;
    private Calendar calendar;
    private int mYear, mMonth, mDay;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private ToDo todo1, todo2;
    private Map<String, Object> map = new HashMap<>();
    private PopupMenu popup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag1, container, false);

        // MenuActivity에서 넘겨준 현재 사용자의 닉네임을 가져와서 적어줌
        bundle = this.getArguments();
        if(bundle != null) {
            bundle = getArguments();
            nickName = bundle.getString("nickName");
        }
        userNameView = view.findViewById(R.id.userNameView);
        userNameView.setText(nickName+"님의 미라클당근");

        // 리스트뷰에 다중 선택 체크박스를 설정
        listView = view.findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, schedule_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

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

        // 오늘 날짜의 ToDo 리스트와 그 날 스케줄의 키 값을 가져옴
        readToDo();
        bringKey();
        
        // 데이트피커를 이용해 일정을 작성할 날짜를 변경
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                if (day < 10) {
                    calendarView.setText(year + " - " + (month + 1) + " - 0" + day);
                }
                else {
                    calendarView.setText(year + " - " + (month + 1) + " - " + day);
                }
                // 날짜 변경 시 그 날에 맞는 ToDo 리스트와 그 날 스케줄의 키 값을 가져옴
                readToDo();
                bringKey();
                Toast.makeText(getActivity().getApplicationContext(), "날짜가 변경되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }, mYear, mMonth, mDay);

        // 날짜를 클릭 시 데이터피커를 불러옴
        calendarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calendarView.isClickable()) {
                    datePickerDialog.show();
                }
            }
        });
        
        btnTime = view.findViewById(R.id.btnTime);
        timeView = view.findViewById(R.id.timeView);
        // 일정과 그 일정을 할 시간을 선택하기 위해 타임피커 사용하며 미라클 모닝 앱이므로 새벽 4시부터 오전 11시 전까지만 시간 설정이 가능함
        timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                if(hour > 10 || hour < 4) {
                    Toast.makeText(getActivity().getApplicationContext(), "4시 ~ 11시 전까지의 시간만 선택 가능합니다. 다시 선택하세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (hour == 10) {
                        if (minute > 10) {
                            timeView.setText("AM " + hour + ":" + minute + " ");
                            Toast.makeText(getActivity().getApplicationContext(), "시간이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                        } else if (minute < 10) {
                            timeView.setText("AM " + hour + ":0" + minute + " ");
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
            }
        }, 0, 0, true);

        // 시간 설정 클릭 시 타임피커를 불러옴
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });

        editText = view.findViewById(R.id.editText);
        btnAdd = view.findViewById(R.id.btnAdd);
        // 버튼 클릭 시 데이터베이스에 데이트피커로 선택한 날짜와 일정 내용, 타임피커로 선택한 시간, 그리고 체크박스 체크 유무를 저장
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date = calendarView.getText().toString();
                gettodoText = editText.getText().toString();
                gettodoTime = timeView.getText().toString();
                gettodoCheck = false;  // 처음에는 체크박스가 체크되지 않으므로 false로 설정

                // 버튼의 글씨가 '수정 완료'일 때는 선택한 날짜의 일정 내용을 수정하거나 시간을 수정
                if(btnAdd.getText() == "수정 완료") {  // 시간만 수정
                    if(btnTime.getText() == "시간 수정") {
                        databaseReference.child(nickName).child("todos").child(date).child(key_list.get(pos)).child("toDoTime").setValue(gettodoTime);
                        editText.setVisibility(view.VISIBLE);
                        btnTime.setText("시간 설정");
                        btnAdd.setText("일정 추가");
                    }
                    else {  // 일정 내용만 수정
                        databaseReference.child(nickName).child("todos").child(date).child(key_list.get(pos)).child("toDoText").setValue(gettodoText);
                        btnTime.setVisibility(view.VISIBLE);
                        editText.setHint("할 일을 입력하세요.");
                        btnAdd.setText("일정 추가");
                    }
                }

                // 버튼의 글씨가 '일정 추가'일 때 시간이 없으면 선택한 날짜의 일정 내용과 체크박스 체크 유무만 저장
                else if (gettodoTime == null) {
                        todo1 = new ToDo(gettodoText, gettodoCheck);
                        databaseReference.child(nickName).child("todos").child(date).push().setValue(todo1);
                }

                // 버튼의 글씨가 '일정 추가'일 때 선택한 날짜의 일정 내용과 시간, 체크박스 체크 유무 저장
                else {
                    todo2 = new ToDo(gettodoText, gettodoTime, gettodoCheck);
                    databaseReference.child(nickName).child("todos").child(date).push().setValue(todo2);
                }

                editText.setText(null);
                timeView.setText(null);

                // 일정 추가 완료 후 키보드 내리기
                InputMethodManager mInputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                // 저장한 일정을 읽어옴
                readToDo();
            }
        });
        
        // ToDo 리스트를 클릭 시, true였을 경우 false로, false였을 경우 true로 바꿔 데이터베이스에 저장
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                date = calendarView.getText().toString();
                gettodoCheck = listView.isItemChecked(position);
                databaseReference.child(nickName).child("todos").child(date).child(key_list.get(position)).child("toDoCheck").setValue(gettodoCheck);

                if (check_list.get(position) == false) {  // false였을 경우 true로 바꿔준 후 5 포인트 증가
                    readPoint();
                    Toast.makeText(getActivity(), "일정 완료! 5 포인트 획득", Toast.LENGTH_SHORT).show();
                    map.put("point", currentPoint+5);
                    databaseReference.child(nickName).child("points").setValue(map);
                }
                else if (check_list.get(position) == true) {  // true였을 경우 false로 바꿔준 후 5포인트 회수
                    readPoint();
                    Toast.makeText(getActivity(), "일정 완료 취소! 5 포인트 회수", Toast.LENGTH_SHORT).show();
                    map.put("point", currentPoint-5);
                    databaseReference.child(nickName).child("points").setValue(map);
                }
            }
        });

        // TODO 리스트를 길게 클릭 시 팝업 메뉴로 내용 수정, 내용 삭제, 시간 수정, 시간 삭제, 일정 삭제 선택 가능
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                date = calendarView.getText().toString();
                popup = new PopupMenu(getActivity(), view);
                new MenuInflater(getActivity()).inflate(R.menu.listview_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        pos = position;
                        switch (menuItem.getItemId()) {
                            case R.id.modify:  // 내용 수정하기 클릭 시 - 내용만 수정
                                editText.setHint("수정하세요.");
                                btnAdd.setText("수정 완료");
                                btnTime.setVisibility(View.INVISIBLE);
                                // 키보드 올리기
                                InputMethodManager mInputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                mInputMethodManager.showSoftInput(editText, 0);
                                break;
                            case R.id.delete:  // 내용 삭제하기 클릭 시 - 내용만 삭제
                                databaseReference.child(nickName).child("todos").child(date).child(key_list.get(position)).child("toDoText").setValue("");
                                break;
                            case R.id.modifyTime:  // 시간 수정하기 클릭 시 - 시간만 수정
                                editText.setVisibility(view.INVISIBLE);
                                btnAdd.setText("수정 완료");
                                btnTime.setText("시간 수정");
                                break;
                            case R.id.deleteTime:  // 시간 삭제하기 클릭 시 - 시간만 삭제
                                databaseReference.child(nickName).child("todos").child(date).child(key_list.get(position)).child("toDoTime").setValue("");
                                break;
                            case R.id.deleteAll:  // 일정 수정하기 클릭 시 - 일정 내용과 시간 모두 삭제
                                databaseReference.child(nickName).child("todos").child(date).child(key_list.get(position)).removeValue();
                                if(check_list.get(pos) == true) {  // 체크를 해서 5 포인트를 획득했을 경우 5포인트 회수
                                    readPoint();
                                    map.put("point", currentPoint-5);
                                    databaseReference.child(nickName).child("points").setValue(map);
                                }
                                break;
                        }
                        readToDo();
                        return false;
                    }
                });

                popup.show();
                return true;
            }
        });
        return view;
    }

    // 데이터베이스에 저장된 일정과 체크박스 상태를 읽어와서 배열에 저장
    private void readToDo() {
        date = calendarView.getText().toString();
        databaseReference.child(nickName).child("todos").child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 선택된 새로운 날짜에 맞는 일정을 읽어오기 위해 배열 초기화
                schedule_list.clear();
                check_list.clear();

                for (DataSnapshot textData : snapshot.getChildren()) {
                    todotime = textData.child("toDoTime").getValue().toString();
                    todotext = textData.child("toDoText").getValue().toString();
                    todocheck = (Boolean) textData.child("toDoCheck").getValue();

                    if (todotime != null) {
                        schedule_list.add(todotime+todotext);
                    }
                    else {
                        schedule_list.add(todotext);
                    }
                    check_list.add(todocheck);
                }
                listView.setAdapter(adapter);

                // 체크박스 상태를 읽어와서 배열에 저장한 후, 리스트뷰의 체크박스 상태를 true 또는 false로 바꿔줌
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

    // 데이터베이스에 저장된 일정을 가져오기 위해 랜덤으로 저장된 키 값을 가져와서 배열에 저장
    private void bringKey() {
        date = calendarView.getText().toString();
        databaseReference.child(nickName).child("todos").child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 선택된 새로운 날짜에 맞는 일정의 키를 읽어오기 위해 배열 초기화
                key_list.clear();
                for (DataSnapshot keyData : snapshot.getChildren()) {
                    todokey = keyData.getKey();
                    key_list.add(todokey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FireBaseData", "loadPost:onCancelled", error.toException());
            }
        });
    }

    // 데이터베이스에 저장된 포인트 정보를 가져옴
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
