package de.dwslab.dwslib.aws;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.dwslab.dwslib.framework.Processor;
import de.dwslab.dwslib.util.io.InputUtil;

public class ParallelS3Uploader extends Processor<String> {

	private String s3Bucket;
	private File fileList;
	private S3Credentials credentials;

	public ParallelS3Uploader(int numThreads, String credFile, String s3Bucket,
			String fileList) throws Exception {
		super(numThreads);
		readCredFromFile(credFile);
		createS3Helper();
		this.fileList = new File(fileList);
		if (this.fileList.isDirectory()) {
			throw new Exception("List of files is not a file.");
		}

		this.s3Bucket = s3Bucket;

		if (this.s3Bucket == null) {
			throw new Exception("S3 Bucket cannot be null!");
		}
	}

	protected void readCredFromFile(String file) throws IOException {
		BufferedReader br = InputUtil.getBufferedReader(new File(file));
		// file needs to have 2 lines at least
		// first line is skipped
		br.readLine();
		String credLine = br.readLine();
		String tok[] = credLine.split(",");
		// create credentials and remove leading and closing "
		credentials = new S3Credentials(tok[1].replace("\"", ""),
				tok[2].replace("\"", ""));

		System.out.println("Credentials are: " + credentials.getAwsAccessKey()
				+ " / " + credentials.getAwsSecret());
	}

	private S3Helper s3;

	protected S3Helper getS3() {
		return s3;
	}

	protected void createS3Helper() {
		s3 = new S3Helper(credentials.getAwsAccessKey(),
				credentials.getAwsSecret());
	}

	@Override
	protected List<String> fillListToProcess() {
		List<String> files = new ArrayList<String>();

		try {
			BufferedReader br = InputUtil.getBufferedReader(fileList);
			while (br.ready()) {
				files.add(br.readLine().trim());
			}
			br.close();
		} catch (IOException e) {
			System.out.println("Could not read files from input file list.");
			e.printStackTrace();
		}

		return files;
	}

	@Override
	protected void process(String object) throws Exception {
		File f = new File(object);
		if (f.isDirectory()) {
			System.out.println("File " + object + " is a directory. Skipping.");
			return;
		}
		getS3().SaveFileToS3(f.getAbsolutePath(), object, s3Bucket);

	}

	public static void main(String[] args) throws NumberFormatException,
			Exception {
		if (args == null || args.length != 4) {
			System.out
					.println("USAGE: ParallelS3Uploader [FILESTOUPLOADFILE] [BUCKETTOUPLOADTO] [AWSCREDENTIALFILE] [NUMBEROFTHREADS]");
		} else {
			ParallelS3Uploader p = new ParallelS3Uploader(
					Integer.parseInt(args[3]), args[2], args[1], args[0]);
			p.process();
		}
	}

}
