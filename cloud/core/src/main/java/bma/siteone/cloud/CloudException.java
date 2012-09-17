package bma.siteone.cloud;

public class CloudException extends RuntimeException {

	public CloudException() {
		super();
	}

	public CloudException(String message, Throwable cause) {
		super(message, cause);
	}

	public CloudException(String message) {
		super(message);
	}

	public CloudException(Throwable cause) {
		super(cause);
	}

}
