package JSON;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;


/* The class JSONBin is the middle-man between the JSONBin requests and all other JSON classes from this package.
 * JSONBin makes the GET and SET requests for the other JSON classes
 */
public class JSONBin {

	private final String MASTER_KEY = "$2b$10$.22MkZ49mEZBvIq.UVQzfO1PNcDXQ3HQYYuVElCjRtVU03Mx.UHiC";
	private final String URL = "https://api.jsonbin.io/v3/b/";
	private final String UserID= "608830ecf6655022c46d5e24";
	private final String WorkspaceID = "60883169c7df3422f7ff3e81";
	private final String ActivityID = "608831b4f6655022c46d5ecc";
	private final String PreferencesID = "608831325210f622be3b4a7b";

	
	public void setUser(String JSON) {
		setJSON(UserID, JSON);
	}
	public JSONObject getUser() {
		return getJSON(UserID);
	}
	
	public void setWorkspace(String JSON) {
		setJSON(WorkspaceID, JSON);
	}
	public JSONObject getWorkspace() {
		return getJSON(WorkspaceID);
	}
	
	public void setActivity(String JSON) {
		setJSON(ActivityID, JSON);
	}
	public JSONObject getActivity() {
		return getJSON(ActivityID);
	}
	
	public void setPreferences(String JSON) {
		setJSON(PreferencesID, JSON);
	}
	public JSONObject getPreferences() {
		return getJSON(PreferencesID);
	}
	
	private void setJSON(String ID, String JSON) {
		  try {
			  URL obj = new URL(URL + ID);	
			  HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			 
			  con.setRequestMethod("PUT");
			  con.setRequestProperty("Content-Type","application/json");
			  con.setRequestProperty("X-Master-Key", MASTER_KEY);	
			 
			  // Send post request
			  con.setDoOutput(true);
			  DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			  wr.writeBytes(JSON);
			  wr.flush();
			  wr.close();
			 
			  System.out.println("Response Code : " + con.getResponseCode());
		  }catch(IOException e) {
			  e.printStackTrace();
		  }catch(Exception e) {
			  e.printStackTrace();
		  }
	}
	private JSONObject getJSON(String ID)  {
		HttpURLConnection con;
		try {
			URL obj = new URL(URL + ID + "/latest"); 
			con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("X-Master-Key", MASTER_KEY);
			System.out.println("Response Code : " + con.getResponseCode());
		
			//changing the response from BufferReader to StringBuffeer
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String output;
			StringBuffer response = new StringBuffer();
			while ((output = in.readLine()) != null) {response.append(output);}
			in.close();
			
			//Trimming the JSONObject to just the JSONdata
			JSONObject jo = new JSONObject(response.toString());
			for(int i = 0; i<jo.names().length(); i++){
				String keyTmp;
				keyTmp = jo.names().getString(i);
				String valueTmp =  jo.get(jo.names().getString(i)).toString();
				if(keyTmp.contains("record")) {
					return new JSONObject(valueTmp);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}