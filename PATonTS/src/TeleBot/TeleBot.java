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
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import JSON.*;

/**
 * @author Juri Horstmann 
 * This Class is using the telegram API to read, interpret and  send messages. I decided to use API instead of the given 
 * library on virtuale.unibo.it to keep a simpler non-maven project, as I am not familiar with those type of projects.
 * By reading the code the reader, familiar with Telegram bots, might ask himself why I didn't use webhooks. 
 * The reason is that the webhook would perfectly work if the program would run on a server 24/7, 
 * as Telegram worries about sending GET requests to the webhook. Yet if the program dosen't run those requests will not be listened
 * This code is able to respond to the telegram users as soon as it is running for the last 100 messages.
 * This code will also insert the Telegram ID of the user after an authentication.
 * All methods are private except start(), this is the only methods needed for external use.
 */

public class TeleBot {
	
	/**
	 * TODO: HUUUUGE!!!! lastUpdate.txt has to go to JSONBin because otherwise there is no synchronization of the responses 
	 */

	private final String URL = "https://api.telegram.org/bot1775326298:AAE0UCVXiw5uYW8sFjzYR9-vAZbMkjSq0Cs";
	public boolean loop = true;
	private Response response = new Response();

	public void start() {
		String[] message = new String[2];
		while (loop) {
			message = getMessage();
			if (message != null) {
				message = response.getResponse(message);
				sendMessage(message);
			}
			else{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/* Sends the message to the user. The array string message[] has in it's 
	 * first element the ID and in the second the response message
	 */
	protected void sendMessage(String message[]) {
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

			while ((output = in.readLine()) != null) {
				response.append(output);
			}
			in.close();
			System.out.println(response.toString());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	/* getMessage() is reading the not responded messages to the bot at once. 
	 * The return will be a String array with in it's first element the telegram user ID and in the second String the message+ 
	 */
	private String[] getMessage()  {
		HttpURLConnection con;
		try {
			URL obj = new URL(URL + "/getUpdates");
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
					int lastHandledUpdate = getLastUpdateID();
					int cnt=-1;
					int j;
					for(j = 0; j<ja.length(); j++){
						jo = ja.getJSONObject(j);
						cnt = jo.getInt("update_id");
						if(cnt == lastHandledUpdate) {
							if(j+1== ja.length()) //End Of File
						    	return null;
							else {
								jo = (JSONObject) ja.get(j+1);
								setLastUpdateID(jo.getInt("update_id"));
								String text;
								try {
							    	jo = (JSONObject) jo.get("message");
							    	text = (String) jo.get("text");
								} catch (JSONException e) {
									jo = (JSONObject) jo.get("callback_query");
									text = jo.getString("data");
								}
						    	jo = (JSONObject) jo.get("from");
								String idUser = Integer.toString(jo.getInt("id"));
						    	String[] values = {idUser, text};
						    	return values;
							}
						}
					}
					jo = ja.getJSONObject(j-1);
					setLastUpdateID(jo.getInt("update_id"));

				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return Integer.parseInt(data);
	}

}
