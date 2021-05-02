package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import TeleBot.TeleBot;
import javafx.stage.Stage;
import javafx.scene.Scene;


public class Main extends Application implements Runnable {

	TeleBot teleBot = new TeleBot();
    boolean running = true;
	@Override	
	public void start(Stage primaryStage) throws Exception {
		/*
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
		loader.setController(new ControllerLogIn());
		*/
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/register.fxml"));
		loader.setController(new ControllerRegistration());
		Scene logInScene = loader.load();
		primaryStage.setScene(logInScene);
		primaryStage.show();
	}
	 
	public static void main(String[] args) {
		Main main = new Main();
	    Thread thread = new Thread(main);
	    thread.start();
    	launch(args);
    	main.teleBot.loop = false;

	}
	public void run() {
    	teleBot.start();	   
	}
	
}
