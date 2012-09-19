package com.etherfirma.util.settings;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.*;
import org.json.*;

import com.weaselworks.io.*;
import com.weaselworks.util.*;

/**
 * 
 * @author crawford
 *
 */

public class Settings
{
	protected static final Logger logger = Logger.getLogger (Settings.class); 
	
	public
	Settings ()
	{
		this (new HashMap<String, Object> ()); 
		return; 
	}

	public
	Settings (final Map<String, Object> map)
	{
		setProperties (map); 
		return; 
	}
	
	protected boolean debug; 
	public boolean getDebug () { return this.debug; } 
	public void setDebug (final boolean debug) { this.debug = debug; return; } 
	
	public
	void dump ()
	{
		dump (getProperties (), 0); 
		return; 
	}
	
	@SuppressWarnings ("unchecked")
	public
	void dump (final Map<String, Object> map, final int indent)
	{
		final String a = StringUtil.repeat ("  ", indent); 
		final String b = StringUtil.repeat ("  ", indent + 1); 
		System.err.println ("{"); 
		for (final Map.Entry<String, Object> entry : map.entrySet ()) { 
			final String key = entry.getKey (); 
			final Object val = entry.getValue (); 
			System.err.print (b + key + ": "); 
			if (val instanceof Map) { 
				dump ((Map<String,Object>) val, indent + 1); 
			} else if (val instanceof String) { 
				System.err.println ("(String) \"" + val + "\"" + ",");
			} else { 
				System.err.println ("(" + val.getClass().getSimpleName () +") " + val + ","); 
			}
		}
		System.err.println (a + "}");
		return; 
	}
	
//	public static
//	void main (final String [] args)
//		throws Exception
//	{
//		final Settings s = new Settings ();
//		final File file = new File ("/Users/crawford/Workspace/hot-ticket/api/src/main/settings/hot-ticket-localhost/mongo.json"); 
//		s.merge (file); 
//		s.dump (); 
//		return; 
//	}
	
	@SuppressWarnings ("unchecked")
	public <T>
	T lookup (final String path, final Class <? extends T> type)
	{ 
		return (T) lookup (path); 
	}

	@SuppressWarnings ("unchecked")
	public <T>
	T lookup (final String path, final Class <? extends T> type, final boolean required)
		throws Exception
	{ 
		final T value = (T) lookup (path); 
		if (value == null) { 
			if (required) { 
				throw new Exception ("Missing setting: " + path); 
			}
		}
		return value; 
	}

	public
	Object lookup (final String path, final boolean required)
		throws Exception
	{
		final Object value = lookup (path);
		if (value == null) { 
			if (required) { 
				throw new Exception ("Missing setting: " + path); 
			}
		}
		return value; 
	}
	
	public
	Object lookup (final String path)
	{
		Object obj = null; 
		Map<String, Object> map = props;
		final String [] strs = path.split ("\\.");
		final int len = strs.length; 
		
		for (int i = 0; i < len; i ++) {
			final String key = strs [i]; 
			obj = map.get (key); 
			if (obj == null) { 
				break; 
			} else if (i == (len - 1)) {
				break; 
			} else if (! (obj instanceof Map)) {
				throw new IllegalStateException ("Node '" + key + "' not a map: " + path); 
			} else { 
				map = (Map<String, Object>) obj; 
			}
		}
		
		return obj; 
	}
	
	public
	Settings extract (final String path)
	{
		final Object obj = lookup (path); 
		if (obj == null) { 
			return null; 
		} else { 
			final Map<String, Object> map = (Map<String, Object>) obj; 
			final Settings settings = new Settings (map); 
			return settings;
		}
		// NOT REACHED
	}
	
	protected Map<String, Object> props; 
	public Map<String, Object> getProperties () { return this.props; } 
	public void setProperties (final Map<String, Object> props) { this.props = props; return; } 
	public boolean hasProperty (final String key) { return this.props.containsKey (key); } 
	public void setProperty (final String key, final Object val) { this.props.put (key, val); return; } 
	
	public 
	Map<String, Object> getChild (final String key, final boolean create)
		throws IllegalStateException
	{
		return getChild (new String [] { key }, create); 
	}
	
	public
	Map<String, Object> getChild (final String [] keys, final boolean create)
		throws IllegalStateException
	{
		Map<String, Object> props = this.props; 
		for (final String key : keys) { 
				Object child = props.get (key); 
				if (child == null) { 
					if (create) {
						child = new HashMap<String, Object> (); 
						props.put (key, child); 
					} else {
						return null; 
					}
				} else if (! (child instanceof Map)) { 
					throw new IllegalStateException ("Child not a map: " + child.getClass ().getSimpleName ()); 
				}
				props = (Map<String, Object>) child; 
		}
		return props; 
	}
	
	public
	void clear ()
	{
		props.clear (); 
		return; 
	}
	
	public
	void merge (final File path)
		throws IOException, JSONException
	{
		if (path.isDirectory ()) { 
			for (final File file : path.listFiles ()) {
				final String name = file.getName ().toLowerCase (); 
				if (! name.startsWith (".")) { 
					if (file.isDirectory () || name.endsWith (".json")) {
						merge (file);
					} else { 
						logger.info ("Ignoring file: " + file.getName ()); 
					}
				}
			}    		
		} else { 
			final String str = IOUtil.readFile (path); 
			final JSONObject obj = new JSONObject (str); 
			merge (obj);
			logger.info ("Loaded settings from: " + path); 
		}

		return; 
	}
	
	public
	void merge (final URL url)
		throws IOException, JSONException
	{
		final InputStream is = url.openStream (); 
		final String str = IOUtil.readFully (is); 
		final JSONObject obj = new JSONObject (str); 
		merge (obj); 
		return;
	}
	
	public
	void mergeResource (final String path)
		throws IOException, JSONException
	{
		final ClassLoader cl = Thread.currentThread ().getContextClassLoader (); 
		final InputStream is = cl.getResourceAsStream (path);
		if (is == null) { 
			throw new IOException ("Resource not found: " + path); 
		}
		final String str = IOUtil.readFully (is); 
		final JSONObject obj = new JSONObject (str); 
		merge (obj); 
	}
	
	public
	void merge (final JSONObject json) 
		throws JSONException 
	{
		final Map<String, Object> map = parse (json); 
		merge (map); 
		return; 
	}
	
	protected
	Map<String, Object> parse (final JSONObject json) 
		throws JSONException
	{
		final Map<String, Object> map = new HashMap<String, Object> (); 
		final Iterator<String> iter = (Iterator<String>) json.keys ();
		while (iter.hasNext ()) { 
			final String key = iter.next (); 
			final Object obj = json.get (key);
			if (obj instanceof JSONObject) {
				map.put (key, parse ((JSONObject) obj));
			} else if (obj instanceof JSONArray) { 
				throw new IllegalArgumentException ("Arrays not supported."); 
			} else { 
				map.put (key, obj); 
			}
		}
		return map; 
	}
	
	public
	void merge (final Settings from)
	{
		merge (from.getProperties ()); 
		return; 
	}
	
	public
	void merge (final Map<String, Object> merge)
	{
		merge (merge, this.props); 
		return; 
	}
	
	protected
	void merge (final Map<String, Object> from, final Map<String, Object> into)
	{
		for (final Map.Entry<String, Object> entry : from.entrySet ()) { 
			final String key = entry.getKey (); 
			final Object value = entry.getValue (); 
			if (value instanceof Map) { 
				final Map<String, Object> from2 = (Map<String, Object>) value;
				final Map<String, Object> into2 = getChild (key, into, true); 
				merge (from2, into2); 
			} else { 
				into.put (key, value); 
			}
		}
	}
	
	protected
	Map<String, Object> getChild (final String key, final Map<String, Object> map, final boolean create)
		throws IllegalStateException
	{
		final Object obj = map.get (key); 
		if (obj == null) {
			if (create) { 
				final Map<String, Object> child = new HashMap<String, Object> (); 
				map.put (key, child); 
				return child; 
			} else { 
				return null; 
			}
		} else if (! (obj instanceof Map)) { 
			throw new IllegalStateException ("Child not a map: " + obj.getClass ().getSimpleName ()); 
		} else { 
			return (Map<String, Object>) obj; 
		}
		
		// NOT REACHED
	}
	
	public
	void merge ()
	{
		
	}

	public
	JSONObject toJSON ()
		throws JSONException 
	{
		final JSONObject obj = toJSON (props); 
		return obj; 
	}
	
	public
	JSONObject toJSON (final Map<String, Object> map)
		throws JSONException 
	{
		final JSONObject obj = new JSONObject (); 
		for (final Map.Entry<String, Object> entry : map.entrySet ()) { 
			final String key = entry.getKey (); 
			final Object value = entry.getValue (); 
			if (value instanceof Map) { 
				final JSONObject child = toJSON ((Map<String, Object>) value); 
				obj.put (key, child); 
			} else { 
				obj.put (key, value); 
			}
		}
		return obj; 
	}
	
	public
	String toString ()
	{
		try { 
			return toJSON ().toString (2);
		}
		catch (final JSONException json_e) { 
			return json_e.getMessage (); 
		}
	}

}

// EOF