package co.com.smartlife.smartlifeapi.shared.service.usecase;

public interface UseCaseWithoutReturn<I> {

	void execute(I input);
}
