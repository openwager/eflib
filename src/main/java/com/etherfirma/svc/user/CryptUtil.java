package com.etherfirma.svc.user;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.weaselworks.util.HexUtil;

/**
 * 
 * @author crawford
 *
 */

public class CryptUtil
{
	private static final Logger logger = Logger.getLogger (CryptUtil.class); 
	
	private
	CryptUtil ()
	{
		return;
	}

	/**
	 * 
	 * @param strings
	 * @return
	 * @throws Exception
	 */
	
	public static
	String md5 (final String [] strings)
		throws Exception
	{
		try { 
			final MessageDigest md = MessageDigest.getInstance ("MD5"); 
			for (final String string  : strings) { 
				md.update (string.getBytes ()); 
			}
			final byte [] hash = md.digest ();
			final byte [] encoded = Base64.encodeBase64 (hash);
			return new String (encoded); 
			 
		}
		catch (final Exception e) { 
			throw e;  
		}
		
		// NOT REACHED
	}
	
	public static
	String md5(final String string)
	{
		MessageDigest md;
		try {
			md = MessageDigest.getInstance ("MD5");

		md.update(string.getBytes());
		final byte [] hash = md.digest ();
		final String encoded = HexUtil.encodeBytes (hash);
		String messageDigest = encoded.toLowerCase ();
		return messageDigest;
		}
		catch (NoSuchAlgorithmException e) {
			return "FAIL";
		} 
	}
	
	public static byte[] base64UrlDecode(String str){
		str = str.replaceAll("\\-", "_");
		str = str.replaceAll("\\+", "/");
		return Base64.decodeBase64(str);
	}
	
	/**
	 * 
	 * @param signedRequest string of the form [SIGNATURE].[PAYLOAD] PAYLOAD is Base64 encoded
	 * @param secret our facebook secret key
	 * @return JSONObject contained in the payload
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws JSONException
	 */
	public static JSONObject parseFBSignedRequest(String signedRequest, String secret) throws InvalidKeyException, NoSuchAlgorithmException, JSONException {
		String[] strs = signedRequest.split("\\.");
		byte[] sig = base64UrlDecode(strs[0]);
		byte[] payload = strs[1].getBytes();
		
		SecretKey secretKey = null;
	    byte[] keyBytes = secret.getBytes();	    
	    secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
	    Mac mac = Mac.getInstance("HmacSHA256");
	    mac.init(secretKey);

	    boolean validates = Arrays.equals(mac.doFinal(payload), sig);
	    if (! validates)
	    {
	    	logger.warn("Invalid Facebook Signature");
	    	return null;
	    }
	    return new JSONObject(new String(base64UrlDecode(strs[1])));
	}
}

// EOF