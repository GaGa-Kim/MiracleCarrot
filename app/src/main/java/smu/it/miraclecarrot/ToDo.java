package smu.it.miraclecarrot;

// ToDo 리스트를 파이어베이스 데이터베이스에 저장하기 위해 사용
public class ToDo {

    private String todoText;  // ToDo 내용
    private String todoTime;  // ToDo 시간
    private boolean todoCheck;  // ToDo 체크박스 (true, false)

    public ToDo() {

    }

    public ToDo(String todoText, boolean todoCheck) {
        this.todoText = todoText;
        this.todoCheck = todoCheck;
    }

    public ToDo(String todoText, String todoTime, boolean todoCheck) {
        this.todoText = todoText;
        this.todoTime = todoTime;
        this.todoCheck = todoCheck;
    }

    public String getToDoText() {
        return todoText;
    }

    public void setToDoText(String todoText) {
        this.todoText = todoText;
    }

    public String getToDoTime() {
        return todoTime;
    }

    public void setToDoTime(String todoTime) {
        this.todoTime = todoTime;
    }

    public boolean getToDoCheck() {
        return todoCheck;
    }

    public void setToDoCheck(boolean todoCheck) {
        this.todoCheck = todoCheck;
    }

}
