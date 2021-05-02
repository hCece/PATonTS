package application;

import java.io.IOException;

import JSON.JSONUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerRegistration {

    @FXML
    private TextField email;

    @FXML
    private TextField password;

    @FXML
    private TextField username;

    @FXML
    private TextField password2;

    @FXML
    private Button setUserBtn;

    @FXML
    private Label lblError;

    @FXML
    private Button BtnSgnIn;

    @FXML
    void goToSignIn(ActionEvent event) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
    	loader.setController(new ControllerLogIn());
    	Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
    	stage.setScene(loader.load());
    	stage.show();
    }

    @FXML
    void setUser(ActionEvent event) {

    	JSONUser jsonUser = new JSONUser();
    	if (password.getText().equals(password2.getText()))
			try {
				System.err.println(jsonUser.setUser(username.getText(), password.getText().hashCode(), email.getText()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		else
    		System.err.println("Le password non sono uguali");
    
    }
    
}
