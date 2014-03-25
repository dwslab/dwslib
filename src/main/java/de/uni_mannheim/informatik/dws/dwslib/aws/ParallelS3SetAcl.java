package de.uni_mannheim.informatik.dws.dwslib.aws;

import java.io.IOException;

public class ParallelS3SetAcl
	extends ParallelS3Processor
{

	private S3Permission _permission;
	
	public ParallelS3SetAcl(int threads, S3Credentials credentials,
			CommandTarget target, S3Permission permission) throws IOException {
		super(threads, credentials, target);
		_permission = permission;
	}

	@Override
	protected void process(S3File object) throws Exception {
		getS3().SetAcl(object.get_key(), object.get_bucket(), _permission);
		setDone(object);
	}

	
	
}
