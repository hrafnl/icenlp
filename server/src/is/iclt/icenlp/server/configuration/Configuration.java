package is.iclt.icenlp.server.configuration;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Singleton configuration lookup. The key value pairs from the configuration
 * file is read into the single instance that is kept in memory and is available
 * to any network thread that is currently running.
 * 
 * @author hlynurs
 */
public class Configuration {
	// Private member variables.
	private static Configuration instance;
	private HashMap<String, String> config;

	// Protected constructor for the class.
	// The only instance that is created is
	// within the getInstance method.
	protected Configuration() {
		this.config = new HashMap<String, String>();
	}

	
	
	
	/**
	 * Function that reads through the configuration file and loads the
	 * Configuration object.
	 * 
	 * @param configFile Name (with path) of the configuration file.
	 * @return True if the configuration file was read without any errors, false otherwise.
	 */
	public static boolean loadConfig(String configFile) {
		FileInputStream fstream = null;
		DataInputStream in = null;
		BufferedReader br = null;

		try {
			fstream = new FileInputStream(configFile);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
		} catch (Exception e) {
			System.out.println("[X] Error while opening configuration file: "
					+ e.getMessage());
			return false;
		}

		String strLine = null;
		try {
			int lineNumber = 1;
			while ((strLine = br.readLine()) != null) {
				if (!strLine.startsWith("#") && !strLine.startsWith("//")
						&& strLine.length() > 0) {
					if (strLine.length() >= 2 && strLine.contains("=")) {
						Pattern p = Pattern.compile("\"[^\"]*\"");

						Matcher matcher = p.matcher(strLine);
						String value;
						if (matcher.find()) {
							value = matcher.group().replace("\"", "");
						} else {
							System.out
									.println("[X] Error in configuration file in line "
											+ lineNumber);
							return false;
						}

						String key = strLine.substring(0, strLine.indexOf("="))
								.replaceAll("\\s", "");

						Configuration.getInstance().addConfigEntry(key, value);
					} else {
						System.out
								.println("[X] Error in configuration file in line "
										+ lineNumber);
						return false;
					}
				}

				lineNumber += 1;
			}
		} catch (IOException e) {
			System.out.println("[X] Error while reading configuration file: "
					+ e.getMessage());
			return false;
		}
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Adds new key value pair into the configuration collection. If the adding
	 * key is already in the collection the old value gets overwritten by the
	 * new value
	 * 
	 * @param key
	 *            String object that contains the key
	 * @param value
	 *            String object that contains the value.
	 */
	public void addConfigEntry(String key, String value) {
		this.config.put(key.toLowerCase(), value);
	}

	/**
	 * Fetches the value with passing key. If the key is not in the
	 * configuration collection then null is returned.
	 * 
	 * @param key
	 *            String object that contains the key.
	 * @return String object that contains the key. is already in the
	 *         configuration collection, null otherwise.
	 */
	public String getValue(String key) {
		return this.config.get(key.toLowerCase());
	}

	/**
	 * Checks if a passing key is already in the configuration collection.
	 * 
	 * @param key
	 *            String object that contains the key.
	 * @return true if the key is in the collection, false otherwise.
	 */
	public boolean containsKey(String key) {
		return this.config.containsKey(key.toLowerCase());
	}

	/**
	 * Allows other classes to communicate with a singleton instance that is
	 * kept in memory. If the instance has not been built then before the
	 * reference is returned an instance is created.
	 * 
	 * @return Reference to a singleton instance of the configuration class.
	 */
	public static synchronized Configuration getInstance() {
		if (instance == null)
			instance = new Configuration();

		return instance;
	}

	/***
	 * Checks whether we are running the server in debug mode. This is used in
	 * many places in other parts of the code so we put this functionality into
	 * a function.
	 * 
	 * @return True of "debug" is set to "true" in the configuration file,
	 *         otherwise it is false.
	 */
	public boolean debugMode() {
		if (this.config.containsKey("debug")) {
			if (this.config.get("debug").equals("true")) {
				return true;
			}
		}
		return false;
	}
}