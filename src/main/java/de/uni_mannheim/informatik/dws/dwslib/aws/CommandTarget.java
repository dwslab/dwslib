package de.uni_mannheim.informatik.dws.dwslib.aws;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class CommandTarget {
	
	public enum CommandTargetType
	{
		List,
		Filter
	}
	
	private String fileList;
	private S3FileFilter fileFilter;
	private CommandTargetType targetType;
	
	public CommandTarget(String fileList)
	{
		setFileList(fileList);
	}
	
	public CommandTarget(S3FileFilter filter)
	{
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
		
		try
		{
			BufferedReader r = new BufferedReader(new FileReader(getFileList()));
			
			String line;
			
			while((line = r.readLine()) != null)
			{	
				URL u = new URL(line.replace("s3://", "http://"));
				
				result.add(new S3File(u.getHost(), u.getPath().replaceFirst("/", "")));
			}
			
			r.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			throw new Exception("Cannot read file list!");
		}
		
		return result;
	}
	
	protected List<S3File> loadFileListFromS3(S3Helper s3)
	{
		System.out.println("Retrieving list of files to process ...");
		return s3.ListBucketFiles(getFileFilter().getBucketName(), getFileFilter().getPrefix());
	}
	
	public List<S3File> loadList(S3Helper s3)
	{
		try
		{
			if(getTargetType()==CommandTargetType.Filter)
				return loadFileListFromS3(s3);
			else
				return loadFileListFromFile();			
		}
		catch(Exception e)
		{
			return new LinkedList<S3File>();
		}
	}
}
