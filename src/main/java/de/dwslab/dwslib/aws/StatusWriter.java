package de.dwslab.dwslib.aws;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatusWriter 
	implements Runnable
{

	private ConcurrentLinkedQueue<S3File> input;
	private boolean shouldStop;
	private BufferedWriter writer;
	protected static Logger log;
	
	public StatusWriter(ConcurrentLinkedQueue<S3File> input, String file) throws IOException
	{
		try {
			log = Logger.getLogger(getClass().getEnclosingClass()
					.getSimpleName());
		} catch (NullPointerException ne) {
			log = Logger.getLogger("CommandTarget.java");
			log.log(Level.WARNING, "Could not obtain class name");
		}
		
		this.input = input;
		shouldStop=false;
		writer = new BufferedWriter(new FileWriter(file, true));
	}

	public void stop()
	{
		shouldStop=true;
	}
	
	@Override
	public void run() {
		S3File f = null;
		
		while(!shouldStop)
		{
			f = input.poll();
			
			if(f!=null)
			{
				try {
					writer.write(f.toString() + "\n");
				} catch (IOException e) {
					log.log(Level.WARNING, e.getMessage());
				}
			} else
				try {
					writer.flush();
				} catch (IOException e) {
					log.log(Level.WARNING, e.getMessage());
				}
		}
		

		try {
			writer.close();
		} catch (IOException e) {
			log.log(Level.WARNING, e.getMessage());
		}
	}
	
}
