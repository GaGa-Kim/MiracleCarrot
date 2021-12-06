package smu.it.miraclecarrot;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Diary {

    private String diaryText;

    public Diary() {

    }

    public Diary(String diaryText) {
        this.diaryText = diaryText;
    }

    public String getDiaryText() {
        return diaryText;
    }

    public void setDiaryText() {
        this.diaryText = diaryText;
    }

    @Override
    public String toString() {
        return "Diary{" +
                "diaryText='" + diaryText + '\'' +
                '}';
    }
}
