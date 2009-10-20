package is.ru.icecache.common;

/***
 * Makes the progam configuration (from arguments)
 * available in code.
 * @author hlynurs
 */
public class Configuration 
{
	// Default host-name and port are set.
	public static String host = "localhost";
	public static String port = "1234";
	
	// Default we don't use appertium output.
	public static boolean appertiumOutput = true;
	
	// default we disable Tritagger by default.
	public static boolean tritagger = false;
	
	// If these configuration are set to NULL then
	// we will read them from IceNLP resources.
	public static String iceLexiconsDir = null;
	public static String tokenizerLexicon = null;
	public static String tritaggerLexicon = null;
	
	// The mapperLexicon must be read from a command line
	// TODO: Add this lexicon into the IceNLP resource.
	public static String mapperLexicon = null;
}
