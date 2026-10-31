package co.com.smartlife.smartlifeapi.crosscutting.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

class LocalHelperTest {

	@AfterEach
	void restoreTheLocale() {
		LocaleContextHolder.resetLocaleContext();
	}

	@Test
	void getCurrentLocalShouldReturnTheLanguageOfTheCurrentLocale() {
		LocaleContextHolder.setLocale(Locale.forLanguageTag("es-CO"));

		assertEquals("es", LocalHelper.getCurrentLocal());
	}

	@Test
	void getCurrentLocalShouldFollowTheLocaleWhenItChanges() {
		LocaleContextHolder.setLocale(Locale.ENGLISH);

		assertEquals("en", LocalHelper.getCurrentLocal());
	}

	@Test
	void theConstructorShouldRefuseToCreateInstances() throws Exception {
		Constructor<LocalHelper> constructor = LocalHelper.class.getDeclaredConstructor();
		constructor.setAccessible(true);

		InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);

		assertInstanceOf(IllegalStateException.class, exception.getCause());
	}
}