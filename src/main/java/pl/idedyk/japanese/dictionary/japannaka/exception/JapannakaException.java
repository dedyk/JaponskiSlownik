package pl.idedyk.japanese.dictionary.japannaka.exception;

public class JapannakaException extends Exception {

	private static final long serialVersionUID = 1L;

	public JapannakaException(String message, Throwable cause) {
		super(message, cause);
	}

	public JapannakaException(String message) {
		super(message);
	}
}
