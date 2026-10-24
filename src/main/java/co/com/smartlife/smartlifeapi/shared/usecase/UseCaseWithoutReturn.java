package co.com.smartlife.smartlifeapi.shared.usecase;

public interface UseCaseWithoutReturn<I> {

	void execute(I input);
}
