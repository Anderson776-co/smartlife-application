package co.com.smartlife.smartlifeapi.shared.usecase;

public interface UseCaseWithReturn <I,O>{
	
	O execute(I input);

}
