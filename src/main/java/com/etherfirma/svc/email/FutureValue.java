package com.etherfirma.svc.email;

import java.util.concurrent.*;

/**
 * 
 * @author crawford
 *
 * @param <T>
 */

public class FutureValue<T>
{
	public
	FutureValue ()
	{
		return ;
	}
	
	/**
	 * Track whether the value has been set yet, or not. 
	 */
	
	protected boolean isSet; 
	
	/**
	 * Contains the value once it has been set.  
	 */
	
	protected T value; 
	
	/**
	 * 
	 * @param value
	 */
	
	public synchronized 
	void setValue (T value) 
	{
		if (isSet) { 
			throw new IllegalStateException ("Value already set."); 
		}
		this.value = value; 
		this.isSet = true; 
		notifyAll ();
		return; 
	}
	
	/**
	 * i 
	 * @return
	 * @throws InterruptedException
	 */
	
	public synchronized
	T getValue ()
		throws InterruptedException
	{
		if (! isSet) { 
			wait ();
		}
		return value;  
	}

	/**
	 * 
	 * @param timeout
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 */
	
	public synchronized
	T getValue (final long timeout)
		throws InterruptedException, TimeoutException
	{
		if (! isSet) { 
			wait (timeout);
			if (! isSet) { 
				throw new TimeoutException (); 
			}
		}
		return value;  
	}
}

// EOF