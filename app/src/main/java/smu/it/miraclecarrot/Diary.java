package smu.it.miraclecarrot;

// 일일 다이어리를 파이어베이스 데이터베이스에 저장하기 위해 사용
public class Diary {

    private String diaryText;  // 다이어리 내용

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
