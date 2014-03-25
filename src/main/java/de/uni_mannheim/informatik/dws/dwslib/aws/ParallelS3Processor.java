package de.uni_mannheim.informatik.dws.dwslib.aws;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import de.uni_mannheim.informatik.dws.dwslib.aws.CommandTarget.CommandTargetType;
import de.uni_mannheim.informatik.dws.dwslib.framework.Processor;

public abstract class ParallelS3Processor
	extends Processor<S3File>
{
	
	private CommandTarget _target;
	private S3Credentials _credentials;
	private S3Helper s3;

	protected S3Helper getS3()
	{
		return s3;
	}
	
	/**
	 * 
	 * @param threads
	 * @param fileList if provided, contains the list of files to process
	 */
	public ParallelS3Processor(int threads, S3Credentials credentials, CommandTarget target) {
		super(threads);
		_credentials = credentials;
		_target = target;
		createS3Helper();
	}

	protected void createS3Helper()
	{
		s3 = new S3Helper(_credentials.getAwsAccessKey(), _credentials.getAwsSecret());
	}
	

	
	@Override
	protected List<S3File> fillListToProcess() {
		return _target.loadList(s3);
	}
}
