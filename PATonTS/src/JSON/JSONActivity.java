package JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class JSONActivity{
	JSONBin jsonBin = new JSONBin();
	JSONObject jo = new JSONObject();
	//it will return the raw jsonObjects of the ID's sent.
	public ArrayList<JSONObject> getValue(ArrayList<String> ID)  {
		try {
			ArrayList<JSONObject> arrJo = new ArrayList<JSONObject>();
			JSONObject joTmp = new JSONObject();
			JSONArray jaOld = new JSONArray(); //JSONArray of JSONObject jo
			
			jo = jsonBin.getActivity();
			joTmp = jo;
			Iterator<String> keys = jo.keys();
			while(keys.hasNext()) {
				String key = keys.next();
				if(ID.contains(key)) {
					jaOld = jo.getJSONObject(key).getJSONArray("weekDays");
					JSONArray jaNew = new JSONArray();
					for(int i = 0; i<jaOld.length();i++){
						String day =(String) jaOld.get(i);
						String[] time = day.split(";");
						day = getWeekDay(Integer.parseInt(time[0]));
						jaNew.put(day+ " " + time[1]);
					}
					joTmp.getJSONObject(key).put("weekDays", jaNew);
					arrJo.add(joTmp.getJSONObject(key));
				}
			}
			return arrJo;
		}catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	/* In JSONbin the weekdays are not saved in their full name, rather with a integer key that is linked to the value.
	 *	This method recalls the key to return the value (key=0;value=monday)
	 */
	public int getDayID(String day) 
	{	switch(day)
		{	case("Monday"): return 0;
			case("Tuesday"): return 1;
			case("Wednesday"): return 2;
			case("Thursday"): return 3;
			case("Friday"): return 4;
			case("Saturday"): return 5;
			case("Sunday"): return 6;
		}
		return -1;
	}
	
	public String getWeekDay(int day) 
	{	switch(day)
		{	case(0): return "Monday";
			case(1): return "Tuesday";
			case(2): return "Wednesday";
			case(3): return "Thursday";
			case(4): return "Friday";
			case(5): return "Saturday";
			case(6): return "Sunday";
		}
		return null;
	}
	
	
	//it returns the position in the array of weekdays or place
	public int getPositionPreference(String IDact, String key, String value) 
	{	try 
		{	JSONArray ja = new JSONArray();
			ja = jo.getJSONObject(IDact).getJSONArray(key);
			
			for(int i=0; i<ja.length(); i++) 
			{	if(ja.getString(i).equals(value))
					return i;				
			}
		} catch (JSONException e) {e.printStackTrace();}
		
		return -1;
	
	}
	

	
	public String getID(String key, String value) 
	{	jo = jsonBin.getActivity();
		return JSONId.getID(jo, key, value);
	}
	
	public String replacePreferences(String IDAct,
			String[] places, String[] weekDays) 
	{	try {
		
			JSONArray ja = new JSONArray();
			
			jo.getJSONObject(IDAct).remove("places");
			jo.getJSONObject(IDAct).put("places", places);
			

			jo.getJSONObject(IDAct).remove("weekDays");
			int i=0;
			for(String str:weekDays)
			{	String [] tmp = str.split(" ");
				weekDays[i]=getDayID(tmp[0])+";"+tmp[1];
				i++;
			}
			jo.getJSONObject(IDAct).put("weekDays", weekDays);
		} catch (JSONException e) {e.printStackTrace();}
		jsonBin.setActivity(jo.toString());	
		
		return null;
	}
	
	

	public String setActivity(String name, String duration, 
		List<String> places, List<String> weekDays) 
	{	if(name.isEmpty() || duration.isEmpty() || 
				places.isEmpty() || weekDays.isEmpty())
			return "you need a value for every variable";
		jo = jsonBin.getActivity();
		JSONObject joAct = new JSONObject();
		String actID=JSONId.getValidID(jo, 'A');
		try 
		{	joAct.put("name", name);
			joAct.put("duration", duration);
			joAct.put("places", places);
			joAct.put("weekDays", weekDays);
			jo.put(actID, joAct);
		} catch (JSONException e) {e.printStackTrace();}
		System.out.println(jo.toString());
		jsonBin.setActivity(jo.toString());		
		return actID;
	}
	
	public ArrayList<String> getActivitiesWithoutWorkspace(){
		JSONWorkspace jsonWorkspace = new JSONWorkspace();
		
		ArrayList<String> setActID = new ArrayList<>();
		ArrayList<String> freeActID = new ArrayList<>();
		ArrayList<JSONObject> freeAct = new ArrayList<>();
		setActID = jsonWorkspace.getSetIDs();
		jo = jsonBin.getActivity();
		
		Iterator<String> keys = jo.keys();
		String str="";
		while(keys.hasNext())
		{	str = keys.next();
			if(!setActID.contains(str))
				freeActID.add(str);
			
		}
		freeAct = getValue(freeActID);
		freeActID.clear();
		for(JSONObject joTmp: freeAct) 
		{	try {
				freeActID.add(joTmp.getString("name"));
			} catch (JSONException e) {e.printStackTrace();}
		}
		return freeActID;
	}

}
	
