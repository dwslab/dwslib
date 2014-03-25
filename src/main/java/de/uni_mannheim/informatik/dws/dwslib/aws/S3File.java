package de.uni_mannheim.informatik.dws.dwslib.aws;

import java.net.URL;

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
	
	@Override
	public String toString() {
		return "s3://" + get_bucket() + "/" + get_key();
	}

	@Override
	public boolean equals(Object obj) {
		try {
			S3File f = (S3File)obj;
			
			return f.get_bucket().equals(get_bucket()) && f.get_key().equals(get_key());
		} catch (Exception e) {
			return super.equals(obj);
		}
	}
	
}
