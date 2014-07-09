package de.dwslab.dwslib.aws;

public class S3Permission {

	public enum UserGroup
	{
		All,
		Authenticated,
		Log
	}
	
	public enum PermissionType
	{
		Read, 
		Write,
		Full
	}
	
	private UserGroup grantee;
	private PermissionType permission;
	
	public S3Permission(UserGroup grantee, PermissionType permission)
	{
		setGrantee(grantee);
		setPermission(permission);
	}
	
	public UserGroup getGrantee() {
		return grantee;
	}
	public void setGrantee(UserGroup grantee) {
		this.grantee = grantee;
	}
	public PermissionType getPermission() {
		return permission;
	}
	public void setPermission(PermissionType permission) {
		this.permission = permission;
	}
	
	
}
