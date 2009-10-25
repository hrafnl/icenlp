package is.ru.icecache.icenlp;

import is.ru.icecache.icenlp.icetagger.IIceTagger;
import is.ru.icecache.icenlp.icetagger.IceTagger;
import is.ru.icecache.icenlp.icetagger.IceTaggerConfigrationException;

/***
 * IceNLPSingletonService is a singleton
 * service that thread can communicate
 * to for serving incoming tagging requests.
 * @author hlynurs
 */
public class IceNLPSingletonService 
{
	// Singleton instance.
	private static IceNLPSingletonService instance_;

	// Member variable for IceTagger
	IIceTagger icetagger;
	
	// Protected constructor for the class.
	protected IceNLPSingletonService() throws IceTaggerConfigrationException
	{	
		// Let us create a new instance of Icetagger.
		this.icetagger = new IceTagger();
		System.out.println("[i] IceNLPSingletonService built.");
	}
	
	/***
	 * This function is used to request reference
	 * to to the singleton instance. If the instance
	 * does not exist then we call the protected
	 * constructor.
	 * @return Reference to the service
	 * @throws IceTaggerConfigrationException if there are any
	 * exception while building the IceTagger object we will throw
	 * a configuration error.
	 */
	public static synchronized IceNLPSingletonService getInstance() throws IceTaggerConfigrationException
	{
        if (instance_ == null) 
        {
        	instance_ = new IceNLPSingletonService();
        }
        return instance_;
	}
	
	public String tagText(String text)
	{
		return icetagger.tag(text).replace("\n", "");
	}
}
