package chat_client_server.error;

public class WrongCredentialsExeption extends RuntimeException{
    public WrongCredentialsExeption() {
    }

    public WrongCredentialsExeption(String message) {
        super(message);
    }
}
