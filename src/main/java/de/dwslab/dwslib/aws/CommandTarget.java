package de.dwslab.dwslib.aws;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jets3t.service.S3ServiceException;

public class CommandTarget {
	
	public enum CommandTargetType
	{
		List,
		Filter
	}
	
	private String fileList = "";
	private S3FileFilter fileFilter;
	private CommandTargetType targetType;
	protected static Logger log;
	
	public CommandTarget(String fileList)
	{
		try {
			log = Logger.getLogger(getClass().getEnclosingClass()
					.getSimpleName());
		} catch (NullPointerException ne) {
			log = Logger.getLogger("CommandTarget.java");
			log.log(Level.WARNING, "Could not obtain class name");
		}
		
		setFileList(fileList);
	}
	
	public CommandTarget(S3FileFilter filter)
	{
		try {
			log = Logger.getLogger(getClass().getEnclosingClass()
					.getSimpleName());
		} catch (NullPointerException ne) {
			log = Logger.getLogger("CommandTarget.java");
			log.log(Level.WARNING, "Could not obtain class name");
		}
		
		setFileFilter(filter);
	}
	
	public String getFileList() {
		return fileList;
	}
	public void setFileList(String fileList) {
		this.fileList = fileList;
		this.targetType = CommandTargetType.List;
	}
	
	public S3FileFilter getFileFilter() {
		return fileFilter;
	}
	public void setFileFilter(S3FileFilter fileFilter) {
		this.fileFilter = fileFilter;
		this.targetType = CommandTargetType.Filter;
	}
	
	public CommandTargetType getTargetType() {
		return targetType;
	}
	
	protected List<S3File> loadFileListFromFile() throws Exception
	{
		List<S3File> result = new LinkedList<S3File>();
		
		log.log(Level.INFO, "Loading file list from '" + getFileList() + "' ...");
		
		List<S3File> done = new LinkedList<S3File>();
		
		try
		{
			BufferedReader r = new BufferedReader(new FileReader(getFileList() + "_done"));
			
			String line;
			
			while((line = r.readLine()) != null)
			{	
				URL u = new URL(line.replace("s3://", "http://"));
				
				done.add(new S3File(u.getHost(), u.getPath().replaceFirst("/", "")));
			}
			
			r.close();
		}
		catch(Exception e)
		{
			log.log(Level.INFO, "Error loading completed file list: " + e.getMessage()); 
		}		
		
		long skipped=0;
		try
		{
			BufferedReader r = new BufferedReader(new FileReader(getFileList()));
			
			String line;
			
			while((line = r.readLine()) != null)
			{	
				URL u = new URL(line.replace("s3://", "http://"));
				
				S3File f = new S3File(u.getHost(), u.getPath().replaceFirst("/", ""));
				
				if(!done.contains(f))
					result.add(f);
				else
					skipped++;
			}
			
			r.close();
		}
		catch(Exception e)
		{
			log.log(Level.INFO, "Error loading file list: " + e.getMessage()); 
			
			throw new Exception("Cannot read file list!");
		}
		
		if(skipped>0)
			log.log(Level.INFO, "Skipped " + skipped + " already processed files.");
		
		return result;
	}
	
	protected List<S3File> loadFileListFromS3(S3Helper s3) throws S3ServiceException
	{
		log.log(Level.INFO, "Retrieving list of files to process ...");
		return s3.ListBucketFiles(getFileFilter().getBucketName(), getFileFilter().getPrefix());
	}
	
	public List<S3File> loadList(S3Helper s3)
	{
		try
		{
			List<S3File> lst = null;
			
			if(getTargetType()==CommandTargetType.Filter)
				lst = loadFileListFromS3(s3);
			else
				lst = loadFileListFromFile();
			
			log.log(Level.INFO, "Loaded " + lst.size() + " file names to process");
			
			return lst;
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "Failed to get list of files to process");
			return new LinkedList<S3File>();
		}
	}
}
