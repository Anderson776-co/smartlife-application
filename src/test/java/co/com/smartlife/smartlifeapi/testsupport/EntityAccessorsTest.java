package co.com.smartlife.smartlifeapi.testsupport;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class EntityAccessorsTest {

	static Stream<Class<?>> entityClasses() {
		return MapperCatalog.mappers().stream()
				.flatMap(mapperType -> Stream.of(MapperCatalog.entityType(mapperType), MapperCatalog.jpaType(mapperType)));
	}

	static Stream<Class<?>> classesWithEmptyConstructor() {
		return entityClasses().filter(type -> emptyConstructorOf(type) != null);
	}

	private static Constructor<?> emptyConstructorOf(final Class<?> type) {
		for (Constructor<?> constructor : type.getDeclaredConstructors()) {
			if (constructor.getParameterCount() == 0) {
				return constructor;
			}
		}
		return null;
	}

	private static Method getterOf(final Class<?> type, final String property) {
		for (String prefix : new String[] { "get", "is" }) {
			try {
				return type.getMethod(prefix + property);
			} catch (NoSuchMethodException exception) {
				// se prueba con el siguiente prefijo
			}
		}
		return null;
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("entityClasses")
	void everySetterShouldStoreTheValueThatItsGetterReturns(final Class<?> type) throws Exception {
		Object instance = TestObjects.build(type);

		for (Method setter : type.getMethods()) {
			if (!setter.getName().startsWith("set") || setter.getParameterCount() != 1) {
				continue;
			}
			Object value = TestObjects.valueFor(setter.getParameterTypes()[0], setter.getGenericParameterTypes()[0]);
			setter.invoke(instance, value);

			Method getter = getterOf(type, setter.getName().substring(3));
			if (getter != null) {
				assertThat(getter.invoke(instance)).as(setter.getName()).isEqualTo(value);
			}
		}
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("classesWithEmptyConstructor")
	void theEmptyConstructorShouldCreateTheEntity(final Class<?> type) throws Exception {
		Constructor<?> emptyConstructor = emptyConstructorOf(type);
		emptyConstructor.setAccessible(true);

		assertThat(emptyConstructor.newInstance()).isNotNull();
	}
}