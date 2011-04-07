package at.molindo.svc.exception;

public class PropertyException extends Exception {

	private static final long serialVersionUID = 1L;
	private final short _property;

	public PropertyException(final short property) {
		_property = property;
	}

	public PropertyException(final String message, final short property) {
		super(message);
		_property = property;
	}

	public PropertyException(final String message, final Throwable cause, final short property) {
		super(message, cause);
		_property = property;
	}

	public PropertyException(final short property, final Throwable cause) {
		super(cause);
		_property = property;
	}

	public final short getProperty() {
		return _property;
	}

}
