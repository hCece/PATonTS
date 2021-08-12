package Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import JSON.JSONActivity;
import JSON.JSONPreference;
import JSON.JSONWorkspace;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MainScene {
	@FXML private ListView<String> lstWorkspace;
	@FXML private ListView<String> lstActivity;
	@FXML private ListView<String> lstTimeUnselected;
	@FXML private ListView<String> lstTimeSelected;
	@FXML private ListView<String> lstPlaceUnselected;
	@FXML private ListView<String> lstPlaceSelected;
	@FXML private Label lblUser;
	@FXML private MenuItem mnNewPreference;
	@FXML private MenuItem mnNewActivity;
	@FXML private MenuItem mnNewWorkspace;

	private String userID;
	private String actID = "";
	private ArrayList<JSONObject> activityData = new ArrayList<>();
	private HashMap<String, String> places = new HashMap<String, String>();
	private HashMap<String, String> weekDays = new HashMap<String, String>();
	private JSONPreference jsonPreference = new JSONPreference();
	private JSONWorkspace jsonWorkspace = new JSONWorkspace();
	private JSONActivity jsonActivity = new JSONActivity();

	public MainScene(String userID) {
		this.userID = userID;
	}

	// loads all workspaces the user is subscribed to and lists them
	private void listWorkspace() {

		ArrayList<String> actID = new ArrayList<>();
		ArrayList<String> workID = new ArrayList<>();
		ArrayList<String> workName = new ArrayList<>();
		// the program gets all activitiesID the user is subscribed to and will deduct
		// the workspaces
		actID = jsonPreference.getActivityID(userID);
		for (String tmpActID : actID) {
			String tmpWorkID = jsonWorkspace.getWorkspaceID("ActivityID", tmpActID);
			if (!workID.contains(tmpWorkID)) {
				workID.add(tmpWorkID);
			}
		}
		for (String tmpWorkID : workID) {
			workName.add(jsonWorkspace.getWorkspaceData(tmpWorkID, "Name"));
		}
		lstWorkspace.getItems().addAll(workName);
	}

	// loads all activities the user is subscribed to and lists them
	private void listActivities(JSONArray activityID) {
		ArrayList<String> toArrayList = new ArrayList<>();
		try {
			for (int i = 0; i < activityID.length(); i++)
				toArrayList.add(activityID.getString(i));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		activityData = jsonActivity.getValue(toArrayList);
		toArrayList.clear();
		for (JSONObject tmpData : activityData) {
			try {
				toArrayList.add(tmpData.getString("name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		lstActivity.getItems().clear();
		lstActivity.getItems().addAll(toArrayList);

	}

	private void listPreferences(String activityName) {
		JSONArray weekDays = new JSONArray();
		JSONArray places = new JSONArray();

		actID = jsonActivity.getID("name", activityName);
		try {
			for (JSONObject jo : activityData) {
				if (jo.getString("name").equals(activityName)) {
					weekDays = jo.getJSONArray("weekDays");
					places = jo.getJSONArray("places");
				}
			}

			this.weekDays = jsonPreference.getPreference(actID, userID, "weekDays");
			this.places = jsonPreference.getPreference(actID, userID, "places");

			lstTimeSelected.getItems().clear();
			lstTimeUnselected.getItems().clear();
			for (int i = 0; i < weekDays.length(); i++) {
				if (this.weekDays.containsValue(weekDays.getString(i)))
					lstTimeSelected.getItems().add(weekDays.getString(i));
				else
					lstTimeUnselected.getItems().add(weekDays.getString(i));
			}

			lstPlaceSelected.getItems().clear();
			lstPlaceUnselected.getItems().clear();
			for (int i = 0; i < places.length(); i++) {
				if (this.places.containsValue(places.getString(i)))
					lstPlaceSelected.getItems().add(places.getString(i));
				else
					lstPlaceUnselected.getItems().add(places.getString(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// the method sets the selected preference to the jsonBin table
	private void setPreference(String key, String value) {
		if (key.equals("weekDays")) {
			int pos = jsonActivity.getPositionPreference(actID, "weekDays", value);
			if (pos != -1) {
				jsonPreference.setPreferece(actID, userID, key, pos);
				lstTimeUnselected.getItems().remove(value);
				lstTimeSelected.getItems().add(value);
			}
		} else {
			int pos = jsonActivity.getPositionPreference(actID, "places", value);
			if (pos != -1) {
				jsonPreference.setPreferece(actID, userID, key, pos);
				lstPlaceUnselected.getItems().remove(value);
				lstPlaceSelected.getItems().add(value);
			}
		}
	}

	// the method sets the selected preference to the jsonBin table
	private void unsetPreference(String key, String value) {
		if (key.equals("weekDays")) {
			for (Entry<String, String> entry : weekDays.entrySet()) {
				System.out.println(entry.getValue());
				if (entry.getValue().equals(value))
					jsonPreference.deletePreferece(actID, userID, key, Integer.parseInt(entry.getKey()));
			}
			lstTimeSelected.getItems().remove(value);
			lstTimeUnselected.getItems().add(value);

		} else // key = "place"
		{
			for (Entry<String, String> entry : weekDays.entrySet()) {
				if (entry.getValue().equals(value))
					jsonPreference.deletePreferece(actID, userID, key, Integer.parseInt(entry.getKey()));
			}
			lstPlaceSelected.getItems().remove(value);
			lstPlaceUnselected.getItems().add(value);
		}
	}

	// in initialize all EventHandler are described and it will check if the user is
	// an admin.---
	public void initialize() throws FileNotFoundException { // TODO: check if user is admin or not

		listWorkspace();
		// if a workspace is clicked on, it will load and list all the activities that
		// are linked to the workspace
		lstWorkspace.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				String space = lstWorkspace.getSelectionModel().getSelectedItem();
				JSONArray aID = new JSONArray();
				aID = jsonWorkspace.getWorkspaceActivities("Name", space);
				System.out.println(aID);
				listActivities(aID);
			}
		});
		// if a activity is clicked on, it will load, list and sort all the preferences
		// that are linked to the activity
		lstActivity.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				String aName = lstActivity.getSelectionModel().getSelectedItem();
				listPreferences(aName);
			}

		});
		// If a preference is double-clicked, the preference get swapped
		lstTimeSelected.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				if (click.getClickCount() == 2) {
					String tName = lstTimeSelected.getSelectionModel().getSelectedItem();
					unsetPreference("weekDays", tName);
				}
			}
		});
		lstPlaceSelected.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				if (click.getClickCount() == 2) {
					String tName = lstPlaceSelected.getSelectionModel().getSelectedItem();
					unsetPreference("places", tName);
				}
			}
		});
		lstTimeUnselected.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				if (click.getClickCount() == 2) {
					String tName = lstTimeUnselected.getSelectionModel().getSelectedItem();
					setPreference("weekDays", tName);
				}
			}
		});
		lstPlaceUnselected.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				if (click.getClickCount() == 2) {
					String tName = lstPlaceUnselected.getSelectionModel().getSelectedItem();
					setPreference("places", tName);
				}
			}

		});

	}

	@FXML void newActivityWindow(ActionEvent event) {
		newWindow("newActivity.fxml", "New activity", new NewActivity(), 750, 500);
	}
	@FXML void newPreferenceWindow(ActionEvent event) {
		newWindow("newPreference.fxml", "New prefernece", new NewPreference(), 750, 500);
	}
	@FXML void newWorkspaceWindow(ActionEvent event) {
		newWindow("newWorkspace.fxml", "New workspace", new NewWorkspace(), 600, 300);
	}
    @FXML void deleteWorkspaceWindow(ActionEvent event) {
    	newWindow("deleteWorkspace.fxml", "Delete workspace", new DeleteWorkspace(), 250, 150);
    }

	private void newWindow(String fileName, String title, Object controller, int length, int height) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("/View/" + fileName));
			fxmlLoader.setController(controller);
			Stage stage = new Stage();
			stage.setTitle(title);
			Scene scene = new Scene(fxmlLoader.load(), length, height);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
