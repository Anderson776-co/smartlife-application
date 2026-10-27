package co.com.smartlife.smartlifeapi.testsupport;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.mapstruct.factory.Mappers;

import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.mapper.ApartmentMapper;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.mapper.DwellingMapper;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.mapper.HouseMapper;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.mapper.UrbanizationDwellingMapper;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.mapper.UserOwnerMapper;
import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.mapper.ResidenceMapper;
import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.mapper.ResidentMapper;
import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.mapper.ResidentResidenceMapper;
import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.mapper.UserResidentMapper;
import co.com.smartlife.smartlifeapi.shared.mapper.EntityMapper;
import co.com.smartlife.smartlifeapi.urbanization.infrastructure.output.adapter.repository.structured.mapper.UrbanizationMapper;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper.IdTypeMapper;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper.RoleMapper;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper.UrbanizationUserMapper;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper.UserMapper;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper.UserRoleMapper;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper.AuthorizatorMapper;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper.PropertyVisitMapper;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper.VisitAuthorizationMapper;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper.VisitReasonMapper;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper.VisitResidentMapper;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper.VisitorMapper;

/**
 * Lista de todos los mappers del proyecto. Las entidades (de dominio y JPA) se
 * obtienen de los tipos genericos de cada mapper, asi que no hay que listarlas.
 */
public final class MapperCatalog {

	private static final List<Class<?>> MAPPERS = List.of(
			AuthorizatorMapper.class, PropertyVisitMapper.class, VisitAuthorizationMapper.class,
			VisitReasonMapper.class, VisitResidentMapper.class, VisitorMapper.class,
			IdTypeMapper.class, RoleMapper.class, UrbanizationUserMapper.class, UserMapper.class,
			UserRoleMapper.class,
			ApartmentMapper.class, DwellingMapper.class, HouseMapper.class,
			UrbanizationDwellingMapper.class, UserOwnerMapper.class,
			ResidenceMapper.class, ResidentMapper.class, ResidentResidenceMapper.class,
			UserResidentMapper.class,
			UrbanizationMapper.class);

	private MapperCatalog() {
	}

	public static List<Class<?>> mappers() {
		return MAPPERS;
	}

	/**
	 * Crea el mapper y le entrega los otros mappers que usa por dentro (en la
	 * aplicacion los inyecta Spring; aqui se asignan a mano).
	 */
	public static Object instanceOf(final Class<?> mapperType) {
		Object mapper = Mappers.getMapper(mapperType);
		for (Field field : mapper.getClass().getDeclaredFields()) {
			if (EntityMapper.class.isAssignableFrom(field.getType())) {
				try {
					field.setAccessible(true);
					field.set(mapper, instanceOf(field.getType()));
				} catch (IllegalAccessException exception) {
					throw new IllegalStateException("No se pudo completar " + field.getName(), exception);
				}
			}
		}
		return mapper;
	}

	public static Class<?> entityType(final Class<?> mapperType) {
		return typeArgument(mapperType, 0);
	}

	public static Class<?> jpaType(final Class<?> mapperType) {
		return typeArgument(mapperType, 1);
	}

	private static Class<?> typeArgument(final Class<?> mapperType, final int index) {
		for (Type implemented : mapperType.getGenericInterfaces()) {
			if (implemented instanceof ParameterizedType parameterized
					&& parameterized.getRawType() == EntityMapper.class) {
				return (Class<?>) parameterized.getActualTypeArguments()[index];
			}
		}
		throw new IllegalStateException(mapperType.getName() + " no extiende EntityMapper");
	}
}