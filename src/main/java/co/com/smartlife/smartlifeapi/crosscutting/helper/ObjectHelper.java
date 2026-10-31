package co.com.smartlife.smartlifeapi.crosscutting.helper;

public final class ObjectHelper {
	private ObjectHelper() {
	}

	public static <O> boolean isNull(final O object) {
		return object == null;
	}

	public static <O> O getDefault(final O object, final O defaultValue) {
		return isNull(object) ? defaultValue : object;
	}
}
