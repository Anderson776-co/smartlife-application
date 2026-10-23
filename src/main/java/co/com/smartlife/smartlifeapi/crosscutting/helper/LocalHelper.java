package co.com.smartlife.smartlifeapi.crosscutting.helper;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;

public final class LocalHelper {

	private LocalHelper() {
		throw new IllegalStateException("Utility class");
	}
	
	public static String getCurrentLocal() {
		Locale locale = LocaleContextHolder.getLocale();
		return locale.getLanguage();
	}

}
