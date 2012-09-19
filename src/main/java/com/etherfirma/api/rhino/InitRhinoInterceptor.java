package com.etherfirma.api.rhino;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.*;
import org.mozilla.javascript.*;

import com.tessera.dispatch.*;
import com.tessera.intercept.*;

/**
 * 
 * @author crawford
 *
 */

public class InitRhinoInterceptor
	extends InterceptorSupport
{
	private static final Logger logger = Logger.getLogger (InitRhinoInterceptor.class); 
	
	public
	InitRhinoInterceptor (Map<String, String> props)
	{
		super (props);
		return; 
	}

   static class MyFactory
  		extends ContextFactory
  {
	   protected boolean useDynamicScope;

	   public
	   MyFactory (final boolean useDynamicScope)
	   {
		   this.useDynamicScope = useDynamicScope; 
		   return; 
	   }
	   
      @Override
      protected boolean hasFeature(Context cx, int featureIndex)
      {
          if (featureIndex == Context.FEATURE_DYNAMIC_SCOPE) {
              return useDynamicScope;
          }
          return super.hasFeature(cx, featureIndex);
      }
  }

   interface PROP
   	extends InterceptorSupport.PROP
   {
	   public String SHUTDOWN = "shutdown"; 
   }
   
   interface DEFAULT
   {
	   public String ATTR = "results"; 
   }
   	
	@Override
	public Alteration intercept (HttpServletRequest req, HttpServletResponse res, DispatchContext dc)
		throws Exception 
	{
		// Get the output object
		
		List<String> output = new ArrayList<String> (); 
		
		if (req != null) { 
			final String attr = getProperty (PROP.ATTR, DEFAULT.ATTR); 
			output = (List<String>) req.getAttribute (attr); 
			if (output == null) { 
				output = new ArrayList<String> (); 
				req.setAttribute (attr, output); 
			} 
		}
		
	   // Create a global context factory for shared scopes

	   if (! ContextFactory.hasExplicitGlobal ()) { 	
		   final ContextFactory cf = new MyFactory (true);
		   ContextFactory.initGlobal (cf);
		   output.add ("Set explicit ContextFactory."); 
	   } else { 
		   output.add ("Explicit ContextFactory already set."); 
	   }
       
	   // Shutdown the previous context if this is a reload
	   
       final Dispatcher disp = dc.getDispatcher (); 
       final ScriptableObject so = RhinoUtil.getSharedScope (disp);
       
       if (so != null) {
    	   final String shutdown = getProperty (PROP.SHUTDOWN);
    	   output.add ("Found prior shared scope."); 
    	   if (shutdown == null) {
    		   output.add ("No shutdown function defined.");     
    	   } else { 
    		   final ServletContext sc = disp.getServletContext (); 
    		   final String result = ShutdownListener.invokeShutdownListeners (shutdown, sc);
    		   output.add ("Invoked shutdown on prior context: " + result); 
    	   }
       }
    	   
       // Create the master context
       
       final Context cx = Context.enter ();
       final ScriptableObject sharedScope = cx.initStandardObjects (null, true);
       
       RhinoUtil.setSharedScope (disp, sharedScope);
       output.add ("Created new Global scope."); 
       return NO_ALTERATION; 
	}
}

// EOF


