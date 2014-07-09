package de.dwslab.dwslib.aws;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import de.dwslab.dwslib.framework.Processor;

public abstract class ParallelS3Processor
	extends Processor<S3File>
{
	
	private CommandTarget _target;
	private S3Credentials _credentials;
	private S3Helper s3;
	private ConcurrentLinkedQueue<S3File> _done;
	private Thread _statusWriterThread;
	private StatusWriter _statusWriter;
	
	protected S3Helper getS3()
	{
		return s3;
	}
	
	/**
	 * 
	 * @param threads
	 * @param fileList if provided, contains the list of files to process
	 * @throws IOException 
	 */
	public ParallelS3Processor(int threads, S3Credentials credentials, CommandTarget target) throws IOException {
		super(threads);
		_credentials = credentials;
		_target = target;
		_done = new ConcurrentLinkedQueue<S3File>();
		_statusWriter = new StatusWriter(_done, target.getFileList() + "_done");
		_statusWriterThread = new Thread(_statusWriter);
		createS3Helper();
	}

	protected void createS3Helper()
	{
		s3 = new S3Helper(_credentials.getAwsAccessKey(), _credentials.getAwsSecret());
	}
	
	protected void setDone(S3File file)
	{
		_done.add(file);
	}
	
	@Override
	protected void beforeProcess() {
		_statusWriterThread.start();
	}
	
	@Override
	protected void afterProcess() {
		try {
			_statusWriter.stop();
			_statusWriterThread.join();
		} catch (InterruptedException e) {
			log.log(Level.WARNING, e.getMessage());
		}
	}
	
	@Override
	protected List<S3File> fillListToProcess() {
		return _target.loadList(s3);
	}
}
