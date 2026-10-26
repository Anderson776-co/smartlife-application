package co.com.smartlife.smartlifeapi.crosscutting.exception;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;

public class SmartLifeException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	
	public enum Type { BUSINESS, AUTHENTICATION, AUTHORIZATION, TECHNICAL, UNEXPECTED }
	
	private Throwable rootException;
	private String userMessage;
	private String technicalMessage;
	private final Type type;
	
	
	private SmartLifeException(final Throwable rootException, final String userMessage, 
			final String technicalMessage, final Type type) {
		super(technicalMessage);
		setRootException(rootException);
		setUserMessage(userMessage);
		setTechnicalMessage(technicalMessage);
		this.type = ObjectHelper.getDefault(type, Type.UNEXPECTED);
	}
	
	public static SmartLifeException create(final String userMessage) {
		return create(userMessage, userMessage);
	}
	
	public static SmartLifeException create(final String userMessage, final String technicalMessage) {
        return new SmartLifeException(new Exception(), userMessage, technicalMessage, Type.BUSINESS);
    }
	
	public static SmartLifeException create(final Throwable rootException, final String userMessage,final String technicalMessage) {
		return new SmartLifeException(rootException, userMessage, technicalMessage, Type.TECHNICAL);
	}
	
	public static SmartLifeException createAuthentication(final String userMessage,final String technicalMessage) {
		return new SmartLifeException(new Exception(), userMessage, technicalMessage, Type.AUTHENTICATION);
	}
	
	public static SmartLifeException createAuthorization(final String userMessage,final String technicalMessage) {
		return new SmartLifeException(new Exception(), userMessage, technicalMessage, Type.AUTHORIZATION);
}
	
	public Throwable getRootException() {
		return rootException;
	}

	private void setRootException(Throwable rootException) {
		this.rootException = ObjectHelper.getDefault(rootException, new Exception());
	}

	public String getUserMessage() {
		return userMessage;
	}

	private void setUserMessage(String userMessage) {
		this.userMessage = ObjectHelper.getDefault(userMessage, "");
	}

	public String getTechnicalMessage() {
		return technicalMessage;
	}

	private void setTechnicalMessage(String technicalMessage) {
		this.technicalMessage = ObjectHelper.getDefault(technicalMessage, "");
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Type getType() {
		return type;
	}

}
