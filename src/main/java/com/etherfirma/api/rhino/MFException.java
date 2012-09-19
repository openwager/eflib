package com.etherfirma.api.rhino;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import com.etherfirma.api.data.MFErrorCode;


@SuppressWarnings("serial")
public class MFException extends Exception {
	
	private static final Logger logger = Logger.getLogger(MFException.class);
	
	
	public static class ErrorType {
		public final int code;
		public final String name;
		
		private ErrorType(int code, String name) {
			if (name == null) throw new IllegalArgumentException("name cannot be null");
			this.code = code;
			this.name = name;
		}
	}
	
	public static final Comparator<ErrorType> NUMERIC_SORT = new Comparator<ErrorType>() {
		@Override
		public int compare(ErrorType arg0, ErrorType arg1) {
			int diff = arg0.code - arg1.code;
			if (diff == 0) {
				diff = arg0.name.compareTo(arg1.name);
			}
			return diff;
		}
	};
	
	private static final Map<Integer, ErrorType> code2errorType;
	private static final Map<String, ErrorType> name2errorType;
	private static final ErrorType DEFAULT;
	
	static {
		code2errorType = new HashMap<Integer, ErrorType>();
		name2errorType = new HashMap<String, ErrorType>();
		
		DEFAULT = new ErrorType(-1, "UNSPECIFIED");
		code2errorType.put(DEFAULT.code, DEFAULT);
		name2errorType.put(DEFAULT.name, DEFAULT);
		
		// initialize with codes from MFErrorCode
		
		// we only want public static final int-typed fields.
		
		// oh goody! a bit field.  Welcome to 1973.
		int publicStaticFinal = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;
		for (Field f : MFErrorCode.class.getFields()) {
			if (lookupErrorType(f.getName()) != null)
			if (f.getType() != int.class) continue;
			if (f.getModifiers() != publicStaticFinal) continue;
			try {
				registerErrorType(f.getName(), f.getInt(null));
			} catch (Exception e) {
				logger.error("God hates us", e);
			}
		}
	}
	
	
	// synchronizing because this method needs to add to two different
	// maps, and maybe something weird would happen if two invocations interleaved.
	// that said this will probably only be called single threaded on startup.
	public synchronized static boolean registerErrorType(String name, int code) {
        //logger.debug("trying to register an error type " + name + " " + code, new Throwable());
		if (code < 0) {
			logger.error("Tried to register a negative code: " + code);
			return false;
		}
		if (name == null || name.length() < 1) {
			logger.error("Tried to register a null or zero-length error name: " + name);
			return false;
		}
		ErrorType et = name2errorType.get(name);
	    if (et != null) {
	        logger.error("Tried to register an error with a name already in use: " + name + ", " + code + "; existing" + et);
	        return false;
	    }
	    et = code2errorType.get(code);
	    if (et != null) {
	        logger.error("Tried to register an error with a code already in use: " + name + ", " + code + "; existing" + et);
	        return false;
	    }
	    et = new ErrorType(code, name);
	    code2errorType.put(code, et);
	    name2errorType.put(name, et);
	    return true;
	}
	
	/**
	 * return the named error type from the known set of types,
	 * or a default if it can't be found.
	 * @param name the name of the error type
	 * @return the named error type, or a default error type
	 */
	public static ErrorType lookupErrorType(String name) {

		ErrorType et = name2errorType.get(name);
		if (et == null)
			return DEFAULT;
		return et;
	}
	/**
	 * return the error type code from the known set of types,
	 * or a default if it can't be found.
	 * @param name the name of the error type
	 * @return the named error type, or a default error type
	 */
	public static ErrorType lookupErrorType(int code) {
		ErrorType et = code2errorType.get(code);
		if (et == null)
			return DEFAULT;
		return et;
	}
	
	
	public static List<ErrorType> getAllRegisteredErrorTypes() {
		List<ErrorType> ets = new ArrayList<ErrorType>(code2errorType.values());
		Collections.sort(ets, NUMERIC_SORT);
		return ets;
	}
	
	
	// instance-level stuff follows

	
	private final ErrorType errorType;
	
	public MFException(String type, String msg) {
		super(msg);
		this.errorType = lookupErrorType(type);
	}
		
	public MFException(String type, String msg, Throwable cause) {
		super(msg, cause);
		this.errorType = lookupErrorType(type);
	}
	
	public ErrorType getErrorType() {
		return this.errorType;
	}
	public int getCode() {
		return this.errorType.code;
	}
	public String getName() {
		return this.errorType.name;
	}

    public String toString() {
        return getClass().getName() + ": " + this.errorType.name + "/" + this.errorType.code + ": " + this.getLocalizedMessage();
    }
}
