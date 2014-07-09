package de.dwslab.dwslib.util;
/**
 * Progress counter
 * @author Oliver Lehmberg
 *
 */

public class Progress {
	public long count;
	public long interval;
	public String message;
	
	public Progress(int interval, String message)
	{
		count = 0;
		this.interval = interval;
		this.message = message;
	}
	
	public void IncrementAndPrint()
	{
		count++;
		if(count % interval == 0)
			System.out.println(count + " " + message);
	}
}
