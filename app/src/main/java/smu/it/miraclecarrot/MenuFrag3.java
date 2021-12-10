package smu.it.miraclecarrot;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MenuFrag3 extends Fragment {

    private View view;
    private ImageView carrotView, nutrientsView, waterView, sunView;
    private TextView percentView, pointView;
    private EditText goalView;
    private ProgressBar progressBar;
    private LinearLayout animationLayout = null;
    private AnimationDrawable animationDrawable;

    // 파이어베이스 사용
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private String nickName, goal, currentGoal;
    private Bundle bundle;
    private SoundPool soundPool;
    private int currentPoint, currentPercent, soundID_1, soundID_2, soundID_3, soundID_4;
    private float scale;
    private Map<String, Object> map = new HashMap<>();
    private Animation carrot_drop_ani, carrot_shine_ani, carrot_shake_ani, carrot_harvest_ani, carrot_fire_ani;
    private LayoutInflater nutrientInflater, waterInflater, sineInflater, harvestInflater;
    private Handler handler = new Handler();
    private BitmapDrawable frame1, frame2, frame3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag3, container, false);

        // MenuActivity에서 넘겨준 현재 사용자의 닉네임을 가져와서 적어줌
        bundle = this.getArguments();
        if(bundle != null) {
            bundle = getArguments();
            nickName = bundle.getString("nickName");
        }

        // 효과음에 사용할 음악들
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundID_1 = soundPool.load(getActivity(), R.raw.nutrient, 1);
        soundID_2 = soundPool.load(getActivity(), R.raw.water, 2);
        soundID_3 = soundPool.load(getActivity(), R.raw.sun, 3);
        soundID_4 = soundPool.load(getActivity(), R.raw.harvest, 4);
        
        // 데이터베이스에 저장된 목표(자기보상), 포인트, 퍼센트, 당근 크기 정보를 가져옴
        readGoal();  
        readPoint();
        readPercent();
        readCarrotScale();

        // 자기보상 입력 후 엔터 클릭 시 저장
        goalView = view.findViewById(R.id.goalView);
        goalView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyCode == keyEvent.KEYCODE_ENTER) {
                    goal = goalView.getText().toString();
                    map.put("goals", goal);
                    databaseReference.child(nickName).child("goal").setValue(map);
                }
                return false;
            }
        });

        percentView = view.findViewById(R.id.percentView);
        progressBar = view.findViewById(R.id.progressBar);
        carrotView = view.findViewById(R.id.carrotView);
        animationLayout = view.findViewById(R.id.animationLayout);
        pointView = view.findViewById(R.id.pointView);

        // 영양분과 물의 물방물이 떨어지는 애니메이션 : Tweening Animation
        carrot_drop_ani = AnimationUtils.loadAnimation(getActivity(), R.anim.ani_carrot_drop);
        // 햇빛이 반짝이는 애니메이션 : Tweening Animation
        carrot_shine_ani = AnimationUtils.loadAnimation(getActivity(), R.anim.ani_carrot_shine);
        // 분무기와 해가 움직이는 애니메이션 : Tweening Animation
        carrot_shake_ani = AnimationUtils.loadAnimation(getActivity(), R.anim.ani_carrot_shake);

        // 이미지뷰인 영양분, 물, 햇빛 클릭 시 이벤트
        nutrientsView = view.findViewById(R.id.nutrientsView);
        nutrientsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readPoint();
                if (currentPoint - 20 >= 0) {  // 포인트가 20 이상 있을 때만
                    // inflater를 이용해 animationLayout에 영양분 주기 layout인 carrot_nutrient 가져옴
                    nutrientInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    nutrientInflater.inflate(R.layout.carrot_nutrient, animationLayout, true);

                    // 효과음 재생
                    soundPool.play(soundID_1, 1f, 1f, 0, 0, 1f);

                    // carrot_nutrient layout 의 imageView를 가져와서 애니메이션
                    animationLayout.findViewById(R.id.nutrients).startAnimation(carrot_shake_ani);
                    animationLayout.findViewById(R.id.nutrient1).startAnimation(carrot_drop_ani);
                    animationLayout.findViewById(R.id.nutrient2).startAnimation(carrot_drop_ani);
                    animationLayout.findViewById(R.id.nutrient3).startAnimation(carrot_drop_ani);
                    animationLayout.findViewById(R.id.nutrient4).startAnimation(carrot_drop_ani);
                    animationLayout.findViewById(R.id.nutrient5).startAnimation(carrot_drop_ani);

                    // 애니메이션 끝난 후 inflater 삭제
                    animationCarrotStop();

                    // 1초 후 당근 크기 키우기
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            growCarrot(20);
                        }
                    }, 1000);

                    databaseReference.child(nickName).child("points").child("point").setValue(currentPoint-20);
                }
                else {
                    Toast.makeText(getActivity(), "포인트가 모자랍니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        waterView = view.findViewById(R.id.waterView);
        waterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readPoint();
                if (currentPoint - 10 >= 0) {  // 포인트가 10 이상 있을 때만
                    // inflater를 이용해 animationLayout에 물 주기 layout인 carrot_water 가져옴
                    waterInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    waterInflater.inflate(R.layout.carrot_water, animationLayout, true);

                    // 효과음 재생
                    soundPool.play(soundID_2, 1f, 1f, 0, 0, 1f);

                    // carrot_water layout 의 imageView를 가져와서 애니메이션
                    animationLayout.findViewById(R.id.sprayer).startAnimation(carrot_shake_ani);
                    animationLayout.findViewById(R.id.water1).startAnimation(carrot_drop_ani);
                    animationLayout.findViewById(R.id.water2).startAnimation(carrot_drop_ani);
                    animationLayout.findViewById(R.id.water3).startAnimation(carrot_drop_ani);
                    animationLayout.findViewById(R.id.water4).startAnimation(carrot_drop_ani);
                    animationLayout.findViewById(R.id.water5).startAnimation(carrot_drop_ani);

                    // 애니메이션 끝난 후 inflater 삭제
                    animationCarrotStop();

                    // 1초 후 당근 크기 키우기
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            growCarrot(10);
                        }
                    }, 1000);
                    databaseReference.child(nickName).child("points").child("point").setValue(currentPoint-10);
                }

                else {
                    Toast.makeText(getActivity(), "포인트가 모자랍니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        sunView = view.findViewById(R.id.sunView);
        sunView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readPoint();
                if (currentPoint - 10 >= 0) {  // 포인트가 10 이상 있을 때만
                    // inflater를 이용해 animationLayout에 햇빛 주기 layout인 carrot_sun 가져옴
                    sineInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    sineInflater.inflate(R.layout.carrot_sun, animationLayout, true);

                    // 효과음 재생
                    soundPool.play(soundID_3, 1f, 1f, 0, 0, 1f);

                    // carrot_sun layout 의 imageView를 가져와서 애니메이션
                    animationLayout.findViewById(R.id.sun).startAnimation(carrot_shake_ani);
                    animationLayout.findViewById(R.id.shine).startAnimation(carrot_shine_ani);

                    // 애니메이션 끝난 후 inflater 삭제
                    animationCarrotStop();

                    // 1초 후 당근 크기 키우기
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            growCarrot(10);
                        }
                    }, 1000);databaseReference.child(nickName).child("points").child("point").setValue(currentPoint-10);
                }

                else {
                    Toast.makeText(getActivity(), "포인트가 모자랍니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }

    // 이미지뷰인 영양분, 물, 햇빛 클릭 시에 당근 키우기
    private void growCarrot(int point) {

        // 당근 성장률 퍼센트 가져와서 프로그레스바에 적용
        readPercent();
        progressBar.setProgress(currentPercent);

        // 당근 이미지 사이즈 가져오기
        float carrotSizeX = carrotView.getScaleX(), carrotSizeY = carrotView.getScaleY();

        // 당근 크기 증가 및 증가된 크기 데이터베이스에 저장
        carrotView.setScaleX(carrotSizeX+point/10*0.1f);
        carrotView.setScaleY(carrotSizeY+point/10*0.1f);
        databaseReference.child(nickName).child("carrotscale").child("scale").setValue(carrotView.getScaleX());

        // 성장률 증가
        progressBar.incrementProgressBy(point);

        // 성장률 출력
        percentView.setText(currentPercent+"%");
        databaseReference.child(nickName).child("percents").child("percent").setValue(currentPercent+point);

        // 당근이 모두 성장하면 수확할 때 나타나는 애니메이션
        carrot_harvest_ani = AnimationUtils.loadAnimation(getActivity(), R.anim.ani_carrot_harvest);

        // 성장률이 100%를 넘을 시에 목표 달성 보상에 대한 토스트 메시지 출력
        if (currentPercent+point >= 100) {
            goal = goalView.getText().toString();
            Toast.makeText(view.getContext(), "당근을 수확했습니다!\n보상 : " + goal, Toast.LENGTH_LONG).show();

            // 토스트 메시지 출력 후
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 성장률과 당근 크기 초기화
                    progressBar.setProgress(0);
                    percentView.setText("0%");
                    databaseReference.child(nickName).child("percents").child("percent").setValue(0);

                    // activity_main layout 의 imageView를 가져와서 수확 애니메이션
                    carrotView.startAnimation(carrot_harvest_ani);
                    // 당근 크기 원래대로
                    carrotView.setScaleX(carrotSizeX/2);
                    carrotView.setScaleY(carrotSizeY/2);
                    databaseReference.child(nickName).child("carrotscale").setValue(carrotView.getScaleX());

                    // 수확 축하 폭죽 애니메이션
                    harvestAnimation();
                }
            }, 500);
        }
    }

    // 영양분, 물, 햇빛 효과 출력 완료 후, inflater 삭제
    private void animationCarrotStop() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animationLayout.removeAllViews();
            }
        }, 1000);
    }

    // 수확 축하 폭죽 애니메이션
    private void harvestAnimation() {
        soundPool.play(soundID_4, 1f, 1f, 0, 0, 1f);

        // 폭죽의 깜빡임을 위한 애니메이션 : Tweening Animation
        carrot_fire_ani = AnimationUtils.loadAnimation(getActivity(), R.anim.ani_carrot_fire);

        // inflater를 이용해 animationLayout에 수확 하기 layout인 carrot_harvest 가져옴
        harvestInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        harvestInflater.inflate(R.layout.carrot_harvest, animationLayout, true);

        // 폭죽 효과를 주기 위한 애니메이션 : Frame Animation
        frame1 = (BitmapDrawable) getResources().getDrawable(R.drawable.firecracker, null);
        frame2 = (BitmapDrawable) getResources().getDrawable(R.drawable.fireworks_1, null);
        frame3 = (BitmapDrawable) getResources().getDrawable(R.drawable.fireworks_2, null);

        int reasonableDuration = 1000;

        animationDrawable = new AnimationDrawable();

        animationDrawable.setOneShot(false);
        animationDrawable.addFrame(frame1, reasonableDuration);
        animationDrawable.addFrame(frame2, reasonableDuration);
        animationDrawable.addFrame(frame3, reasonableDuration);

        animationLayout.findViewById(R.id.firecracker).setBackgroundDrawable(animationDrawable);

        animationDrawable.setVisible(true, true);
        animationDrawable.start();

        // 폭죽의 깜빡임을 위한 애니메이션
        animationLayout.findViewById(R.id.firecracker).startAnimation(carrot_fire_ani);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animationDrawable.stop();
                animationDrawable.setVisible(false, false);

                // 애니메이션 끝난 후 inflater 삭제
                animationCarrotStop();
            }
        }, 2100);
    }

    // 데이터베이스에 저장된 목표(자기보상) 정보를 가져옴
    private void readGoal() {
        databaseReference.child(nickName).child("goal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot goalData : snapshot.getChildren()) {
                    currentGoal = goalData.getValue().toString();
                    goalView.setText(currentGoal);
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
                    pointView.setText(String.valueOf("사용 가능한 포인트 : "+currentPoint));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FireBaseData", "loadPost:onCancelled", error.toException());
            }
        });
    }

    // 데이터베이스에 저장된 퍼센트 정보를 가져옴
    private void readPercent() {
        databaseReference.child(nickName).child("percents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot percentData : snapshot.getChildren()) {
                    currentPercent = Integer.parseInt(percentData.getValue().toString());
                    percentView.setText(currentPercent+"%");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FireBaseData", "loadPost:onCancelled", error.toException());
            }
        });
    }

    // 데이터베이스에 저장된 당근 크기 정보를 가져옴
    private void readCarrotScale() {
        databaseReference.child(nickName).child("carrotscale").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot scaleData : snapshot.getChildren()) {
                    scale = Float.parseFloat(scaleData.getValue().toString());
                    if(percentView.getText() != "0%") {
                        carrotView.setScaleX(scale);
                        carrotView.setScaleY(scale);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FireBaseData", "loadPost:onCancelled", error.toException());
            }
        });
    }

}
