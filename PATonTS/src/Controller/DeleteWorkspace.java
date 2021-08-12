package Controller;

import org.json.JSONArray;

import JSON.JSONWorkspace;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

public class DeleteWorkspace {
	@FXML private ComboBox<String> cmbWorkspace;
    @FXML private Button btnDelete;
	private JSONWorkspace jsonWorkspace = new JSONWorkspace();

    @FXML void deleteWorkspace(ActionEvent event) {
    	String nameW = cmbWorkspace.getValue();
    	String idW = jsonWorkspace.getWorkspaceID("Name", nameW);
    	jsonWorkspace.deleteWorkspace(idW);
    	cmbWorkspace.getItems().clear();
    	cmbWorkspace.getItems().addAll(jsonWorkspace.getWorkspaceNames());
    }

    public void initialize() {
    	cmbWorkspace.getItems().addAll(jsonWorkspace.getWorkspaceNames());
    }
}
