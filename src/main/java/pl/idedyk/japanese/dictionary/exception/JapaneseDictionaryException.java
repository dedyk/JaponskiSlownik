package pl.idedyk.japanese.dictionary.exception;

public class JapaneseDictionaryException extends Exception {

	private static final long serialVersionUID = 1L;

	public JapaneseDictionaryException(String message, Throwable cause) {
		super(message, cause);
	}

	public JapaneseDictionaryException(String message) {
		super(message);
	}
}
