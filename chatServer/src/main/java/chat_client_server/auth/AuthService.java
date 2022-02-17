package chat_client_server.auth;

import java.sql.SQLException;

public interface AuthService {

    void start();

    void stop();



    String  authorizeUserByLoginAndPassword(String login, String password) throws SQLException;

    String changeNick(String login, String newNick);

    void createNewUser(String login, String password, String nick);

    void deleteUser(String login, String pass);

    void changePassword(String login, String oldPass, String newPass);

    void resetPassword(String login, String newPass, String secret);
}