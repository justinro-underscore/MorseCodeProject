import java.io.IOException;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
	@FXML Button btnTransmit;
	@FXML TextArea txtOutput;
	Image redStatusImage = new Image(getClass().getResource("").toString().replace("/bin/", "/res/redStatusImage.png"));
	Image greenStatusImage = new Image(getClass().getResource("").toString().replace("/bin/", "/res/greenStatusImage.png"));
	@FXML ImageView imgTransmitStatus;

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
		initializeUI();

		stage.show();
		startRunning();
	}

	/**
	 * Initializes all aspects of the UI
	 */
	private void initializeUI()
	{
		mainPane.setDisable(true);
		mainPane.setOpacity(0.5);
		imgTransmitStatus.setImage(redStatusImage);
		initializeListeners();
	}

	/**
	 * Sets the functions that run when the user clicks on certain objects
	 */
	private void initializeListeners()
	{
		// btnTransmit begins transmitting written message
		btnTransmit.setOnAction(e ->
		{
			transmitFunction();
		});
	}

	/**
	 * Outputs whatever message is written in the output field
	 */
	private void transmitFunction()
	{
		String outputStr = txtOutput.getText().replace('\n', ' ').toLowerCase();
		if(outputStr.isEmpty())
			return;
		imgTransmitStatus.setImage(greenStatusImage);
		txtOutput.setEditable(false);
		btnTransmit.setDisable(true);
		String outputData = MorseCodeRunner.convertToMorse(outputStr);
		// Put on separate thread
		Task<Void> morseOutput = new Task<Void>()
		{
			@Override public Void call()
			{
				MorseCodeRunner.playMorse(outputData);

				// Run after playing morse code
				txtOutput.setEditable(true);
				btnTransmit.setDisable(false);
				imgTransmitStatus.setImage(redStatusImage);
				return null;
			}
		};
		new Thread(morseOutput).start();
	}

	/**
	 * Initializes the MorseCodeRunner
	 */
	private void startRunning()
	{
		Task<Void> init = new Task<Void>()
		{
			@Override public Void call()
			{
				MorseCodeRunner.programInitialize();

				// Run after program is initialized
				loadingPane.setDisable(true);
				loadingPane.setVisible(false);
				mainPane.setDisable(false);
				mainPane.setOpacity(1);
				return null;
			}
		};
		new Thread(init).start();
	}
}
