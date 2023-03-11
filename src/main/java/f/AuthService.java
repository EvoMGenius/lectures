package f;

public interface AuthService {

    boolean authenticate(String login, String password);

    String getNickname(String login);
}
