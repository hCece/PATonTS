package Controller;
import java.util.List;

import JSON.JSONActivity;
import JSON.JSONWorkspace;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class NewActivity {
    @FXML private TextField lblName;
    @FXML private TextField lblDuration;
    @FXML private TextField lblPlaces;
    @FXML private TextField lblTime;

    @FXML private ComboBox<String> cmbDay;
    @FXML private Button btnSetPlace;
    @FXML private Button btnSetWeekDay;
    @FXML private ListView<String> lstPlaces;
    @FXML private ListView<String> lstWeekDays;
    @FXML private ComboBox<String> cmbWorkspace;

    JSONWorkspace jsonWorkspace = new JSONWorkspace();
    JSONActivity jsonActivity = new JSONActivity();
    
    @FXML void setPlace(ActionEvent event) {
    	lstPlaces.getItems().add(lblPlaces.getText());
    }

    @FXML void setWeekDay(ActionEvent event) {
    	lstWeekDays.getItems().add(cmbDay.getValue()+" "+lblTime.getText());
    }
    
    
    //all workspaces are listed and EventHandler are described
    public void initialize() {
    	cmbWorkspace.getItems().addAll(jsonWorkspace.getWorkspaceNames());
    	cmbDay.getItems().addAll("Monday","Tuesday",
    			"Wednesday","Thursday","Friday", "Saturday","Sunday");
    	
    	lstWeekDays.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent click) {
	    		if (click.getClickCount() == 2) 
	    		{	String data = lstWeekDays.getSelectionModel().getSelectedItem();
	    			lstWeekDays.getItems().remove(data);
	    		}
	        }
    	});
    	lstPlaces.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent click) {
	    		if (click.getClickCount() == 2) 
	    		{	String data = lstWeekDays.getSelectionModel().getSelectedItem();
	    			lstPlaces.getItems().remove(data);
	    		}
	        }
    	});
    }
    
    @FXML
    void toJSON(ActionEvent event) {
    	
    	
    	String[] aName = lstWeekDays.getItems().toArray(new String[0]);

    	List<String> places = lstPlaces.getItems();
    	List<String> dayCode = places;
    	dayCode.clear();
    	for(int i =0; i<aName.length; i++)
    	{	String[] data = aName[i].split(" ");
    		data[0]=""+jsonActivity.getDayID(data[0]);
    		dayCode.add(data[0]+";"+data[1]);
    	}
    	
    	
    	String res = jsonActivity.setActivity(lblName.getText(), 
    			lblDuration.getText(), places, dayCode);
    	if(!res.contains("A_"))
    		System.out.println(res);
    	else
    	{	
    		jsonWorkspace.setActivityID("Name",
    				cmbWorkspace.getSelectionModel().getSelectedItem() , res);
    
    	}
    }

}
