package de.uni_mannheim.informatik.dws.dwslib.aws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

public class S3Helper {
	
	private RestS3Service s3;
	private AWSCredentials awsCreds;
	
	public S3Helper(String accessKey, String secretKey)
	{
		awsCreds = new AWSCredentials(accessKey, secretKey);
	}
	
	protected RestS3Service getStorage() {
		if (s3 == null) {
			try {
				s3 = new RestS3Service(awsCreds);
			} catch (S3ServiceException e1) {
				System.out.println("Unable to connect to S3!");
				e1.printStackTrace();
			}
		}
		return s3;
	}
	
	public void SaveFileToS3(String localFile, String S3FileKey,
			String S3Bucket) {
		S3Object dataFileObject = new S3Object(localFile);
		dataFileObject.setKey(S3FileKey);
		dataFileObject.setDataInputFile(new File(localFile));
		try {
			getStorage().putObject(S3Bucket, dataFileObject);
			dataFileObject.closeDataInputStream();
		} catch (S3ServiceException e) {
			System.out.println("Error saving output to S3: " + localFile);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error saving output to S3: " + localFile);
			e.printStackTrace();
		}
	}
	
	public List<String> ListBucketContents(String S3BucketName, String prefix)
	{
		ArrayList<String> objects = new ArrayList<String>();;
		
		try {
			for(S3Object obj : getStorage().listObjects(S3BucketName, prefix, null))
			{
				objects.add(obj.getKey());
			}
		} catch (S3ServiceException e) {
			e.printStackTrace();
		}
		
		return objects;
	}
	
	public void LoadFileFromS3(String localFile, String S3FileKey,
			String S3Bucket) {
		File file = new File(localFile);
		S3Object inputObject;
		try {
			inputObject = getStorage().getObject(new S3Bucket(S3Bucket),
					S3FileKey);
			storeStreamToFile(inputObject.getDataInputStream(), file);
			inputObject.closeDataInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to save local file: " + localFile);
		} catch (ServiceException e) {
			e.printStackTrace();
			System.out.println("Unable to load file from S3: " + S3Bucket + "/"
					+ S3FileKey);
		}
	}
	
	protected void storeStreamToFile(InputStream in, File outFile)
			throws IOException {
		OutputStream out = new FileOutputStream(outFile);
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}
		out.close();
	}
}
