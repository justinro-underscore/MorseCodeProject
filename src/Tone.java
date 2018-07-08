/**
 * Name: Justin Roderman
 * File: Tone.java
 * Date: October 29, 2017
 * Desc: Handles the tone creation
 */

public class Tone
{
	public static final int SAMPLE_RATE = 64 * 1024; // ~64KHz
	public static final double TONE_FREQUENCY = 440;
	public static final double PERIOD = SAMPLE_RATE / TONE_FREQUENCY;

	private int length;

    private byte[] sin;

    public Tone(int length)
    {
    	this.length = length;
    	sin = new byte[length * SAMPLE_RATE];

        for (int i = 0; i < sin.length; i++)
        {
            double angle = 2.0 * Math.PI * i / PERIOD;
            sin[i] = (byte)(Math.sin(angle) * 127.0);
        }
    }

    public byte[] data()
    {
        return sin;
    }

    public int getLength()
    {
    	return length;
    }
}
