package Controller;

import java.util.List;

import JSON.JSONActivity;
import JSON.JSONWorkspace;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
public class NewWorkspace {

	@FXML private TextField lblName;
	@FXML private TextField lblDescription;
	@FXML private Button btnSetActivity;
	@FXML private ComboBox<String> cmbActivity;
	@FXML private ListView<String> lstActivity;
	
	JSONActivity jsonActivity = new JSONActivity();
	JSONWorkspace jsonWorkspace = new JSONWorkspace();
	@FXML void setActivity(ActionEvent event) {
		lstActivity.getItems().add(cmbActivity.getValue());
	}
	
	@FXML void toJSON(ActionEvent event) {

    	String[] aName = lstActivity.getItems().toArray(new String[0]);
    	List<String> aID =  lstActivity.getItems();
    	aID.clear();
    	
    	for(int i=0; i<aName.length; i++)
    	{
    		aID.add(jsonActivity.getID("name", aName[i]));
    	}
    	
		jsonWorkspace.setWorkspace(lblName.getText(), lblDescription.getText(), aID);
		cmbActivity.getItems().clear();
		lstActivity.getItems().clear();
		cmbActivity.getItems().addAll(jsonActivity.getActivitiesWithoutWorkspace());
	}

	public void initialize() {
		cmbActivity.getItems().addAll(jsonActivity.getActivitiesWithoutWorkspace());

	}
	
	

}
