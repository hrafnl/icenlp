package is.iclt.icenlp.icetagger;

public class IceTaggerException extends IceTaggerConfigrationException {
	private static final long serialVersionUID = 1L;

	public IceTaggerException() {
		super();
	}

	public IceTaggerException(String message, Throwable loc) {
		super(message, loc);
	}

	public IceTaggerException(String message) {
		super(message);
	}
}
