First, fetch the latest source code using: 
cd
% ant clean fetch

Then update Cache.java: 

% emacs target/ehcache/ehcache/ehcache-core/src/main/java/net/sf/ehcache/Cache.java

    /**                                                                                                                                                                                                                                                                                 
     * Blah.                                                                                                                                                                                                                                                                            
     *                                                                                                                                                                                                                                                                                  
     * @param obj blah                                                                                                                                                                                                                                                                  
     * @return something                                                                                                                                                                                                                                                                
     */

    public final Element jsGet(Object obj) throws IllegalStateException, CacheException {
        return get((Object) obj);
    }

    /**                                                                                                                                                                                                                                                                                 
     * Blah.                                                                                                                                                                                                                                                                            
     *                                                                                                                                                                                                                                                                                  
     * @param key blah                                                                                                                                                                                                                                                                  
     * @return something                                                                                                                                                                                                                                                                
     */

    public final boolean jsRemove(Object key) throws IllegalStateException {
        return remove((Object) key);
    }

and Element.java

% emacs target/ehcache/ehcache/ehcache-core/src/main/java/net/sf/ehcache/Element.java

    /**                                                                                                                                                 
     * Word.                                                                                                                                            
     *                                                                                                                                                  
     * @param a blah                                                                                                                                    
     * @param b blah                                                                                                                                    
     * @return blah                                                                                                                                     
     */

    public static Element newInstance(Object a, Object b) {
        return new Element(a, b);
    }

and then build it: 

% cd target/ehcache/ehcache/ehcache-core
% mvn -Dmaven.test.skip=true install 

Using it from the script runner: 

| var c = MFP.EHCACHE.MANAGER.getCache ("access_token");
|
| var e = net.sf.ehcache.Element.newInstance ("foo", "bar"); 
| c.put (e); 
|
| c._get ("foo");

and then update the version in build.xml. 

and then rename to be RELEASE versions. 

% cd  target/ehcache/ehcache/ehcache-core/target
% mv ehcache-core-2.8.0{-SNAPSHOT,}.jar
% mv ehcache-core-2.8.0{-SNAPSHOT,}-sources.jar
% mv ehcache-core-2.8.0{-SNAPSHOT,}-javadoc.jar

and then push to maven: 

% ant deploy-ehcache

