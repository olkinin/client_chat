package ChatClient;

import ChatClient.netWork.MessageProcessor;
import ChatClient.netWork.NetworkService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainChatController implements Initializable, MessageProcessor {
    public static final String REGEX = "%!%";
    public VBox authLoginPanel;
    public TextField authLoginField;
    public PasswordField authPasswordField;
    public TextField authNick;


    private String nick;
    private NetworkService networkService;

    @FXML
    private VBox changeNickPanel;

    @FXML
    private TextField newNickField;

    @FXML
    private VBox changePasswordPanel;

    @FXML
    private PasswordField oldPassField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private VBox loginPanel;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private VBox mainChatPanel;

    @FXML
    private TextArea mainChatArea;

    @FXML
    private ListView contactList;

    @FXML
    private TextField inputField;

    @FXML
    private Button btnSend;

    public void connectToServer(ActionEvent actionEvent) {
    }

    public void disconnectFromServer(ActionEvent actionEvent) {
    }

    public void mockAction(ActionEvent actionEvent) {
    }

    public void exit(ActionEvent actionEvent) {
        System.exit(1);
    }

    public void showHelp(ActionEvent actionEvent) {
    }

    public void showAbout(ActionEvent actionEvent) {
    }

    public void sendMessage(ActionEvent actionEvent) {
        String message = inputField.getText();
        if (message.isEmpty()) {
            return;
        }
        String recipient = (String) contactList.getSelectionModel().getSelectedItem();
        if (!recipient.equals("ALL")) {
            networkService.sendMessage("/w" + REGEX + recipient + REGEX + message);
        } else {
            networkService.sendMessage("/broadcast" + REGEX + message);
        }
        inputField.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.networkService = new NetworkService(this);
    }

    @Override
    public void processMessage(String message) {
        Platform.runLater(() -> parseIncomingMessage(message));
    }

    private void parseIncomingMessage(String message) {
        String[] splitMessage = message.split(REGEX);
        switch (splitMessage[0]) {
            case "/auth_ok":
                this.nick = splitMessage[1];
                loginPanel.setVisible(false);
                mainChatPanel.setVisible(true);
                break;
            case "/error":
                showError(splitMessage[1]);
                System.out.println("got error " + splitMessage[1]);
                break;
            case "/list":
                ArrayList<String> contacts = new ArrayList<String>();
                contacts.add("ALL");
                for (int i = 1; i < splitMessage.length; i++) {
                    contacts.add(splitMessage[i]);
                }
                contactList.setItems(FXCollections.observableList(contacts));
                contactList.getSelectionModel().selectFirst();
                break;
            case "/change_pass_ok":
                changePasswordPanel.setVisible(false);
                mainChatPanel.setVisible(true);
                break;
            default:
                mainChatArea.appendText(splitMessage[0] + System.lineSeparator());
                break;
        }
    }

    public void sendChangeNick(ActionEvent actionEvent) {
        if (newNickField.getText().isEmpty()) return;
        networkService.sendMessage("/change_nick" + REGEX + newNickField.getText());
    }

    public void sendChangePass(ActionEvent actionEvent) {
        if (newPasswordField.getText().isEmpty() || oldPassField.getText().isEmpty()) return;
        networkService.sendMessage("/change_pass" + REGEX + oldPassField.getText() + REGEX + newPasswordField.getText());
    }

    public void sendEternalLogout(ActionEvent actionEvent) {
        networkService.sendMessage("/remove");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR,
                "An error occured: " + message,
                ButtonType.OK);
        alert.showAndWait();
    }

    public void sendAuth(ActionEvent actionEvent) {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (login.isEmpty() || password.isEmpty()) {
            return;
        }

        String message = "/auth" + REGEX + login + REGEX + password;

        if (!networkService.isConnected()) {
            try {
                networkService.connect();
            } catch (IOException e) {
                e.printStackTrace();
                showError(e.getMessage());

            }
        }

        networkService.sendMessage(message);
    }

    public void returnToChat(ActionEvent actionEvent) {
        changeNickPanel.setVisible(false);
        changePasswordPanel.setVisible(false);
        mainChatPanel.setVisible(true);
    }

    public void showChangeNick(ActionEvent actionEvent) {
        mainChatPanel.setVisible(false);
        changeNickPanel.setVisible(true);
    }

    public void showChangePass(ActionEvent actionEvent) {
        mainChatPanel.setVisible(false);
        changePasswordPanel.setVisible(true);
    }


//    public void authSendAuth(ActionEvent actionEvent) {
//        String login = authLoginField.getText();
//        String password = authPasswordField.getText();
//        String nick = authNick.getText();
//
//        if (login.isEmpty() || password.isEmpty()) {
//            return;
//        }
//
//        String message = "/auth" + REGEX + login + REGEX + password+REGEX+nick;
//
//        if (!networkService.isConnected()) {
//            try {
//                networkService.connect();
//            } catch (IOException e) {
//                e.printStackTrace();
//                showError(e.getMessage());
//
//            }
//        }
//
//        networkService.sendMessage(message);
//    }
}

