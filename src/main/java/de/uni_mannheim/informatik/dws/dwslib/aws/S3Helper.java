package de.uni_mannheim.informatik.dws.dwslib.aws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

public class S3Helper {
	
	private RestS3Service s3;
	private AWSCredentials awsCreds;
	protected static Logger log;
	
	public S3Helper(String accessKey, String secretKey)
	{
		try {
			log = Logger.getLogger(getClass().getEnclosingClass()
					.getSimpleName());
		} catch (NullPointerException ne) {
			log = Logger.getLogger("S3Helper.java");
			log.log(Level.WARNING, "Could not obtain class name");
		}
		
		awsCreds = new AWSCredentials(accessKey, secretKey);
	}
	
	protected RestS3Service getStorage() {
		if (s3 == null) {
			try {
				s3 = new RestS3Service(awsCreds);
			} catch (S3ServiceException e1) {
				log.log(Level.SEVERE, new Date() + "Unable to connect to S3!");
				log.log(Level.SEVERE, e1.getMessage());
			}
		}
		return s3;
	}
	
	public void setRequestPaysEnabled(boolean enabled)
	{
		getStorage().setRequesterPaysEnabled(enabled);
	}
	
	public void SaveFileToS3(String localFile, String S3FileKey,
			String S3Bucket) throws S3ServiceException, IOException {
		S3Object dataFileObject = new S3Object(localFile);
		dataFileObject.setKey(S3FileKey);
		dataFileObject.setDataInputFile(new File(localFile));
		try {
			getStorage().putObject(S3Bucket, dataFileObject);
			dataFileObject.closeDataInputStream();
		} catch (S3ServiceException e) {
			log.log(Level.WARNING, new Date() + "Error saving output to S3: " + localFile);
			log.log(Level.WARNING, e.getMessage());
			throw e;
		} catch (IOException e) {
			log.log(Level.WARNING, new Date() + "Error saving output to S3: " + localFile);
			log.log(Level.WARNING, e.getMessage());
			throw e;
		}
	}
	
	public List<String> ListBucketContents(String S3BucketName, String prefix) throws S3ServiceException
	{
		ArrayList<String> objects = new ArrayList<String>();;
		
		try {
			for(S3Object obj : getStorage().listObjects(S3BucketName, prefix, null))
			{
				objects.add(obj.getKey());
			}
		} catch (S3ServiceException e) {
			log.log(Level.WARNING, new Date() + "Error listing files");
			log.log(Level.WARNING, e.getMessage());
			throw e;
		}
		
		return objects;
	}
	
	public List<S3File> ListBucketFiles(String S3BucketName, String prefix) throws S3ServiceException
	{
		ArrayList<S3File> objects = new ArrayList<S3File>();;
		
		try {
			for(S3Object obj : getStorage().listObjects(S3BucketName, prefix, null))
			{
				objects.add(new S3File(S3BucketName, obj.getKey()));
			}
		} catch (S3ServiceException e) {
			log.log(Level.WARNING, new Date() + "Error listing files");
			log.log(Level.WARNING, e.getMessage());
			throw e;
		}
		
		return objects;
	}
	
	public void SetAcl(String S3FileKey,
			String S3Bucket, S3Permission perm) throws ServiceException
	{
		try {
			AccessControlList acl = getStorage().getObjectAcl(S3Bucket, S3FileKey);
			
			GroupGrantee grantee = null;
			switch(perm.getGrantee())
			{
			case All:
				grantee = GroupGrantee.ALL_USERS;
				break;
			case Authenticated:
				grantee = GroupGrantee.AUTHENTICATED_USERS;
				break;
			case Log:
				grantee = GroupGrantee.LOG_DELIVERY;
				break;
			}
			
			Permission p = null;
			switch(perm.getPermission())
			{
			case Read:
				p = Permission.PERMISSION_READ;
				break;
			case Write:
				p = Permission.PERMISSION_WRITE;
				break;
			case Full:
				p = Permission.PERMISSION_FULL_CONTROL;
				break;
			}
			
			acl.grantPermission(grantee, p);
			
			getStorage().putObjectAcl(S3Bucket, S3FileKey, acl);
		} catch (S3ServiceException e) {
			log.log(Level.WARNING, new Date() + "Error setting permissions");
			log.log(Level.WARNING, e.getMessage());
			throw e;
		} catch (ServiceException e) {
			log.log(Level.WARNING, new Date() + "Error setting permissions");
			log.log(Level.WARNING, e.getMessage());
			throw e;
		}
	}
	
	public void LoadFileFromS3(String localFile, String S3FileKey,
			String S3Bucket) throws IOException, ServiceException {
		File file = new File(localFile);
		S3Object inputObject;
		try {
			inputObject = getStorage().getObject(new S3Bucket(S3Bucket),
					S3FileKey);
			storeStreamToFile(inputObject.getDataInputStream(), file);
			inputObject.closeDataInputStream();
		} catch (IOException e) {
			log.log(Level.WARNING, new Date() + "Unable to save local file: " + localFile);
			log.log(Level.WARNING, e.getMessage());
			throw e;
		} catch (ServiceException e) {
			log.log(Level.WARNING, new Date() + "Unable to load file from S3: " + S3Bucket + "/"
					+ S3FileKey);
			log.log(Level.WARNING, e.getMessage());
			throw e;
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
