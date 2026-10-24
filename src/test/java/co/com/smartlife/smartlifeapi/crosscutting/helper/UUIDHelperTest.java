package co.com.smartlife.smartlifeapi.crosscutting.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class UUIDHelperTest {

	private static final UUID ZERO_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	private final UUIDHelper helper = UUIDHelper.getUUIDHelper();

	@Test
	void getUUIDHelperShouldAlwaysReturnTheSameInstance() {
		assertSame(UUIDHelper.getUUIDHelper(), UUIDHelper.getUUIDHelper());
	}

	@Test
	void getDefaultShouldReturnTheZeroUuid() {
		assertEquals(ZERO_UUID, helper.getDefault());
	}

	@Test
	void getDefaultShouldReturnTheDefaultForNull() {
		assertEquals(ZERO_UUID, helper.getDefault(null));
	}

	@Test
	void getDefaultShouldReturnTheGivenValue() {
		UUID value = UUID.randomUUID();

		assertEquals(value, helper.getDefault(value));
	}

	@Test
	void getFromStringShouldReturnTheDefaultForNullOrEmpty() {
		assertEquals(ZERO_UUID, helper.getFromString(null));
		assertEquals(ZERO_UUID, helper.getFromString(""));
	}

	@Test
	void getFromStringShouldParseAValidUuid() {
		UUID value = UUID.randomUUID();

		assertEquals(value, helper.getFromString(value.toString()));
	}

	@Test
	void getFromStringShouldFailForAnInvalidUuid() {
		assertThrows(IllegalArgumentException.class, () -> helper.getFromString("no-es-un-uuid"));
	}

	@Test
	void isDefaultUuidShouldBeTrueForTheDefaultAndForNull() {
		assertTrue(helper.isDefaultUUID(ZERO_UUID));
		assertTrue(helper.isDefaultUUID(null));
	}

	@Test
	void isDefaultUuidShouldBeFalseForARandomUuid() {
		assertFalse(helper.isDefaultUUID(UUID.randomUUID()));
	}

	@Test
	void generateNewUuidShouldReturnDifferentUuids() {
		UUID first = helper.generateNewUUID();
		UUID second = helper.generateNewUUID();

		assertNotEquals(first, second);
		assertNotEquals(ZERO_UUID, first);
	}

	@Test
	void privateConstructorShouldBeInstantiableByReflection() throws Exception {
		Constructor<UUIDHelper> constructor = UUIDHelper.class.getDeclaredConstructor();
		constructor.setAccessible(true);

		assertNotNull(constructor.newInstance());
	}
}