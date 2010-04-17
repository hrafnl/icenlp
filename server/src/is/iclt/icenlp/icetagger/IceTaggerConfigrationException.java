package is.iclt.icenlp.icetagger;

/***
 * Exception that is used to wrap IceTagger exception and throw it to higher
 * levels.
 * 
 * @author hlynurs
 */
public class IceTaggerConfigrationException extends Exception {
	private static final long serialVersionUID = 1L;

	public IceTaggerConfigrationException() {
		super();
	}

	public IceTaggerConfigrationException(String message) {
		super(message);
	}

	public IceTaggerConfigrationException(String message, Throwable loc) {
		super(message, loc);
	}
}
