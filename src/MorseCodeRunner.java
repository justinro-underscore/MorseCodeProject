import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
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

public class MorseCodeRunner
{
	// For morse code speaker
	final static int DOT_LENGTH = 50; // .1 second
	final static int DASH_LENGTH = 3 * DOT_LENGTH; // .3 seconds
	final static int SPACE_BETWEEN_ELEM = DOT_LENGTH; // .1 second
	final static int SPACE_BETWEEN_LETTERS = 3 * DOT_LENGTH; // .3 seconds
	final static int SPACE_BETWEEN_WORDS = 7 * DOT_LENGTH; // .7 seconds

	public static  HashMap<Character, String> morseDictionary; // Morse Code Dictionary

	private static Tone[] tones;

	/**
	 * Populates the morseDictionary and sets up synthesizer
	 */
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

		System.out.println("Creating tones");
		tones = new Tone[2];
		tones[0] = new Tone(DOT_LENGTH);
		tones[1] = new Tone(DASH_LENGTH);
		System.out.println("Finished creating tones");
	}

	/**
	 * Converts the input to morse code
	 * @param input Input to change to morse code
	 * @return The converted string
	 */
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

	/**
	 * Converts the morse code to sound
	 * @param input Input to be converted to sound
	 */
	public static void playMorse(String input)
	{
		System.out.println("Outputting: \"" + input + "\"");
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

	/**
	 * Plays a single note
	 * Code influenced by https://stackoverflow.com/questions/2064066/does-java-have-built-in-libraries-for-audio-synthesis/2065693#2065693
	 * @param shortLength Whether or not the input is long or short
	 * @param line The audio output
	 */
	private static void playSound(boolean shortLength, SourceDataLine line)
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
	}
}
