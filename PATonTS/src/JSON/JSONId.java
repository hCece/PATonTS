package JSON;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONId {
	//it will return a valid id with the lowest number possible. 
	protected static String getValidID(JSONObject jo, char id) {
		ArrayList<Integer> workID = new ArrayList<>(); 
		int j=-1;
		for(int i=0; i<jo.length(); i++) 
		{	try 
			{	j = Integer.parseInt(jo.names().getString(i).split("_")[1]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			workID.add(j);
		}
		boolean hasValue=false;
		for(int i =0; i<workID.size(); i++) 
		{	for(j=1; (j-1)<workID.size(); j++) 
			{	if(j==workID.get(i)) {
					hasValue=true;
					break;
				}
			}
			if(!hasValue && i<workID.size())
			{	if(workID.size()<10)
					return id + "_0"+workID.size();
				else
				 	return id+ "_"+workID.size();
			}else
				hasValue=false;
		}
		if(workID.size()<10)
			return id + "_0"+(workID.size()+1);
		else
			return id +"_"+(workID.size()+1);
	}

	protected static String getID(JSONObject jo, String key, String value) 
	{	try 
		{	for(int i=0; i<jo.length(); i++) 
			{	JSONObject joTmp = jo.getJSONObject(jo.names().getString(i));
				System.out.println(joTmp);
				if(joTmp.get(key).equals(value))
					return jo.names().getString(i);				
			}	
		} catch (JSONException e) {e.printStackTrace();}
		return null;
	}
}
