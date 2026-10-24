package co.com.smartlife.smartlifeapi.crosscutting.exception;

public class SmarLifeException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	
	private Throwable rootException;
	private String userMessage;
	private String technicalMessage;
	
	private SmarLifeException(final Throwable rootException, final String userMessage, final String technicalMessage) {
		setRootException(rootException);
		setUserMessage(userMessage);
		setTechnicalMessage(technicalMessage);
	}
	
	public static SmarLifeException create(final String userMessage) {
		return new SmarLifeException(new Exception(), userMessage, userMessage);
	}
	
	public static SmarLifeException create(final String userMessage, final String technicalMessage) {
		return new SmarLifeException(new Exception(),userMessage, technicalMessage);
	}
	
	public Throwable getRootException() {
		return rootException;
	}

	private void setRootException(Throwable rootException) {
		this.rootException = rootException;
	}

	public String getUserMessage() {
		return userMessage;
	}

	private void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}

	public String getTechnicalMessage() {
		return technicalMessage;
	}

	private void setTechnicalMessage(String technicalMessage) {
		this.technicalMessage = technicalMessage;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
