package JSON;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class JSONUser {
	JSONBin jsonBin = new JSONBin();
	JSONObject jo = new JSONObject();
	public ArrayList<String> getValue(JSONObject jo, String key) {
		ArrayList<String> list = new ArrayList<String>();
		return getValue(jo, key, list);
	}	
	private ArrayList<String> getValue(JSONObject jo, String key, ArrayList<String> value) {
		for(int i = 0; i<jo.names().length(); i++){
			String keyTmp;
			try {
				keyTmp = jo.names().getString(i);
			
				String valueTmp =  jo.get(jo.names().getString(i)).toString();
				if(keyTmp.contains("U_")) {
					getValue(new JSONObject(valueTmp), key, value);
				}
				else if (keyTmp.equals(key)) value.add(valueTmp);
			}catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
		}
		
		return value;
	}
	public boolean isLoginSame(String username, int password) {		
		jo = jsonBin.getUser();
		ArrayList<String> usernames = new ArrayList<String>(getValue(jo, "username"));
		if(usernames.contains(username)) {
			int index = usernames.indexOf(username);
			if (getValue(jo, "password").get(index).equals(Integer.toString(password))) return true;
		}
		return false;
	}
	private JSONObject newUser(String username, int password, String email) throws JSONException{
		JSONObject json = new JSONObject();
		json.put("username",username);
		json.put("password",password);
		json.put("email",email);
		json.put("isAdmin",false);
		return json;
	}
	private int getLastUserID(JSONObject jo) {
		String key;
		try {
			key = jo.names().getString(0);
			
			for(int i = 1; i<jo.names().length(); i++){
				String keyTmp =  jo.names().getString(i);		
				if(keyTmp.compareTo(key) > 0) 
					key = keyTmp;
			}
			return Integer.parseInt(key.substring(2, 4));
		}catch (JSONException e) {
			e.printStackTrace();
		}
		return -1;
	}
	public String setUser(String username, int password, String email) throws 	Exception{
		JSONObject jo =  jsonBin.getUser();
		ArrayList<String> list = new ArrayList<String>(getValue(jo, "username"));
		if(list.contains(username)) 
			return "Questo unsername è già in uso";
		list.clear();
		list = new ArrayList<String>(getValue(jo, "email", list));
		if(list.contains(email)) 
			return "Questo indirizzo email è già in uso";
		/* TODO: Insert those line of code
		if(!email.contains("@studio.unibo.it") || !email.contains("@unibo.it")) 
			return "Questo indirizzo email non è valido. Usare l'email di unibo";
		*/
		
		//Now that all possible errors are checked, we can start inserting the users data
		int lastID = getLastUserID(jo);

		String ID = "U_";
		if(lastID<10) ID += "0";
		else if(lastID == -1) return null;
		ID +=String.valueOf(lastID+1);

		JSONObject value = newUser(username, password, email);
		jo.put(ID, value);
		jsonBin.setUser(jo.toString());
		
		return null;

	}
	public void setTelegramID(String username, int TeleID){
		try {
			jo = jsonBin.getUser();
			String UserID = findUserID("username", username);
			System.out.println(UserID);
			if (UserID == null)
				System.err.println("Questo username non esiste");
			((JSONObject) jo.get(UserID)).put("idTelegram", TeleID);
			jsonBin.setUser(jo.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public String findUserID(String key, String value) {
	//NOTE: This function does not invoke a new API request, it will use the JSON file of the last invoked API request
		jo = jsonBin.getUser();
		try {
			for(int i = 0; i<jo.names().length(); i++){
				String keyTmp = jo.names().getString(i);
				if(keyTmp.contains("U_")) {
					JSONObject joTmp =  (JSONObject) jo.get(jo.names().getString(i));
					//There is only String or Integers as keys, so if there is a JSONException in getString it has to be an getInt
					try {
						if(value.equals(joTmp.getString(key))) {
							return keyTmp;
						}
					}catch(JSONException e) {
						if(value.equals(Integer.toString(joTmp.getInt(key)))){
							return keyTmp;
						}
					}
				}			
			} 
		}catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
	
