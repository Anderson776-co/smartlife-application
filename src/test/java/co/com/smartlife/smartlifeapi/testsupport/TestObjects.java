package co.com.smartlife.smartlifeapi.testsupport;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Construye objetos de prueba con todos sus campos llenos, usando reflexión.
 * Cada campo recibe un valor distinto, para que un cruce de campos se note.
 */
public final class TestObjects {

	private static final int FULL_DEPTH = 4;
	private static final Instant BASE_INSTANT = Instant.parse("2026-01-01T00:00:00Z");

	private final AtomicInteger counter = new AtomicInteger();
	private final int maxDepth;

	private TestObjects(final int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public static <T> T build(final Class<T> type) {
		return new TestObjects(FULL_DEPTH).create(type, 0);
	}

	/** Igual que build, pero deja en null los campos que son otras entidades. */
	public static <T> T buildWithoutRelations(final Class<T> type) {
		return new TestObjects(1).create(type, 0);
	}

	/** Las relaciones existen, pero las relaciones de esas relaciones quedan en null. */
	public static <T> T buildWithShallowRelations(final Class<T> type) {
		return new TestObjects(2).create(type, 0);
	}

	public static Object valueFor(final Class<?> type, final Type genericType) {
		return new TestObjects(FULL_DEPTH).value(type, genericType, 0);
	}

	private <T> T create(final Class<T> type, final int depth) {
		try {
			Constructor<?> constructor = Arrays.stream(type.getDeclaredConstructors())
					.max(Comparator.comparingInt(Constructor::getParameterCount))
					.orElseThrow(() -> new IllegalStateException("Sin constructor: " + type.getName()));
			constructor.setAccessible(true);
			Class<?>[] types = constructor.getParameterTypes();
			Type[] genericTypes = constructor.getGenericParameterTypes();
			Object[] arguments = new Object[types.length];
			for (int index = 0; index < types.length; index++) {
				arguments[index] = value(types[index], genericTypes[index], depth + 1);
			}
			return type.cast(constructor.newInstance(arguments));
		} catch (ReflectiveOperationException exception) {
			throw new IllegalStateException("No se pudo construir " + type.getName(), exception);
		}
	}

	private Object value(final Class<?> type, final Type genericType, final int depth) {
		if (type == UUID.class) {
			return UUID.randomUUID();
		}
		if (type == String.class) {
			return "valor-" + counter.incrementAndGet();
		}
		if (type == Boolean.class || type == boolean.class) {
			return counter.incrementAndGet() % 2 == 0;
		}
		if (type == Integer.class || type == int.class) {
			return counter.incrementAndGet();
		}
		if (type == Long.class || type == long.class) {
			return (long) counter.incrementAndGet();
		}
		if (type == Double.class || type == double.class) {
			return (double) counter.incrementAndGet();
		}
		if (type == BigDecimal.class) {
			return BigDecimal.valueOf(counter.incrementAndGet());
		}
		if (type == Instant.class) {
			return BASE_INSTANT.plusSeconds(counter.incrementAndGet());
		}
		if (type == LocalDate.class) {
			return LocalDate.of(2026, 1, 1).plusDays(counter.incrementAndGet());
		}
		if (type == LocalDateTime.class) {
			return LocalDateTime.of(2026, 1, 1, 0, 0).plusSeconds(counter.incrementAndGet());
		}
		if (type == LocalTime.class) {
			return LocalTime.of(8, 0).plusMinutes(counter.incrementAndGet());
		}
		if (type.isEnum()) {
			return type.getEnumConstants()[0];
		}
		if (List.class.isAssignableFrom(type) || Set.class.isAssignableFrom(type)) {
			return collectionOf(type, genericType, depth);
		}
		if (depth >= maxDepth) {
			return null;
		}
		return create(type, depth);
	}

	private Object collectionOf(final Class<?> type, final Type genericType, final int depth) {
		List<Object> elements = new ArrayList<>();
		if (genericType instanceof ParameterizedType parameterized
				&& parameterized.getActualTypeArguments()[0] instanceof Class<?> elementType
				&& depth < maxDepth) {
			elements.add(value(elementType, elementType, depth + 1));
		}
		return Set.class.isAssignableFrom(type) ? new HashSet<>(elements) : elements;
	}
}