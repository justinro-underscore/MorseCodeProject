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
	public static  HashMap<Character, String> morseDictionary;
	
	public static void main(String[] args)
	{
		programInitialize();
		
		String in = input();
		System.out.println(convertToMorse(in));
	}
	
	public static void programInitialize()
	{
		morseDictionary = new HashMap<Character, String>();
		
		try
		{
			Scanner morseInput = new Scanner(new File("MorseDictionary.txt"));
			while(morseInput.hasNextLine())
			{
				char key = morseInput.next().charAt(0);
				String morse = morseInput.next();
				morseDictionary.put(key, morse);
			}
			morseDictionary.put(' ', " ");
			morseInput.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Error: Dictionary not found. Exiting Program...");
			System.exit(0);
		}
	}
	
	public static String input()
	{
		String inputMessage = "";
		Scanner input = new Scanner(System.in);
		System.out.print("Please input message: ");
		inputMessage = input.nextLine();
		input.close();
		
		inputMessage = inputMessage.toLowerCase();
		
		return inputMessage;
	}
	
	public static String convertToMorse(String input)
	{
		String result = "";
		for(int i = 0; i < input.length(); i++)
		{
			String morse = morseDictionary.get(input.charAt(i));
			if(morse != null)
				result += (morse + ' ');
		}
		return result;
	}
}
