package is.ru.icenlpserver.common;

import java.util.HashMap;

/**
 * Singleton configuration lookup. The key value pairs from
 * the configuration file is read into the single instance
 * that is kept in memory and is available to any
 * network thread that is currently running.
 * @author hlynurs
 */
public class Configuration 
{
	// Private member variables.
	private static Configuration instance;
	private HashMap<String, String> config;
		
	
	// Protected constructor for the class.
	// The only instance that is created is 
	// within the getInstance method.
	protected Configuration()
	{
		this.config = new HashMap<String, String>();
	}

	/**
	 * Adds new key value pair into the configuration collection.
	 * If the adding key is already in the collection the old
	 * value gets overwritten by the new value
	 * @param key String object that contains the key
	 * @param value String object that contains the value.
	 */
	public void addConfigEntry(String key, String value)
	{
		this.config.put(key.toLowerCase(), value);
	}
	
	/**
	 * Fetches the value with passing key. If the key is not in
	 * the configuration collection then null is returned. 
	 * @param key String object that contains the key.
	 * @return String object that contains the key.
	 * is already in the configuration collection, null otherwise.
	 */
	public String getValue(String key)
	{
		return this.config.get(key.toLowerCase());
	}
	
	/**
	 * Checks if a passing key is already in the configuration
	 * collection.
	 * @param key String object that contains the key.
	 * @return true if the key is in the collection, false otherwise.
	 */
	public boolean containsKey(String key)
	{
		return this.config.containsKey(key.toLowerCase());
	}
	
	/**
	 * Allows other classes to communicate with a singleton
	 * instance that is kept in memory. If the instance has not
	 * been built then before the reference is returned an instance
	 * is created.
	 * @return Reference to a singleton instance of the configuration class.
	 */
	public static synchronized Configuration getInstance()
	{
		if(instance == null)
			instance = new Configuration();
		
		return instance;
	}
}