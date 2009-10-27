package is.ru.icecache.common;

import java.util.HashMap;

public class Configuration 
{
	private static Configuration instance;
	private HashMap<String, String> config;
		
	protected Configuration()
	{
		this.config = new HashMap<String, String>();
	}
	
	public void addConfigEntry(String key, String value)
	{
		this.config.put(key.toLowerCase(), value);
	}
	
	public String getValue(String key)
	{
		return this.config.get(key.toLowerCase());
	}
	
	public boolean containsKey(String key)
	{
		return this.config.containsKey(key.toLowerCase());
	}
	
	public static synchronized Configuration getInstance()
	{
		if(instance == null)
			instance = new Configuration();
		
		return instance;
	}
}