package de.uni_mannheim.informatik.dws.dwslib.aws;

public class S3File {
	private String _bucket;
	private String _key;
	
	public S3File(String bucket, String key)
	{
		set_bucket(bucket);
		set_key(key);
	}

	public String get_bucket() {
		return _bucket;
	}

	public void set_bucket(String _bucket) {
		this._bucket = _bucket;
	}

	public String get_key() {
		return _key;
	}

	public void set_key(String _key) {
		this._key = _key;
	}
	
	
}
