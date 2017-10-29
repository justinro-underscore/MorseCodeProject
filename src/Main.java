import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

/*
 * Name: Justin Roderman
 * File: Main.java
 * Date: October 29, 2017
 * Desc: Simple program to receive input as string and output as morse code
 */

/*
 * TODO:
 * 		- Change from Clip to Midi for morse code output
 * 		- Create input from microphone <- This will take a while
 * 		- Don't hard code EVERYTHING
 */

public class Main
{
	// For morse code speaker
	final static int SPACE_BETWEEN_WORDS = 300; // .3 seconds
	final static int SPACE_BETWEEN_LETTERS = 200; // .2 seconds
	
	public static  HashMap<Character, String> morseDictionary; // Morse Code Dictionary
	
	public static void main(String[] args)
	{
		programInitialize(); // Must run first
		
		String in = input();
		String morse = convertToMorse(in);
		System.out.println(morse);
		playMorse(morse);
	}
	
	// Populates the morseDictionary
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
						playSound(true);
					else if(temp == '-')
						playSound(false);
				}
			}
			try { Thread.sleep(SPACE_BETWEEN_LETTERS); } // Pause for next letter
			catch (InterruptedException e) { e.printStackTrace(); }
		}
		chopper.close();
	}
	
	// Plays a note
	// Code from https://www.youtube.com/watch?v=nUKya2DvYSo
	public static void playSound(boolean shortLength)
	{
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
	}
}
