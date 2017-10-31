import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.ReverbType;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/*
 * Name: Justin Roderman
 * File: Main.java
 * Date: October 29, 2017
 * Desc: Simple program to receive input as string and output as morse code
 */

/*
 * TODO:
 * 		- Create input from microphone <- This will take a while
 * 		- Don't hard code EVERYTHING
 */

public class Main
{
	// For morse code speaker
	final static int DOT_LENGTH = 100; // .1 second
	final static int DASH_LENGTH = 3 * DOT_LENGTH; // .3 seconds
	final static int SPACE_BETWEEN_ELEM = DOT_LENGTH; // .1 second
	final static int SPACE_BETWEEN_LETTERS = 3 * DOT_LENGTH; // .3 seconds
	final static int SPACE_BETWEEN_WORDS = 7 * DOT_LENGTH; // .7 seconds
	
	public static  HashMap<Character, String> morseDictionary; // Morse Code Dictionary

	public static Synthesizer synth;
	
	private static Tone[] tones;
	
	public static void main(String[] args)
	{
		programInitialize(); // Must run first
		
		String in = input();
		String morse = convertToMorse(in);
		System.out.println(morse);
		playMorse(morse);

//        synth.close(); // Close the synthesizer
	}
	
	// Populates the morseDictionary and sets up synthesizer
	public static void programInitialize()
	{
		morseDictionary = new HashMap<Character, String>();
		
		// Populate the dictionary
		try
		{
			Scanner morseInput = new Scanner(new File("MorseDictionary.txt"));
			while(morseInput.hasNextLine())
			{
				char key = morseInput.next().charAt(0);
				String morse = morseInput.next();
				morseDictionary.put(key, morse);
			}
			morseDictionary.put(' ', "|"); // Spaces are denoted by pipes
			morseInput.close();
		}
		catch (FileNotFoundException e) // Run if dictionary is not in files
		{
			System.out.println("Error: Dictionary not found. Exiting Program...");
			System.exit(0);
		}
		
		/*
		// Set up synthesizer
		try
		{
			synth = MidiSystem.getSynthesizer();
		    synth.open();
		}
		catch (MidiUnavailableException e) { e.printStackTrace(); }
		*/
		
		System.out.println("Creating tones");
		tones = new Tone[2];
		tones[0] = new Tone(DOT_LENGTH);
		tones[1] = new Tone(DASH_LENGTH);
		System.out.println("Finished creating tones");
	}
	
	// Simple user input
	public static String input()
	{
		// Receive input
		String inputMessage = "";
		Scanner input = new Scanner(System.in);
		System.out.print("Please input message: ");
		inputMessage = input.nextLine();
		input.close();
		
		inputMessage = inputMessage.toLowerCase(); // Makes all characters lowercase
		
		return inputMessage;
	}
	
	// Converts the input to morse code
	public static String convertToMorse(String input)
	{
		String result = "";
		for(int i = 0; i < input.length(); i++)
		{
			char key = input.charAt(i);
			if(morseDictionary.containsKey(key))
				result += (morseDictionary.get(key) + ' ');
		}
		return result;
	}
	
	// Converts the morse code to sound
	public static void playMorse(String input)
	{
		final AudioFormat af = new AudioFormat(Tone.SAMPLE_RATE, 8, 1, true, true); // AudioFormat
        SourceDataLine line = null; // Create the Data Line
		try
		{
			line = AudioSystem.getSourceDataLine(af);
	        line.open(af, Tone.SAMPLE_RATE); // Open the data line
		}
		catch (LineUnavailableException e) { e.printStackTrace(); }
        line.start(); // Start the line
		
		Scanner chopper = new Scanner(input); // Go through the input
		String letter; // Each morse code letter
		while(chopper.hasNext())
		{
			letter = chopper.next();
			if(letter == "|") // If it's a space, wait for a bit
			{
				try { Thread.sleep(SPACE_BETWEEN_WORDS); }
				catch (InterruptedException e) { e.printStackTrace(); }
			}
			else // If a letter, go through the letter
			{
				for(int i = 0; i < letter.length(); i++)
				{
					char temp = letter.charAt(i);
					// Determines whether the char is a short or long beep
					if(temp == '.')
						playSound(true, line);
					else if(temp == '-')
						playSound(false, line);
					try { Thread.sleep(SPACE_BETWEEN_ELEM); } // Pause for next element
					catch (InterruptedException e) { e.printStackTrace(); }
				}
			}
			try { Thread.sleep(SPACE_BETWEEN_LETTERS); } // Pause for next letter
			catch (InterruptedException e) { e.printStackTrace(); }
		}
		chopper.close();
	}
	
	// Plays a note
	// Code influenced by https://stackoverflow.com/questions/2064066/does-java-have-built-in-libraries-for-audio-synthesis/2065693#2065693
	public static void playSound(boolean shortLength, SourceDataLine line)
	{
    	Tone t; // Tone to be played (short or long)
    	if(shortLength)
    		t = tones[0];
    	else
    		t = tones[1];
    	line.write(t.data(), 0, Tone.SAMPLE_RATE * t.getLength() / 1000); // Writes the data
        try { Thread.sleep(t.getLength()); } // Sleep for the length of the tone
        catch (InterruptedException e) { e.printStackTrace(); }
    	
        line.drain(); // Drain the line
		
		
		
		
		
		/*
		try
		{
//			synth.open();
			MidiChannel[] mc = synth.getChannels(); // Creates the synthesizer
	        mc[0].programChange(6); // Sets the instrument
	        
	        // Plays the note
	        mc[0].noteOn(90, 150);
	        Thread.sleep(noteLength);
	        mc[0].noteOff(90, 600);
//	        synth.close(); // Close the synthesizer
		}
		catch (InterruptedException e) { e.printStackTrace(); }
//		catch (MidiUnavailableException e) { e.printStackTrace(); }
		*/
		
		
		
		
		
		/*
		Mixer.Info[] mixInfos = AudioSystem.getMixerInfo();
		
		Mixer mixer = AudioSystem.getMixer(mixInfos[0]);
		
		Clip clip = null;
		DataLine.Info dataInfo = new DataLine.Info(Clip.class, null);
		try { clip = (Clip) mixer.getLine(dataInfo); }
		catch(LineUnavailableException e) { e.printStackTrace(); }
		
		try
		{
			String path = "file:/C:/Users/jcrod/Desktop/Programming/Java/MorseCodeProgram/"; // Okay okay I know I shouldn't hard code this but I'm out of time
			if(shortLength)
				path += "BeepShort.wav";
			else
				path += "BeepLong.wav";
			URL soundURL = new URL(path);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);
			clip.open(audioStream);
		}
		catch(LineUnavailableException lue) { lue.printStackTrace(); }
		catch(UnsupportedAudioFileException uafe) { uafe.printStackTrace(); }
		catch(IOException ioe) { ioe.printStackTrace(); }
		catch(Exception e)
		{
			System.out.println("Error: Beep.wav not found\nExiting Program...");
			System.exit(0);
		}
		
		clip.start();
		
		do
		{
			try { Thread.sleep(50); }
			catch(InterruptedException ie) { ie.printStackTrace(); }
		} while(clip.isActive());
		*/
	}
}
