package chat_client_server.Server;

import chat_client_server.error.WrongCredentialsExeption;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private Thread handlerThread;
    private Server server;
    private String user;
    File file;
    ExecutorService executorService = Executors.newCachedThreadPool();

    public ClientHandler(Socket socket, Server server) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Handler created");
        } catch (IOException e) {
            System.out.println("Connection broken with user " + user);
        }
    }

    public void handle() {
        executorService.execute(()->{
        //handlerThread = new Thread(() -> {
            authorize();
            System.out.println(Thread.currentThread().getName());
            while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                try {
                    String message = in.readUTF();
                    handleMessage(message);
                } catch (IOException e) {
                    System.out.println("Connection broken with user " + user);
                    server.removeAuthorizedClientFromList(this);
                }
            }
        });
       // handlerThread.start();

    }

    private void handleMessage(String message) {
        String[] splitMessage = message.split(Server.REGEX);
        try {
            switch (splitMessage[0]) {
                case "/w":
                    server.privateMessage(this.user, splitMessage[1], splitMessage[2], this);
                    break;
                case "/broadcast":
                    server.broadcastMessage(user, splitMessage[1]);
                    break;
                case "/change_nick":
                    String nick = server.getAuthService().changeNick(this.user, splitMessage[1]);
                    server.removeAuthorizedClientFromList(this);
                    this.user = nick;
                    server.addAuthorizedClientToList(this);
                    send("/change_nick_ok");
                    break;
                case "/change_pass":
                    server.getAuthService().changePassword(this.user, splitMessage[1], splitMessage[2]);
                    send("/change_pass_ok");
                    break;
                case "/remove":
                    server.getAuthService().deleteUser(splitMessage[1], splitMessage[2]);
                    this.socket.close();
                    break;
                case "/register":
                    server.getAuthService().createNewUser(splitMessage[1], splitMessage[2], splitMessage[3]);
                    send("register_ok:");
                    break;
            }
        } catch (IOException e) {
            send("/error" + Server.REGEX + e.getMessage());
        }
    }

    private void authorize() {
        System.out.println("Authorizing");

        while (true) {
            try {
                String message = in.readUTF();
                if (message.startsWith("/auth")) {
                    String[] parsedAuthMessage = message.split(Server.REGEX);
                    String response = "";
                    String nickname = null;
                    try {
                        try {
                            nickname = server.getAuthService().authorizeUserByLoginAndPassword(parsedAuthMessage[1], parsedAuthMessage[2]);
                            file = new File("saveMessage/" + nickname + ".txt");
                            if (!file.exists()) {
                                try {
                                    file.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try (FileInputStream fis = new FileInputStream("saveMessage/" + nickname + ".txt")) {
                                    int x;
                                    while ((x = fis.read()) > -1) {
                                    }
                                }
                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } catch (WrongCredentialsExeption e) {
                        response = "/error" + Server.REGEX + e.getMessage();
                        System.out.println("Wrong credentials, nick " + parsedAuthMessage[1]);
                    }

                    if (server.isNickBusy(nickname)) {
                        response = "/error" + Server.REGEX + "this client already connected";
                        System.out.println("Nick busy " + nickname);
                    }
                    if (!response.equals("")) {
                        send(response);
                    } else {
                        this.user = nickname;
                        server.addAuthorizedClientToList(this);
                        send("/auth_ok" + Server.REGEX + nickname);
                        break;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Thread getHandlerThread() {
        return handlerThread;
    }

    public String getUserNick() {
        return this.user;
    }
}