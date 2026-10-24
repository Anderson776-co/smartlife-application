package co.com.smartlife.smartlifeapi.testsupport;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import co.com.smartlife.smartlifeapi.shared.mapper.EntityMapper;

class EntityMapperRoundTripTest {

	static Stream<Class<?>> mappers() {
		return MapperCatalog.mappers().stream();
	}

	@SuppressWarnings("unchecked")
	private static EntityMapper<Object, Object> mapperOf(final Class<?> mapperType) {
		return (EntityMapper<Object, Object>) MapperCatalog.instanceOf(mapperType);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("mappers")
	void aJpaEntityShouldComeBackEqualAfterTheRoundTrip(final Class<?> mapperType) {
		EntityMapper<Object, Object> mapper = mapperOf(mapperType);
		Object jpaEntity = TestObjects.build(MapperCatalog.jpaType(mapperType));

		Object entity = mapper.toEntity(jpaEntity);
		Object back = mapper.toJpaEntity(entity);

		assertThat(entity).isNotNull().isInstanceOf(MapperCatalog.entityType(mapperType));
		assertThat(back).usingRecursiveComparison().isEqualTo(jpaEntity);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("mappers")
	void aDomainEntityShouldComeBackEqualAfterTheRoundTrip(final Class<?> mapperType) {
		EntityMapper<Object, Object> mapper = mapperOf(mapperType);
		Object entity = TestObjects.build(MapperCatalog.entityType(mapperType));

		Object jpaEntity = mapper.toJpaEntity(entity);
		Object back = mapper.toEntity(jpaEntity);

		assertThat(jpaEntity).isNotNull().isInstanceOf(MapperCatalog.jpaType(mapperType));
		assertThat(back).usingRecursiveComparison().isEqualTo(entity);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("mappers")
	void aJpaEntityWithoutRelationsShouldComeBackEqualAfterTheRoundTrip(final Class<?> mapperType) {
		EntityMapper<Object, Object> mapper = mapperOf(mapperType);
		Object jpaEntity = TestObjects.buildWithoutRelations(MapperCatalog.jpaType(mapperType));

		Object back = mapper.toJpaEntity(mapper.toEntity(jpaEntity));

		assertThat(back).usingRecursiveComparison().isEqualTo(jpaEntity);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("mappers")
	void aDomainEntityWithoutRelationsShouldComeBackEqualAfterTheRoundTrip(final Class<?> mapperType) {
		EntityMapper<Object, Object> mapper = mapperOf(mapperType);
		Object entity = TestObjects.buildWithoutRelations(MapperCatalog.entityType(mapperType));

		Object back = mapper.toEntity(mapper.toJpaEntity(entity));

		assertThat(back).usingRecursiveComparison().isEqualTo(entity);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("mappers")
	void aJpaEntityWithShallowRelationsShouldComeBackEqualAfterTheRoundTrip(final Class<?> mapperType) {
		EntityMapper<Object, Object> mapper = mapperOf(mapperType);
		Object jpaEntity = TestObjects.buildWithShallowRelations(MapperCatalog.jpaType(mapperType));

		Object back = mapper.toJpaEntity(mapper.toEntity(jpaEntity));

		assertThat(back).usingRecursiveComparison().isEqualTo(jpaEntity);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("mappers")
	void aDomainEntityWithShallowRelationsShouldComeBackEqualAfterTheRoundTrip(final Class<?> mapperType) {
		EntityMapper<Object, Object> mapper = mapperOf(mapperType);
		Object entity = TestObjects.buildWithShallowRelations(MapperCatalog.entityType(mapperType));

		Object back = mapper.toEntity(mapper.toJpaEntity(entity));

		assertThat(back).usingRecursiveComparison().isEqualTo(entity);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("mappers")
	void aListShouldBeMappedElementByElement(final Class<?> mapperType) {
		EntityMapper<Object, Object> mapper = mapperOf(mapperType);
		Object jpaEntity = TestObjects.build(MapperCatalog.jpaType(mapperType));

		List<Object> entities = mapper.toEntity(List.of(jpaEntity));

		assertThat(entities).hasSize(1);
		assertThat(entities.get(0)).usingRecursiveComparison().isEqualTo(mapper.toEntity(jpaEntity));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("mappers")
	void nullsShouldBeMappedToNull(final Class<?> mapperType) {
		EntityMapper<Object, Object> mapper = mapperOf(mapperType);

		assertThat(mapper.toEntity((Object) null)).isNull();
		assertThat(mapper.toJpaEntity(null)).isNull();
		assertThat(mapper.toEntity((List<Object>) null)).isNull();
	}

	@Test
	void theCatalogShouldListAllTheMappers() {
		assertThat(MapperCatalog.mappers()).hasSize(21).doesNotHaveDuplicates();
	}
}