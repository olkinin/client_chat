package chat_client_server;

import chat_client_server.Server.Server;
import chat_client_server.auth.InMemoryAuthService;


public class App {
    public static void main(String[] args) {
        new Server(new InMemoryAuthService()).start();
    }
}
