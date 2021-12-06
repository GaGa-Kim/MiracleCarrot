package smu.it.miraclecarrot;

public class ToDo {

    private String todoText;
    private String todoTime;
    private boolean todoCheck;

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
