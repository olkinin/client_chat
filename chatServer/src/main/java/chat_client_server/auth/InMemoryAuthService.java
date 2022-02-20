package chat_client_server.auth;

import java.sql.*;

public class InMemoryAuthService implements AuthService {
    private static Connection connection;
    private static Statement statement;
    private static final String DB_STRING = "jdbc:sqlite:db/example.db";
    private static PreparedStatement ps;

    String nick = null;
    // private List<User> users;


//            start();
//        this.users = new ArrayList<>();
//        users.addAll(Arrays.asList(
//                new User("log1", "pass", "nick1", "secret"),
//                new User("log2", "pass", "nick2", "secret"),
//                new User("log3", "pass", "nick3", "secret"),
//                new User("log4", "pass", "nick4", "secret"),
//                new User("log5", "pass", "nick5", "secret")
//        ));
//    }




    public InMemoryAuthService() {
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();

        }
        createTable();
    }

    public void createTable() {
        try {
            statement.executeUpdate("create table if not exists users" + "(id integer primary key autoincrement, " +
                    "login text, password text, nick text, secret text);");
            statement.execute("insert into users(login, password,nick,secret) values('log1','pass','nick1','secret'),('log2','pass','nick2','secret'),('log3','pass','nick3','secret')");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void start() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection(DB_STRING);
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



//    @Override
//    public void start() {
//        System.out.println("Auth service started");
//    }
//
//    @Override
//    public void stop() {
//        System.out.println("Auth service stopped");
//    }

//    @Override
//    public String authorizeUserByLoginAndPassword(String login, String password) {
//        for (User user : users) {
//            if (login.equals(user.getLogin()) && password.equals(user.getPassword())) {
//                return user.getNick();
//            }
//        }
//        throw new WrongCredentialsExeption("Wrong username or password");
//    }

    @Override
    public String authorizeUserByLoginAndPassword(String login, String password) throws SQLException {
        ResultSet rs = null;
        try {
            rs = statement.executeQuery("select * from users");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        while (rs.next()) {String a=rs.getString("login");
            String b = rs.getString("password");
            if(a.equals(login)&&b.equals(password))
                try {
                    nick = rs.getString("nick");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return nick;
    }


    @Override
    public String changeNick(String login, String newNick) {
        return null;
    }

//    @Override
//    public User createNewUser(String login, String password, String nick) {
//        return null;
//    }

    @Override
    public void createNewUser(String login, String password, String nick) {
        try {
            ps = connection.prepareStatement("insert into users(login, password,nick) values(?,?,?)");

            System.out.println("add string");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUser(String login, String pass) {

    }

    @Override
    public void changePassword(String login, String oldPass, String newPass) {

    }

    @Override
    public void resetPassword(String login, String newPass, String secret) {

    }
}
