package is.iclt.icenlp;

import is.iclt.icenlp.icetagger.IIceTagger;
import is.iclt.icenlp.icetagger.IceTaggerConfigrationException;


/***
 * IceNLPSingletonService is a singleton service that thread can communicate to
 * for serving incoming requests.
 * 
 * @author hlynurs
 */
public class IceNLPSingletonService {
	// Singleton instance.
	private static IceNLPSingletonService instance_;

	// Member variable for IceTagger
	IIceTagger icetagger;

	// Protected constructor for the class.
	protected IceNLPSingletonService() throws IceTaggerConfigrationException 
	{
		// Let us create a new instance of Icetagger.
		//this.icetagger = new IceTagger();
		System.out.println("[i] IceNLPSingletonService built.");
	}

	/***
	 * This function is used to request reference to to the singleton instance.
	 * If the instance does not exist then we call the protected constructor.
	 * 
	 * @return Reference to the service
	 * @throws IceTaggerConfigrationException
	 *             if there are any exception while building the IceTagger
	 *             object we will throw a configuration error.
	 */
	public static synchronized IceNLPSingletonService getInstance() throws IceTaggerConfigrationException {
		if (instance_ == null) {
			instance_ = new IceNLPSingletonService();
		}
		return instance_;
	}

	/***
	 * Function that passes the string to icetagger for analysis. The string
	 * will be tagged and returned to caller.
	 * 
	 * @param text
	 *            that one wants to analyze.
	 * @return The same text tagged with the tagset defined in the mapping file.
	 */
	public String tagText(String text) 
	{
		/*
		String[] lines = text.split("\n");
		String outPut = "";
		for(String s : lines)
			outPut += icetagger.tag(s) + "\n";
			
		if(outPut.length() >=1)
			outPut = outPut.substring(0, outPut.length()-1);
		return outPut;
		*/
		return null;
	}
}
