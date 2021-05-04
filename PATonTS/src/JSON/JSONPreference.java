package JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
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
	
	public void setPreferece(String IDAct, String IDUser, String key, int value) {
		jo = jsonBin.getPreferences();
		try {
			if(!jo.getJSONObject(IDAct).getJSONObject(IDUser).getJSONArray(key).toString().contains(Integer.toString(value))) {
				jo.getJSONObject(IDAct).getJSONObject(IDUser).getJSONArray(key).put(value);
				jsonBin.setPreferences(jo.toString());
				System.out.println(key+ ":" + value);
				System.out.println(jo.toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void deletePreferece(String IDAct, String IDUser, String key, int value) {
		jo = jsonBin.getPreferences();
		try {
			JSONArray ja = jo.getJSONObject(IDAct).getJSONObject(IDUser).getJSONArray(key);
			if(ja.toString().contains(Integer.toString(value))) {
				for(int i = 0; i < ja.length(); i++) {
					if(ja.getInt(i) == value) {
						jo.getJSONObject(IDAct).getJSONObject(IDUser).getJSONArray(key).remove(i);	
						jsonBin.setPreferences(jo.toString());
						return;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	public HashMap<String, String> getPreference(String IDAct, String IDUser, String key) {
		jo = jsonBin.getPreferences();
		JSONObject joAct = jsonBin.getActivity();
		HashMap<String, String> returnValue = new HashMap<String, String>();
		try {
			System.out.println(jo.getJSONObject(IDAct).getJSONObject(IDUser).toString());
			JSONArray ja = jo.getJSONObject(IDAct).getJSONObject(IDUser).getJSONArray(key);
			JSONActivity jsonAct = new JSONActivity();
			String value;
			for(int i=0; i < ja.length(); i++) {
				value = joAct.getJSONObject(IDAct).getJSONArray(key).get(ja.getInt(i)).toString();
				if(key.equals("weekDays")) {
					System.out.println(value);
					String[] time = value.split(";");
					value = jsonAct.getWeekDay(time[0]) + " " + time[1];
				}
				returnValue.put(ja.get(i).toString(), value);
			}
			return returnValue;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
