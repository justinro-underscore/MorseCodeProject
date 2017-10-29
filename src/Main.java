import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/*
 * Name: Justin Roderman
 * File: Main.java
 * Date: October 29, 2017
 * Desc: Simple program to receive input as string and output as morse code
 */

public class Main
{
	public static  HashMap<Character, String> morseDictionary; // Morse Code Dictionary
	
	public static void main(String[] args)
	{
		programInitialize(); // Must run first
		
		String in = input();
		System.out.println(convertToMorse(in));
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
}
