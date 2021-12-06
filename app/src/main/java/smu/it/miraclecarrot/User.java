package smu.it.miraclecarrot;

public class User {

    private String nickName;
    private boolean userLogin;

    public User() {

    }

    public User(String nickName, boolean userLogin) {
        this.nickName = nickName;
        this.userLogin = userLogin;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean getUserLogin() {
        return userLogin;
    }

    public void setUserLogin() {
        this.userLogin = userLogin;
    }

    @Override
    public String toString() {
        return "User{" +
                "nickName='" + nickName + '\'' +
                ", userLogin=" + userLogin +
                '}';
    }
}
