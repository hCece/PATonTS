package JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class JSONActivity{
	JSONBin jsonBin = new JSONBin();
	JSONObject jo = new JSONObject();
	
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
						day = getWeekDay(time[0]);
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
	public String getWeekDay(String day) {
		try {
			File txtFile = new File("./src/JSON/week hash.txt");
		    Scanner reader = new Scanner(txtFile);
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				String[] lineArr = line.split(";");
				if(lineArr[0].equals(day)) {
				    reader.close();
					return lineArr[1];
				}
			}
		    reader.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
	
