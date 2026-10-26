package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.externalservices.identityprovider.firebase;

final class FirebaseRejectedException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	FirebaseRejectedException(final Throwable cause) {
		super(cause.getMessage(), cause);
	}

}
