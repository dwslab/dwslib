package de.dwslab.dwslib.aws;

public class S3Credentials {

	private String awsAccessKey;
	private String awsSecret;
	
	public S3Credentials(String accessKey, String secret)
	{
		setAwsAccessKey(accessKey);
		setAwsSecret(secret);
	}
	
	public String getAwsAccessKey() {
		return awsAccessKey;
	}
	public void setAwsAccessKey(String awsAccessKey) {
		this.awsAccessKey = awsAccessKey;
	}
	public String getAwsSecret() {
		return awsSecret;
	}
	public void setAwsSecret(String awsSecret) {
		this.awsSecret = awsSecret;
	}
	
	
	
}
