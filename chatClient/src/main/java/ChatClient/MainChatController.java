package ChatClient;

import com.sun.org.apache.xpath.internal.operations.String;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainChatController implements Initializable {

    @FXML
    public VBox mainChatPanel;
    @FXML
    public TextArea mainChatArea;

    @FXML
    public ListView contactList;
    @FXML
    public TextField inputField;
    @FXML
    public Button btnSend;
    public Label label;


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
        java.lang.String massage = inputField.getText();

        if (massage.isEmpty()) {
            return;
        }

       clickMouseContacts();
        mainChatArea.appendText(massage + System.lineSeparator());
        inputField.clear();

    }
/* Такое решение с объяснениями я нашла в книге Шилдта. Но не получилось напечатать именно выбранный контакт
* и get.Children() тоже идея не дала мне ввести. Насколько я поняла, он не мой label видит,
* а только ссылку на созданны  объект. А как исправить ситуацию?*/

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List contacts = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            contacts.add("Contact#" + (i + 1));
        }
        contactList.setItems(FXCollections.observableList(contacts));
    }

    public void clickMouseContacts() {
        Label label=new Label();
        mainChatArea.appendText(java.lang.String.valueOf(label));
        MultipleSelectionModel<java.lang.String> lv = contactList.getSelectionModel();
        lv.selectedItemProperty().addListener(new ChangeListener<java.lang.String>() {
            @Override
            public void changed(ObservableValue<? extends java.lang.String>
                                        observable, java.lang.String oldValue,
                                java.lang.String newValue) {
                if (newValue != null) {
                    label.setText("< " + newValue + " >: ");

                } else {
                    label.setText("All: ");
                }
            }

        });
    }

}
