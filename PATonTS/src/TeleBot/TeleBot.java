package TeleBot;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import JSON.*;


public class TeleBot {

	private String HELP = ""	;
	private final String URL = "https://api.telegram.org/bot1775326298:AAE0UCVXiw5uYW8sFjzYR9-vAZbMkjSq0Cs";
	public boolean loop = true;

	public void start() {
		String[] message = new String[2];
		while(loop) {
			message = getMessage();
			if(message != null) {
				message = getResponse(message);
				sendMessage(message);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {e.printStackTrace();} 
		}
	}
	

	private String[] getResponse(String[] message) {
		JSONBin jsonBin = new JSONBin();
		JSONUser jsonUser = new JSONUser();
		ArrayList<String> list = new ArrayList<String>(jsonUser.getValue(jsonBin.getUser(), "idTelegram"));
		if(!list.contains(message[0])) {
			if(!isPossibleUser(message[0])) {
				message[1] = "Non ti conosco. Inserisci le tue credenziali con la seguente sintassi: [username];[password]";
				setPossibleUser(message[0]);
			}else {
				String[] credentials = message[1].split(";");
				boolean loginSame = jsonUser.isLoginSame(credentials[0], credentials[1].hashCode());
				if(loginSame) {
					message[1] = "Accesso riuscito. Ciao "+ credentials[0] +" :). %0A"
							+ "Ti consiglio di cancellare il tuo ultimo messaggio per la tua sicurezza. %0A";
					
						getHELP();
					message[1] = message[1] + HELP;
					jsonUser.setTelegramID(credentials[0], Integer.parseInt(message[0]));
				}else
					message[1] = "username o password sono errate. Riprova!";
			}
		}else {
			JSONPreference jsonPreference = new JSONPreference();
			if(message[1].equals("/getActivity")) 
				message[1] = getActivity(jsonUser, jsonPreference, message[0]);				
			else if(message[1].contains("/changePreference"))
				System.out.println();
			else if(message[1].contains("/setPreference"))
				System.out.println();
			else {
				getHELP();
				message[1] = HELP;
			}
		}
		return message;		
	}
	private String getActivity(JSONUser jsonUser, JSONPreference jsonPreference, String msg) {
		JSONActivity jsonActivity = new JSONActivity();
		String idUser = jsonUser.findUserID("idTelegram", msg);
		ArrayList<String> idActivity = new ArrayList<String>(jsonPreference.getActivityID(idUser));
		ArrayList<JSONObject> activities = new ArrayList<JSONObject>(jsonActivity.getValue(idActivity));
		String msgTmp = "";
		try {
			for (JSONObject joTmp : activities) {
				msgTmp +=  "*Nome:*";
				msgTmp += joTmp.getString("name") + "%0A";
				joTmp.remove("name");
				msgTmp +=  joTmp.toString();	
			}
		}catch (JSONException e) {
			e.printStackTrace();
		}
		msgTmp = stringPretty(msgTmp);
		msgTmp = msgTmp.replace("places", "*luoghi*");
		msgTmp = msgTmp.replace("weekDays", "*Giorni*");
		msgTmp = msgTmp.replace("duration", "*Durata*");
		msgTmp += "&parse_mode=Markdown";
		return msgTmp;
	}
	private String stringPretty(String str) {
		str = str.replace("\"", "");
		str = str.replace(",", "%0A");
		str = str.replace("[", "");
		str = str.replace("]", "");
		str = str.replace("{", "");
		str = str.replace(":", ":%0A");
		str = str.replace("}", "%0A%0A%0A");
		return str;
	}
	private void sendMessage(String message[]) {
		  try {
				URL obj = new URL(URL + "/sendMessage?chat_id=" + message[0] + "&text=" + message[1]);	
				HttpURLConnection con = (HttpURLConnection) obj.openConnection(); 
				// Send post request
				con.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				wr.flush();
				wr.close();
				System.out.println("Response Code : " + con.getResponseCode());
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String output;
				StringBuffer response = new StringBuffer();
				
				while ((output = in.readLine()) != null) {response.append(output);}
				in.close();
				System.out.println(response.toString());
				
		  }catch(IOException e) {
			  e.printStackTrace();
		  }catch(Exception e) {
			  e.printStackTrace();
		  }
	}	

	/* TODO: (Very important) Now the code reads only the last message, yet i have to change it
	 * such that the code reads the more then one unread message 
	 */
	private String[] getMessage()  {
		URL obj;
		try {
			obj = new URL(URL + "/getUpdates");
			HttpURLConnection con;
			con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			System.out.println("Response Code : " + con.getResponseCode());
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String output;
			StringBuffer response = new StringBuffer();		
			while ((output = in.readLine()) != null) {response.append(output);}
			in.close();
			
			JSONObject jo = new JSONObject(response.toString());
			for(int i = 0; i<jo.names().length(); i++){
				String keyTmp;
				keyTmp = jo.names().getString(i);
				String valueTmp =  jo.get(jo.names().getString(i)).toString();
				if(keyTmp.contains("result")) {
					JSONArray ja = new JSONArray(valueTmp);
					jo = (JSONObject) ja.get(ja.length()-1);
					int UpdateID = (int) jo.get("update_id");
				    if(UpdateID == getLastUpdateID())
				    	return null;
				    else {
				    	setLastUpdateID(UpdateID);
				    	jo = (JSONObject) jo.get("message");
				    	String text = (String) jo.get("text");
				    	jo = (JSONObject) jo.get("from");
				    	String idUser = Integer.toString(jo.getInt("id"));
				    	
				    	String[] values = {idUser, text};
				    	return values;
				    }
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	private void getHELP() {
		if(HELP == "") {
			try {
				File txtFile = new File("./src/TeleBot/HELP.txt");
			    Scanner reader = new Scanner(txtFile);
				if (reader.hasNextLine()) 
					HELP = HELP + reader.nextLine();
			    reader.close();
			}catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	private boolean isPossibleUser(String ID) {
		try {
			File txtFile = new File("./src/TeleBot/possibleUser.txt");
		    Scanner reader = new Scanner(txtFile);
			if (reader.hasNextLine()) {
				String line = reader.nextLine();
				System.out.println(line);
				if(line.equals(ID)) {
				    reader.close();
					return true;
				}
			}
		     reader.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	private void setPossibleUser(String ID) {
		String IDTmp = ID+"\n";
	      try {
	         Files.write(Paths.get("./src/possibleUser.txt"),IDTmp.getBytes(), StandardOpenOption.APPEND);
	      } catch(IOException e){
	         e.printStackTrace();
	      }
	}
	private void setLastUpdateID(int ID) {
		try {
	      FileWriter myWriter = new FileWriter("./src/TeleBot/lastUpdate.txt");
	      myWriter.write(Integer.toString(ID));
	      myWriter.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
		
	}
	private int getLastUpdateID() {
		String data = "-1";
		try {
			File txtFile = new File("./src/TeleBot/lastUpdate.txt");
		    Scanner reader = new Scanner(txtFile);
			if (reader.hasNextLine()) 
				data = reader.nextLine();
		     reader.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return Integer.parseInt(data);
	}

}
