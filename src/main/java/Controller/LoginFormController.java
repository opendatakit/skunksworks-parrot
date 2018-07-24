package Controller;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static Data.Data.FIREBASE_KEYS_FILE_NAME;

public class LoginFormController implements Initializable {
    public TextField usernameField;
    public TextField passwordField;
    public Button loginButton;
    public ProgressIndicator progressIndicator;
    public Label statusLabel;
    public ImageView configureIcon;
    public Label configureText;
    public AnchorPane centerPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stopProgress();
        setStatusText(null);

    }

    private void setStatusText(String status){
        statusLabel.setText(status);
    }

    public void loginButtonClicked(){
        loginButton.setDisable(true);
        setStatusText(null);
        startProgress();
        usernameField.setDisable(true);
        passwordField.setDisable(true);

        if(usernameField.getText().trim().isEmpty()){
            setStatusText("Username field can't be empty");
        }
        else if (passwordField.getText().trim().isEmpty()){
            setStatusText("Password field can't be empty");
        }
        else{
            attemptLogin(usernameField.getText().trim(), passwordField.getText().trim());
        }
    }

    private void attemptLogin(String username, String password) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                updateMessage("Please Wait...");
                updateProgress(-1, 100);

                try {
                    //TODO: write code to verify user credentials from server.
                    if(username.equals("admin")&& password.equals("password")) {
                        try {
                            initializeFirebaseSDK();
                        }catch (IOException e){
                            updateProgress(0,100);
                            updateMessage("Error: Firebase key file not found.");
                        }
                    }else{
                        updateProgress(0,100);
                        updateMessage("Invalid username/password");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    updateProgress(0, 100);
                }
                return null;
            }
        };
        task.setOnSucceeded(taskFinishEvent -> {
            //TODO: complete onSucceeded method.
            moveToDashboard();
        });

        progressIndicator.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(task.messageProperty());
        new Thread(task).start();
    }

    private void initializeFirebaseSDK() throws IOException{

        FileInputStream serviceAccount = new FileInputStream(FIREBASE_KEYS_FILE_NAME);
        FirebaseOptions options = new FirebaseOptions.Builder()
                 .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                 .setDatabaseUrl("https://odk-notifications.firebaseio.com/")
                 .build();
        FirebaseApp.initializeApp(options);

    }


    private void moveToDashboard() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainUI.fxml"));
        Stage stage = new Stage();
        stage.setTitle("ODK Notifications Admin Panel");
        try {
            stage.setScene(new Scene(fxmlLoader.load(),    1024, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
        ((Stage) loginButton.getScene().getWindow()).close();
    }

    private void startProgress(){
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(-1.0);
    }

    private void stopProgress(){
        progressIndicator.setVisible(false);
        progressIndicator.setProgress(0.0);
    }

}
