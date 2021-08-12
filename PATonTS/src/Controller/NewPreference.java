package Controller;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class NewPreference{

    @FXML private TextField lblPlaces;
    @FXML private Button btnSetPlace;
    @FXML private Button btnSetWeekDay;
    @FXML private TextField lblTime;
    @FXML private ComboBox<String> cmbWorkspace;
    @FXML private ComboBox<String> cmbDay;
    @FXML private ComboBox<String> cmbActivity;
    @FXML private ListView<String> lstPlaces;
    @FXML private ListView<String> lstWeekDays;

    JSONWorkspace jsonWorkspace = new JSONWorkspace();
    JSONActivity jsonActivity = new JSONActivity();
    
	ArrayList<String> aID = new ArrayList<>();
	ArrayList<JSONObject> aData = new ArrayList<>();
	JSONArray jaPlace = new JSONArray();
	JSONArray jaWeekDays= new JSONArray();
    
    @FXML void getActivities(ActionEvent event) {
    	cmbActivity.getItems().clear();
    	JSONArray ja = new JSONArray();
    	ja = jsonWorkspace.getWorkspaceActivities("Name", cmbWorkspace.getValue());

    	
    	for(int i=0; i<ja.length(); i++)
    	{	try {
				aID.add(ja.getString(i));
			} catch (JSONException e) {e.printStackTrace();}
    	}	
    	aData = jsonActivity.getValue(aID);
    	aID.clear();
    	for(int i=0; i<aData.size(); i++) 
    	{	try {
    			aID.add(aData.get(i).get("name").toString());			
    		} catch (JSONException e) {e.printStackTrace();}
    	}
    	cmbActivity.getItems().addAll(aID);
    }

    @FXML void getPreference(ActionEvent event) {
    	lstWeekDays.getItems().clear();
    	lstPlaces.getItems().clear();
    	try {
	    	String aName = cmbActivity.getValue();
	    	for(int i = 0; i<aData.size(); i++)
	    	{	if(aData.get(i).getString("name").equals(aName))
	    		{	jaPlace=aData.get(i).getJSONArray("places");
	    			jaWeekDays= aData.get(i).getJSONArray("weekDays");
		    		break;
	    		}
	    	}
	    	
	    	for(int i = 0; i<jaPlace.length(); i++) 
	    		lstPlaces.getItems().add(jaPlace.getString(i));
	    	
	    	for(int i = 0; i<jaWeekDays.length(); i++) 
	    		lstWeekDays.getItems().add(jaWeekDays.getString(i));	    	
	    	
    	} catch (JSONException e) {e.printStackTrace();}
    }
    @FXML void setPlace(ActionEvent event) {
    	lstPlaces.getItems().add(lblPlaces.getText());
    }
    @FXML void setWeekDay(ActionEvent event) {
    	lstWeekDays.getItems().add(cmbDay.getValue()+" "+lblTime.getText());
    }

    @FXML void toJSON(ActionEvent event) {
    	String[] places = lstPlaces.getItems().toArray(new String[0]);
    	String[] weekDays = lstWeekDays.getItems().toArray(new String[0]);
    	String actName =  cmbActivity.getValue();
    	
    	String actID = jsonActivity.getID("name", actName);
    	
    	
    	jsonActivity.replacePreferences(actID, places, weekDays);
    	
    }
    
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

}
