package smu.it.miraclecarrot;

// 사용자 로그인을 위한 정보를 파이어베이스 데이터베이스에 저장하기 위해 사용
public class User {

    private String nickName;  // 현재 로그인하고 있는 닉네임
    private boolean userLogin;  // 현재 로그인을 누군가 하고 있는지 아닌지 (true, false)

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
