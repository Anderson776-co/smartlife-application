package co.com.smartlife.smartlifeapi.crosscutting.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;

import org.junit.jupiter.api.Test;

class TextHelperTest {

	@Test
	void getDefaultWithoutArgumentShouldReturnEmpty() {
		assertEquals("", TextHelper.getDefault());
	}

	@Test
	void getDefaultShouldReturnEmptyForNull() {
		assertEquals("", TextHelper.getDefault(null));
	}

	@Test
	void getDefaultShouldReturnTheValue() {
		assertEquals("hola", TextHelper.getDefault("hola"));
	}

	@Test
	void getDefaultWithTrimShouldRemoveTheSpaces() {
		assertEquals("hola", TextHelper.getDefaultWithTrim("  hola  "));
	}

	@Test
	void getDefaultWithTrimShouldReturnEmptyForNull() {
		assertEquals("", TextHelper.getDefaultWithTrim(null));
	}

	@Test
	void isEmptyShouldBeTrueForNullAndForEmptyText() {
		assertTrue(TextHelper.isEmpty(null));
		assertTrue(TextHelper.isEmpty(""));
	}

	@Test
	void isEmptyShouldBeFalseForSpacesAndForText() {
		assertFalse(TextHelper.isEmpty(" "));
		assertFalse(TextHelper.isEmpty("hola"));
	}

	@Test
	void isEmptyWithTrimShouldBeTrueForSpaces() {
		assertTrue(TextHelper.isEmptyWithTrim("   "));
		assertTrue(TextHelper.isEmptyWithTrim(null));
	}

	@Test
	void isEmptyWithTrimShouldBeFalseForText() {
		assertFalse(TextHelper.isEmptyWithTrim(" hola "));
	}

	@Test
	void isValidEmailShouldAcceptValidEmails() {
		assertTrue(TextHelper.isValidEmail("usuario@test.com"));
		assertTrue(TextHelper.isValidEmail("  usuario@test.com  "));
		assertTrue(TextHelper.isValidEmail("nombre.apellido+etiqueta@correo.co"));
	}

	@Test
	void isValidEmailShouldRejectInvalidEmails() {
		assertFalse(TextHelper.isValidEmail(null));
		assertFalse(TextHelper.isValidEmail(""));
		assertFalse(TextHelper.isValidEmail("sin-arroba"));
		assertFalse(TextHelper.isValidEmail("usuario@test"));
	}

	@Test
	void isValidStringShouldAcceptLetters() {
		assertTrue(TextHelper.isValidString("Juan"));
		assertTrue(TextHelper.isValidString("Ñandú"));
		assertTrue(TextHelper.isValidString("  María  "));
	}

	@Test
	void isValidStringShouldRejectOtherCharacters() {
		assertFalse(TextHelper.isValidString(null));
		assertFalse(TextHelper.isValidString(""));
		assertFalse(TextHelper.isValidString("Juan Pérez"));
		assertFalse(TextHelper.isValidString("Juan3"));
	}

	@Test
	void lengthIsValidShouldCheckTheRange() {
		assertTrue(TextHelper.lengthIsValid("abc", 1, 5, false));
		assertTrue(TextHelper.lengthIsValid(null, 0, 5, false));
		assertFalse(TextHelper.lengthIsValid("", 1, 5, false));
		assertFalse(TextHelper.lengthIsValid("abcdef", 1, 5, false));
	}

	@Test
	void lengthIsValidShouldApplyTrimOnlyWhenAsked() {
		assertTrue(TextHelper.lengthIsValid("  a ", 1, 1, true));
		assertFalse(TextHelper.lengthIsValid("  a ", 1, 1, false));
	}

	@Test
	void lengthIsValidWithTrimShouldCheckTheTrimmedLength() {
		assertTrue(TextHelper.lengthIsValidWithTrim("  ab ", 2, 2));
		assertFalse(TextHelper.lengthIsValidWithTrim("  a ", 2, 2));
	}

	@Test
	void formatIsValidShouldCheckThePattern() {
		assertTrue(TextHelper.formatIsValid("123", "\\d+", false));
		assertFalse(TextHelper.formatIsValid("12a", "\\d+", false));
		assertTrue(TextHelper.formatIsValid(null, "\\d*", false));
	}

	@Test
	void formatIsValidShouldApplyTrimOnlyWhenAsked() {
		assertTrue(TextHelper.formatIsValid(" 123 ", "\\d+", true));
		assertFalse(TextHelper.formatIsValid(" 123 ", "\\d+", false));
	}

	@Test
	void privateConstructorShouldBeInstantiableByReflection() throws Exception {
		Constructor<TextHelper> constructor = TextHelper.class.getDeclaredConstructor();
		constructor.setAccessible(true);

		assertNotNull(constructor.newInstance());
	}
}