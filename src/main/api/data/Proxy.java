package data;

public class Proxy {
    private final String ip;
    private String username;
    private String password;

    public Proxy(String ip) {
        this.ip = ip;
    }

    public Proxy(String ip, String username, String password) {
        this.ip = ip;
        this.username = username;
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
