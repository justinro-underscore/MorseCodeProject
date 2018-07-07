import java.io.IOException;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Name: Justin Roderman
 * File: UIController.java
 * Date: July 7, 2018
 * Desc: File that facilitates user interaction
 */

public class UIController extends Application
{
	@FXML GridPane mainPane;
	@FXML AnchorPane loadingPane;
	@FXML Button btnTest;

	/**
	 * Where the application launches from
	 * @param args What is passed in (don't worry about this)
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Where the application launches from
	 * @throws IOException If an input or output exception occurred
	 */
	@Override
	public void start(Stage arg0) throws Exception
	{
		FXMLLoader load = new FXMLLoader(getClass().getResource("MorseCodeUI.fxml")); // You may have to change the path in order to access MorseCodeUI.fxml
		load.setController(this); // Makes it so that you can control the UI using this class

		Parent root = (Parent) load.load();
		Scene scene = new Scene(root);

		// Start the application
		Stage stage = new Stage();
		stage.setTitle("Morse Code Project");
		stage.setScene(scene);

		initializeListeners(stage);
		mainPane.setDisable(true);
		mainPane.setOpacity(0.5);
		stage.show();
		startRunning();
	}

	/**
	 * Sets the functions that run when the user clicks on certain objects
	 */
	private void initializeListeners(Stage stage)
	{
		btnTest.setOnAction(e -> {
			System.out.println("Pressed");
		});
	}

	private void startRunning()
	{
		Task<Void> init = new Task<Void>()
		{
			@Override public Void call()
			{
				MorseCodeRunner.programInitialize();
				loadingPane.setDisable(true);
				loadingPane.setVisible(false);
				mainPane.setDisable(false);
				mainPane.setOpacity(1);
				return null;
			}
		};
		Thread initThread = new Thread(init);
		initThread.start();
	}

	/**
	 * http://code.makery.ch/blog/javafx-dialogs-official/
	 * @param title String representing title of dialog box
	 * @param header String representing head of dialog box
	 * @param content Content of dialog box
	 * @param type Type of dialog box
	 */
	public void showDialogBox(String title, String header, String content, AlertType type)
	{
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);

		alert.showAndWait();
	}
}
