package com.etherfirma.util.settings;

import org.apache.log4j.*;
import org.testng.annotations.*;

/**
 * 
 * @author crawford
 *
 */

public class SettingsTest
{
	@Test 
	public 
	void test1 ()
		throws Exception
	{
		final Settings s = new Settings (); 
		System.err.println (s);
		
		s.setProperty ("color", "yellow"); 
		s.setProperty ("time", 123456L); 
		s.setProperty ("size", 32); 
		s.setProperty ("happy", true); 
		System.err.println (s);
		
		s.getChild ("XXXX", true).put ("a", 1); 
		s.getChild ("XXXX", true).put ("b", 1); 
		
		s.getChild (new String [] { "a", "b", "c" }, true).put ("foo", "bar"); 
		s.getChild (new String [] { "a", "b", "c", "d" }, true).put ("x", "y"); 
		System.err.println (s); 
		
		return; 
	}
	
	@Test
	public
	void test2 ()
		throws Exception
	{
		final Settings s1 = new Settings (); 
		s1.setProperty ("a1", "1"); 
		s1.setProperty ("a2", "1"); 
		s1.setProperty ("a3", "1"); 
		s1.setProperty ("a4", "1"); 
		s1.getChild ("sub", true).put ("foo", "bar"); 
		s1.getChild ("sub", true).put ("baz", "boo"); 
		
		final Settings s2 = new Settings (); 
		s2.setProperty ("a3", "2"); 
		s2.setProperty ("a4", "2"); 
		s2.setProperty ("a5", "2"); 
		s2.setProperty ("a6", "2");
		s2.getChild ("sub", true).put ("foo", "bar2"); 
		s2.getChild ("sub", true).put ("baz2", "boo2"); 
		
		
		System.err.println (s1); 
		System.err.println (s2);
		s1.merge (s2);
		System.err.println (s1); 
		
		return ;
	}
	
	@Test
	public 
	void test3 ()
		throws Exception
	{
		final Settings s = new Settings (); 
		s.mergeResource ("com/etherfirma/util/settings/sample/001-foo.json"); 
		s.mergeResource ("com/etherfirma/util/settings/sample/002-bar.json"); 
		s.mergeResource ("com/etherfirma/util/settings/sample/003-baz.json"); 
		System.err.println (s); 
		
		System.err.println (s.lookup ("person.address.zip")); 
		return; 
	}
	
	
	public static
	void main (final String [] args)
		throws Exception
	{
		BasicConfigurator.configure (); 
		new SettingsTest ().test3 (); 
		return; 
	}
}

// EOF