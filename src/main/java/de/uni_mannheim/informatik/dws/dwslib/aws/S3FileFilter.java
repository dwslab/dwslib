package de.uni_mannheim.informatik.dws.dwslib.aws;

public class S3FileFilter {
	private String bucketName;
	private String prefix;
	
	public S3FileFilter(String bucket, String keyPrefix)
	{
		setBucketName(bucket);
		setPrefix(keyPrefix);
	}
	
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	
}
