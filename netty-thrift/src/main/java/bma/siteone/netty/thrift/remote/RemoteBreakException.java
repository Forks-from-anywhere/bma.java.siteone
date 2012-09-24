package bma.siteone.netty.thrift.remote;

public class RemoteBreakException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RemoteBreakException() {
		super();
	}

	public RemoteBreakException(String message) {
		super(message);
	}

	public RemoteBreakException(Throwable cause) {
		super(cause);
	}

	public RemoteBreakException(String message, Throwable cause) {
		super(message, cause);
	}

}
