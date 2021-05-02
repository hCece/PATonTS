package JSON;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONPreference {
	JSONBin jsonBin = new JSONBin();
	JSONObject jo = new JSONObject();
	public ArrayList<String> getActivityID(String user){
		try {
			ArrayList<String> returnValue = new ArrayList<String>();
			jo = jsonBin.getPreferences();
			Iterator<String> keys = jo.keys();
			JSONObject joUser = new JSONObject();
			while(keys.hasNext()) {
				 String key = keys.next();
				 joUser = jo.getJSONObject(key);
				 if(joUser.has(user)) returnValue.add(key);
			}
			return returnValue;
		}catch(JSONException e) {
			e.printStackTrace();
		}
		return null;
		
		
		
		
	}
	
	
	
}
