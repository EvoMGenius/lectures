package f;

import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthServiceImpl implements AuthService {

    private final List<UserDataTuple> userData = new ArrayList<>(List.of(
            new UserDataTuple("user1", "pass1", "papich"),
            new UserDataTuple("user2", "pass2", "solevoy"),
            new UserDataTuple("user3", "pass3", "azino777")
                                                                        ));

    @Override
    public boolean authenticate(String login, String password) {
        if (login.isEmpty()) {
            return false;
        }
        if (password.isEmpty()) {
            return false;
        }
        for (UserDataTuple user : userData) {
            if (login.equals(user   .getLogin())) {
                if (password.equals(user.getPassword())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getNickname(String login) {
        for (UserDataTuple user : userData) {
            if (login.equals(user.getLogin())) {
                return user.getNickname();
            }
        }
        return null;
    }
}
