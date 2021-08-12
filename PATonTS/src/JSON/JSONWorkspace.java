package JSON;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONWorkspace {
	
	JSONBin jsonBin = new JSONBin();
	JSONObject jo = new JSONObject();
	
	//ex. key = "Name" ; value = "Sport"
	public String getWorkspaceID(String key, String value) {
		jo = jsonBin.getWorkspace();
		try {
			for(int i = 0; i<jo.names().length(); i++)
			{	JSONObject joTmp = jo.getJSONObject(jo.names().getString(i));
				
				JSONArray ja = new JSONArray();
				if(key == "ActivityID")
				{	ja = (JSONArray) joTmp.get(key);
					for(int j = 0; j<ja.length(); j++)
					{	if(ja.getString(j).equals(value))
							return jo.names().getString(i);			
					}
				}else
				{	String valueTmp = joTmp.getString(key);
					if(valueTmp.equals(value))
						return jo.names().getString(i);			
				}
			}
		} catch (JSONException e) {e.printStackTrace();}
		return null;
	}
	
	

	/** You can't find out the activities with this method
	 * Ex. workspaceID = "W_01" ; key = "Description"
	*/
	public String getWorkspaceData(String workspaceID, String key) 
	{	jo = jsonBin.getWorkspace();
		try 
		{	jo = jo.getJSONObject(workspaceID);
			return jo.getString(key);
		}catch (JSONException e) {e.printStackTrace();}
		return null;
	}

	protected ArrayList<String> getSetIDs() 
	{	jo = jsonBin.getWorkspace();
		
		ArrayList<String> id = new ArrayList<>();
		JSONArray ja = new JSONArray();
		Iterator<String> keys = jo.keys();
		
		try 
		{	while(keys.hasNext())
			{	ja = jo.getJSONObject(keys.next()).getJSONArray("ActivityID");
				for(int i=0; i<ja.length(); i++)
					id.add(ja.getString(i));
			}
		} catch (JSONException e) {e.printStackTrace();}
		return id;
	}
	public void setActivityID(String key, String value, String idAct)
	{	
		String idWork = JSONId.getID(jo, key, value);
		
		try {
			jo.getJSONObject(idWork).getJSONArray("ActivityID").put(idAct);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		jsonBin.setWorkspace(jo.toString());
	
	}
	/**
	 * @param key --> "W_ID" == workspace ID , "Name" or "Description"
	 * @return Arraylist of activities in the workspace or null
	 */
	public JSONArray getWorkspaceActivities(String key, String value)
	{	jo = jsonBin.getWorkspace();
		try
		{	for(int i = 0; i<jo.names().length(); i++)
			{	JSONObject joTmp = jo.getJSONObject(jo.names().getString(i));
				System.out.println(joTmp);
				if(key.equals("W_ID"))
				{	if(value.equals(jo.names().getString(i)))
						return joTmp.getJSONArray("ActivityID");
				}
				else 
				{	String valueTmp = joTmp.getString(key);
					System.out.println(valueTmp);
					if(valueTmp.equals(value))
						return joTmp.getJSONArray("ActivityID");
				}
			}
		} catch (JSONException e) {e.printStackTrace();}
		return null;
		
	}


	public ArrayList<String> getWorkspaceNames() {
		jo = jsonBin.getWorkspace();
		ArrayList<String> rtrn = new ArrayList<>();
		try {
			for(int i = 0; i<jo.names().length(); i++) {
				JSONObject joTmp = jo.getJSONObject(jo.names().getString(i));
				rtrn.add(joTmp.getString("Name"));
			} 
		}catch (JSONException e) {e.printStackTrace();}
		return rtrn;
	}

	public String setWorkspace(String name, String description, List<String> activityID) {
		if(name.isEmpty() || description.isEmpty())
			return "you need a value for name and description";
		
		jo = jsonBin.getWorkspace();
		JSONObject joWork = new JSONObject();
		String workID=JSONId.getValidID(jo,'W');
		try 
		{	joWork.put("Name", name);
			joWork.put("Description", description);
			joWork.put("ActivityID", activityID);
			jo.put(workID, joWork);
		} catch (JSONException e) {e.printStackTrace();}

		jsonBin.setWorkspace(jo.toString());	
		return null;
	}
	



	public void deleteWorkspace(String workspaceID) {
		 jo = jsonBin.getWorkspace();
		 jo.remove(workspaceID);
		 jsonBin.setWorkspace(workspaceID);
	}
	
}
