package TeleBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import JSON.JSONActivity;
import JSON.JSONBin;
import JSON.JSONPreference;
import JSON.JSONUser;

public class Response {
	

	private String IDact = "";
	private String HELP = getHELP();
	
	protected String[] getResponse(String[] message) {
		JSONBin jsonBin = new JSONBin();
		JSONUser jsonUser = new JSONUser();
		ArrayList<String> list = new ArrayList<String>(jsonUser.getValue(jsonBin.getUser(), "idTelegram"));
		if (!isInList(list, message[0])) {
			System.out.println(isPossibleUser(message[0]));
			if (!isPossibleUser(message[0])) {
				message[1] = "Non ti conosco. Inserisci le tue credenziali con la seguente sintassi: [username];[password]";
				setPossibleUser(message[0]);
			} else {
				boolean loginSame = false;
				String[] credentials = new String[2];
				try {
					credentials = message[1].split(";");
					loginSame = jsonUser.isLoginSame(credentials[0], credentials[1].hashCode());
				}catch(ArrayIndexOutOfBoundsException e) {}
				
				if (loginSame) {
					message[1] = "Accesso riuscito. Ciao " + credentials[0] + " :). %0A"
							+ "Ti consiglio di cancellare il tuo ultimo messaggio per la tua sicurezza. %0A";
					message[1] = message[1] + HELP;
					jsonUser.setTelegramID(credentials[0], Integer.parseInt(message[0]));
					deletePossibleUser(message[0]);
				} else
					message[1] = "username o password sono errate. Riprova!";
			}
		}else{
			JSONPreference jsonPreference = new JSONPreference();
			JSONActivity jsonActivity = new JSONActivity();
			String idUser = jsonUser.findUserID("idTelegram", message[0]);
			System.out.println(message[1]);
			ArrayList<String> idActivity = new ArrayList<String>(jsonPreference.getActivityID(idUser));
			ArrayList<JSONObject> activities = new ArrayList<JSONObject>(jsonActivity.getValue(idActivity));
			if (message[1].equals("/getActivity"))
				message[1] = getActivity(activities);
			else if (message[1].contains("/deletePreference"))
				message[1] = inlineActivityName(idActivity, activities, "D");
			else if (message[1].contains("/newPreference")) {
				message[1] = inlineActivityName(idActivity, activities, "S");
			}else if(message[1].contains("A_")){
				TeleBot telebot = new TeleBot();
				String[] tmp = message[1].split(";");
				IDact = tmp[0];
				if (message[1].contains(";S")) {
					message[1] = inlineActivity(idActivity, activities, "places");
					telebot.sendMessage(message);
					message[1] = inlineActivity(idActivity, activities, "weekDays");
				}else if (message[1].contains(";D")) {
					message[1] = inlinePreference(tmp[0],idUser, "places", jsonPreference);
					telebot.sendMessage(message);
					message[1] = inlinePreference(tmp[0], idUser, "weekDays", jsonPreference);
				}

			}else if(message[1].contains(";D") || message[1].contains(";S")) {
				int i = Integer.parseInt(message[1].split(";")[0].split("_")[1]);
				if(message[1].contains("W_") && message[1].contains(";S")) 
					jsonPreference.setPreferece(IDact, idUser, "weekDays", i);
				else if(message[1].contains("P_") && message[1].contains(";S")) 
					jsonPreference.setPreferece(IDact, idUser, "places", i);
				else if(message[1].contains("W_") && message[1].contains(";D")) 
					jsonPreference.deletePreferece(IDact, idUser, "weekDays", i);
				else if(message[1].contains("P_") && message[1].contains(";D")) 
					jsonPreference.deletePreferece(IDact, idUser, "places", i);
				message[1] = "fatto :) %0A" + HELP;
			}else
				message[1] = HELP;
		}
		return message;
	}
	
	
	private boolean isInList(ArrayList<String> list, String id) {
		for(String tmp: list) {
			if(tmp.equals(id))
				return true;
		}
		return false;
	}
	
	private void deletePossibleUser(String ID){
		try {
		    File file = new File("./src/TeleBot/possibleUser.txt");
		    List<String> out = Files.lines(file.toPath())
		                        .filter(line -> !line.contains(ID))
		                        .collect(Collectors.toList());
		    Files.write(file.toPath(), out, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	

	private String inlineActivity(ArrayList<String> idActivity,ArrayList<JSONObject> activities, String key) {
		String replyMarkup = beginInlineRes(key);
		int i = 0;
		for(String IDtmp :idActivity ) {
			if(IDtmp.equals(IDact)) {
				break;
			}
			i++;
		}
		try {
			JSONArray ja = activities.get(i).getJSONArray(key);
			for(i=0; i<ja.length(); i++) {
				replyMarkup += "{\"text\":\"" + ja.get(i)  + "\",\"callback_data\":\""+
							Character.toUpperCase(key.charAt(0))+"_" + i + ";S\"},";
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return replyMarkup.substring(0, replyMarkup.length() - 1) + "]]}";
	}

	private String inlineActivityName(ArrayList<String> idActivity,ArrayList<JSONObject> activities, String type) {
		String replyMarkup="Scegli un'activity:&reply_markup={\"inline_keyboard\":[[";
		try {
			for(int i = 0; i< idActivity.size(); i++) {
					replyMarkup += "{\"text\":\"" + activities.get(i).getString("name")  + "\",\"callback_data\":\"" + idActivity.get(i) + ";"+type+"\"},";
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return replyMarkup.substring(0, replyMarkup.length() - 1) + "]]}";
	}

	/* getActivity handles the "/getActivity" request by the user. It will look in which activity the user is 
	 * signed get the jsonActivity data and format it for humans 
	 */
	private String getActivity(ArrayList<JSONObject> activities) {

		String msgTmp = "";
		try {
			for (JSONObject joTmp : activities) {
				msgTmp += "*Nome:*";
				msgTmp += joTmp.getString("name") + "%0A";
				joTmp.remove("name");
				msgTmp += joTmp.toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		msgTmp = stringPretty(msgTmp);
		msgTmp = msgTmp.replace("places", "*luoghi*");
		msgTmp = msgTmp.replace("weekDays", "*Giorni*");
		msgTmp = msgTmp.replace("duration", "*Durata*");
		msgTmp += "&parse_mode=Markdown";
		return msgTmp;
	}	
	private String beginInlineRes(String key) {
		String replyMarkup = "";
		if(key.equals("places"))
			replyMarkup="Luoghi";
		else if (key.equals("weekDays"))
			replyMarkup="Orario";
		else
			return null;
		return replyMarkup +=  ":&reply_markup={\"inline_keyboard\":[[";
	}

	
	

	private String inlinePreference(String IDact, String IDuser, String key, JSONPreference jsonPreference) {
		HashMap<String, String> elements = new HashMap<String, String>(jsonPreference.getPreference(IDact, IDuser, key));
		String replyMarkup = beginInlineRes(key);
		
		for(Entry<String, String> entry: elements.entrySet()) {
			replyMarkup += "{\"text\":\"" + entry.getValue()  + "\",\"callback_data\":\""+ 
					Character.toUpperCase(key.charAt(0))+"_" + entry.getKey() + ";D\"},";
		}
		return replyMarkup.substring(0, replyMarkup.length() - 1) + "]]}";
	}

	private boolean isPossibleUser(String ID) {
		try {
			File txtFile = new File("./src/TeleBot/possibleUser.txt");
			Scanner reader = new Scanner(txtFile);
			if (reader.hasNextLine()) {
				String line = reader.nextLine();
				if (line.equals(ID)) {
					reader.close();
					return true;
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void setPossibleUser(String ID) {
		String IDTmp = ID + "\n";
		try {
			Files.write(Paths.get("./src/TeleBot/possibleUser.txt"), IDTmp.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	//reads the first time the method is called the file HELP.txt and puts it on the String HELP
	private String getHELP() {
		String tmp = "";
		try {
			File txtFile = new File("./src/TeleBot/HELP.txt");
			Scanner reader = new Scanner(txtFile);
			if (reader.hasNextLine())
				tmp = tmp + reader.nextLine();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return tmp;
	}
	
	
}
