package co.com.smartlife.smartlifeapi.crosscutting.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;

import org.junit.jupiter.api.Test;

class ObjectHelperTest {

	@Test
	void isNullShouldReturnTrueForNull() {
		assertTrue(ObjectHelper.isNull(null));
	}

	@Test
	void isNullShouldReturnFalseForAValue() {
		assertFalse(ObjectHelper.isNull("valor"));
	}

	@Test
	void getDefaultShouldReturnTheDefaultWhenTheObjectIsNull() {
		assertEquals("por defecto", ObjectHelper.getDefault(null, "por defecto"));
	}

	@Test
	void getDefaultShouldReturnTheObjectWhenItIsNotNull() {
		assertEquals("valor", ObjectHelper.getDefault("valor", "por defecto"));
	}

	@Test
	void privateConstructorShouldBeInstantiableByReflection() throws Exception {
		Constructor<ObjectHelper> constructor = ObjectHelper.class.getDeclaredConstructor();
		constructor.setAccessible(true);

		assertNotNull(constructor.newInstance());
	}
}