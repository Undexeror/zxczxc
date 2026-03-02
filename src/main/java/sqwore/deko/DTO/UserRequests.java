package sqwore.deko.DTO;

public class UserRequests {
    private String username;
    private String password;
    private Integer count;

    public UserRequests(String username, String password, Integer count) {
        this.username = username;
        this.password = password;
        this.count = count;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
