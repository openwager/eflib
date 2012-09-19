package com.etherfirma.api.data;

/**
 * 
 * @author crawford
 *
 */

public class MFErrorCode
{
	private 
	MFErrorCode ()
	{
		return; 
	}
	
	public static final int NONE = 0; 
	public static final int INTERNAL = 1; 
	public static final int NULL = 2; 
	public static final int NO_HANDLER = 3; 
	public static final int MALFORMED = 4; 
	public static final int NOCODEC = 5; 
	public static final int ALREADY = 6;
	public static final int NOT_FOUND = 7; 
	public static final int INVALID = 8; 
	public static final int TOO_MANY = 9; 
	
	public static final int AUTH_REQUIRED = 10; 
	public static final int AUTH_FAILED = 11; 
	public static final int AUTH_NOT_PERMITTED = 12;
	public static final int AUTH_UNVERIFIED = 13;
	public static final int AUTH_BANNED = 14;
	
	public static final int APP_NOT_FOUND = 20; 
	public static final int USER_NOT_FOUND = 21; 
	public static final int LEVEL_NOT_FOUND = 22;
	public static final int FOLDER_NOT_FOUND = 23;
	public static final int ACCOUNT_NOT_FOUND = 24;
	public static final int ITEM_NOT_FOUND = 25;
	public static final int LEVEL_SESSION_NOT_FOUND = 26;
	public static final int MP_SESSION_NOT_FOUND = 27;
	public static final int MESSAGE_NOT_FOUND = 28;
	public static final int ITEM_SET_NOT_FOUND = 29;
	
	public static final int INVALID_OPERATION_USER = 30;
	public static final int INVALID_OPERATION_STRATOS = 31;
	public static final int INVALID_OPERATION_SESSION = 32;
	public static final int INVALID_OPERATION_MESSAGE = 33;
	public static final int INVALID_OPERATION_LEVEL = 34;
	public static final int INVALID_OPERATION_PURCHASE = 35;
	
	public static final int THING_NOT_FOUND = 40;
	
	public static final int DUPLICATE_OBJECT = 50;
	public static final int DUPLICATE_LEVEL = 51;
	public static final int DUPLICATE_MESSAGE = 52;
	public static final int DUPLICATE_TAG_ENTRY = 53;
	public static final int DUPLICATE_MP_SESSION = 54;
	
	public static final int EMAIL_INVALID = 60;
	public static final int EMAIL_ALREADY_USED = 61;
	public static final int USER_INVALID = 62;
	public static final int USER_PROFANITY = 63;
	public static final int USER_ALREADY_USED = 64;
	public static final int PASSWORD_INVALID = 65;
	public static final int USER_INVALID_CHARS = 66;
}

// EOF