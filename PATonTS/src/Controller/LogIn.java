package Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import JSON.JSONUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class LogIn {
    @FXML
    private Button btn;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;	
    @FXML
    private Label lbl;
    @FXML
    private ImageView imgUni;

    @FXML
    void goToNewAccount(MouseEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/register.fxml"));
    	loader.setController(new Registration());
    	Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
    	stage.setScene(loader.load());
    	stage.show();
    }
    
    public void initialize() throws FileNotFoundException {
    	imgUni.setImage(new Image(new FileInputStream("./src/View/jpg/logo.jpg")));
    }
    
    //checks if the loginData is correct, if so it will load the main scene of the project
    @FXML void getData(ActionEvent event) {
		System.out.println(password.getText() + " : "+ password.getText().hashCode());
    	JSONUser jsonUser = new JSONUser();
    	if(jsonUser.isLoginSame(username.getText(), password.getText().hashCode()))
    	{	FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/mainScene.fxml"));
    		String userID = jsonUser.findUserID("username",username.getText());
	    	loader.setController(new MainScene(userID));
	    	Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
	    	try {
				stage.setScene(loader.load());
			} catch (IOException e) {e.printStackTrace();}
	    	stage.show();
    		
    	}
    }
    
    

   
}

