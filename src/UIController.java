import java.io.IOException;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
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
	Image redStatusImage = new Image(getClass().getResource("").toString().replace("/bin/", "/res/redStatusImage.png"));
	Image greenStatusImage = new Image(getClass().getResource("").toString().replace("/bin/", "/res/greenStatusImage.png"));

	// Frequency Control Panel
	@FXML Slider sldrTxFreq;
	@FXML TextField txtTxFreq;
	@FXML Slider sldrRxFreq;
	@FXML TextField txtRxFreq;
	@FXML ToggleButton btnTxRxFreqBind;

	// Transmit Panel
	@FXML Button btnTransmit;
	@FXML TextArea txtOutput;
	@FXML ImageView imgTransmitStatus;

	// Receive Panel
	@FXML Button btnReceive; // TODO May remove later
	@FXML TextArea txtInput;
	@FXML ImageView imgReceiveStatus;

	final int MIN_FREQUENCY = 60;
	final int MAX_FREQUENCY = 20000;
	int transmitFrequency = MIN_FREQUENCY;
	int receiveFrequency = MIN_FREQUENCY;
	boolean binded = false;
	boolean lastFreqReceive = false; // This determines which slider moves when binding is turned on

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

		txtTxFreq.setText("" + MIN_FREQUENCY);
		txtRxFreq.setText("" + MIN_FREQUENCY);
		imgTransmitStatus.setImage(redStatusImage);
		imgReceiveStatus.setImage(redStatusImage);
		initializeListeners();
	}

	/**
	 * Sets the functions that run when the user clicks on certain objects
	 */
	private void initializeListeners()
	{
		/* BUTTON LISTENERS */

		// btnTransmit begins transmitting written message
		btnTransmit.setOnAction(e ->
		{
			transmitFunction();
		});

		// btnReceive begins receiving on specified frequency
		btnReceive.setOnAction(e ->
		{
			receiveFunction();
		});

		// btnTxRxBind binds the transmit and receive frequencies:
		//   When one moves, so does the other
		btnTxRxFreqBind.setOnAction(e ->
		{
			binded = btnTxRxFreqBind.isSelected();
			if(binded)
			{
				if(lastFreqReceive)
					sldrTxFreq.setValue(sldrRxFreq.getValue());
				else
					sldrRxFreq.setValue(sldrTxFreq.getValue());
			}
		});

		/* SLIDER LISTENERS */

		sldrTxFreq.valueProperty().addListener((observable, oldVal, newVal) ->
		{
			lastFreqReceive = false;
			handleSliderFreqInput((int) frequencyEquation((double) newVal, false), txtTxFreq);
			if(binded)
				sldrRxFreq.setValue((double) newVal);
		});
		sldrRxFreq.valueProperty().addListener((observable, oldVal, newVal) ->
		{
			lastFreqReceive = true;
			handleSliderFreqInput((int) frequencyEquation((double) newVal, false), txtRxFreq);
			if(binded)
				sldrTxFreq.setValue((double) newVal);
		});

		/* TEXT FIELD LISTENERS */

		txtTxFreq.textProperty().addListener((observable, oldVal, newVal) ->
		{
			if(!newVal.matches("\\d+")) // Only allow numbers
				txtTxFreq.setText(newVal.replaceAll("[^\\d]", ""));
		});
		txtRxFreq.textProperty().addListener((observable, oldVal, newVal) ->
		{
			if(!newVal.matches("\\d+"))
				txtRxFreq.setText(newVal.replaceAll("[^\\d]", ""));
		});
		txtTxFreq.focusedProperty().addListener((observable, oldVal, newVal) ->
		{
			lastFreqReceive = false;
			if(!newVal)
				handleTextFreqInput(txtTxFreq.getText(), sldrTxFreq);
		});
		txtRxFreq.focusedProperty().addListener((observable, oldVal, newVal) ->
		{
			lastFreqReceive = true;
			if(!newVal)
				handleTextFreqInput(txtRxFreq.getText(), sldrRxFreq);
		});
	}

	private Number frequencyEquation(double input, boolean inverse)
	{
		double MAGIC_NUM1 = 1.2615810293952;
		double MAGIC_NUM2 = 60;
		Number result;
		if(inverse)
		{
			result = (double) (Math.log(input / MAGIC_NUM2) / Math.log(MAGIC_NUM1));
		}
		else
		{
			result = (int) (Math.round(Math.pow(MAGIC_NUM1, input)) * MAGIC_NUM2);
		}
		return result;
	}

	private void handleSliderFreqInput(int newFreq, TextField txtField)
	{
		if(txtField.getText().equals("" + newFreq))
			return;

		if(txtField.equals(txtTxFreq))
		{
			transmitFrequency = newFreq;
			txtTxFreq.setText("" + newFreq);
		}
		else
		{
			receiveFrequency = newFreq;
			txtRxFreq.setText("" + newFreq);
		}
	}

	private void handleTextFreqInput(String newFreqStr, Slider sldr)
	{
		if(("" + (int) frequencyEquation((double) sldr.getValue(), false)).equals(newFreqStr))
			return;

		if(newFreqStr.isEmpty())
			newFreqStr = "" + MIN_FREQUENCY; // So that we don't get NumberFormatException
		int newFreq = Integer.parseInt(newFreqStr);

		// Bounds check
		if(newFreq < MIN_FREQUENCY)
			newFreq = MIN_FREQUENCY;
		else if(newFreq > MAX_FREQUENCY)
			newFreq = MAX_FREQUENCY;

		double sldrValue = (double) frequencyEquation(newFreq, true);
		if(sldr.equals(sldrTxFreq))
		{
			transmitFrequency = newFreq;
			txtTxFreq.setText("" + newFreq);
		}
		else
		{
			receiveFrequency = newFreq;
			txtRxFreq.setText("" + newFreq);
		}
		sldr.setValue(sldrValue);
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
		btnReceive.setDisable(true);
		try {
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
					btnReceive.setDisable(false);
					imgTransmitStatus.setImage(redStatusImage);
					return null;
				}
			};
			new Thread(morseOutput).start();
		}
		catch(NullPointerException e)
		{
			System.out.println("\n!!! WARNING: PROGRAM NEVER INITIALIZED. ABORTING !!!");
			txtOutput.setText("\n!!! WARNING: !!!\nPROGRAM NEVER INITIALIZED\nABORTING\n!!! WARNING !!!");
			txtInput.setText("\n!!! WARNING: !!!\nPROGRAM NEVER INITIALIZED\nABORTING\n!!! WARNING !!!");
			imgTransmitStatus.setImage(redStatusImage);
		}
	}

	/**
	 * Outputs whatever message is received from the given frequency
	 */
	private void receiveFunction()
	{
		txtInput.clear();
		imgReceiveStatus.setImage(greenStatusImage);
		btnReceive.setDisable(true);
		btnTransmit.setDisable(true);
		// Put on separate thread
		Task<Void> morseInput = new Task<Void>()
		{
			@Override public Void call()
			{
				// TODO Receive message

				// Run after finished receiving morse code
				btnReceive.setDisable(false);
				btnTransmit.setDisable(false);
				imgReceiveStatus.setImage(redStatusImage);
				return null;
			}
		};
		new Thread(morseInput).start();

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
//				MorseCodeRunner.programInitialize();

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
