package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.Criteria;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.Filter;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.FilterOperator;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.PropertyVisitDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.AuthorizatorRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.PropertyVisitRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitResidentRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizatorEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.PropertyVisitEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitResidentEntity;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.mapper.PropertyVisitMapperUseCase;

class ConsultAuthorizatorPropertiesUseCaseImplTest {

	private static final UUID USER_ID = UUID.randomUUID();
	private static final UUID URBANIZATION_ID = UUID.randomUUID();
	private static final PropertyVisitEntity APARTMENT_202 = new PropertyVisitEntity(UUID.randomUUID(),
			"Bloque B, Vivienda 202");

	private AuthorizatorRepository authorizatorRepository;
	private VisitResidentRepository visitResidentRepository;
	private PropertyVisitRepository propertyVisitRepository;
	private ConsultAuthorizatorPropertiesUseCaseImpl useCase;

	@BeforeEach
	void setUp() {
		authorizatorRepository = mock(AuthorizatorRepository.class);
		visitResidentRepository = mock(VisitResidentRepository.class);
		propertyVisitRepository = mock(PropertyVisitRepository.class);
		useCase = new ConsultAuthorizatorPropertiesUseCaseImpl(authorizatorRepository, visitResidentRepository,
				propertyVisitRepository, Mappers.getMapper(PropertyVisitMapperUseCase.class));
	}

	@Test
	void shouldListTheDwellingWhereTheResidentLives() {
		givenRegisteredAuthorizator(true);
		givenResidentLivesIn(APARTMENT_202);
		when(propertyVisitRepository.matching(any(Criteria.class))).thenReturn(List.of(APARTMENT_202));

		final List<PropertyVisitDomain> result = useCase.list(authorizator("RESIDENT"));

		assertEquals(List.of(new PropertyVisitDomain(APARTMENT_202.getId(), APARTMENT_202.getLocation())), result);
	}

	@Test
	void shouldFilterThePropertiesByTheIdOfTheDwellingOfTheResident() {
		givenRegisteredAuthorizator(true);
		givenResidentLivesIn(APARTMENT_202);
		when(propertyVisitRepository.matching(any(Criteria.class))).thenReturn(List.of(APARTMENT_202));

		useCase.list(authorizator("RESIDENT"));

		final ArgumentCaptor<Criteria> captor = ArgumentCaptor.forClass(Criteria.class);
		verify(propertyVisitRepository).matching(captor.capture());
		assertEquals(List.of(new Filter(PropertyVisitEntity.ID, FilterOperator.EQUAL, APARTMENT_202.getId())),
				captor.getValue().filters().values());
	}

	@Test
	void shouldReturnNothingWhenTheUserHasNoDwellingAsResident() {
		givenRegisteredAuthorizator(true);
		when(visitResidentRepository.findById(USER_ID)).thenReturn(Optional.empty());

		assertTrue(useCase.list(authorizator("OWNER")).isEmpty());
		verifyNoInteractions(propertyVisitRepository);
	}

	@Test
	void shouldRejectAnAuthorizatorWhoIsNotRegistered() {
		when(authorizatorRepository.findById(USER_ID)).thenReturn(Optional.empty());

		assertThrows(SmartLifeException.class, () -> useCase.list(authorizator("RESIDENT")));
		verifyNoInteractions(visitResidentRepository, propertyVisitRepository);
	}

	@Test
	void shouldRejectAnInactiveAuthorizator() {
		givenRegisteredAuthorizator(false);

		assertThrows(SmartLifeException.class, () -> useCase.list(authorizator("RESIDENT")));
		verifyNoInteractions(visitResidentRepository, propertyVisitRepository);
	}

	private void givenRegisteredAuthorizator(final boolean isActive) {
		when(authorizatorRepository.findById(USER_ID))
				.thenReturn(Optional.of(new AuthorizatorEntity(USER_ID, "Usuario de prueba", isActive, URBANIZATION_ID)));
	}

	private void givenResidentLivesIn(final PropertyVisitEntity property) {
		when(visitResidentRepository.findById(USER_ID)).thenReturn(Optional.of(new VisitResidentEntity(USER_ID, property)));
	}

	private static AuthorizatorDomain authorizator(final String... roles) {
		return new AuthorizatorDomain(USER_ID, URBANIZATION_ID, Set.of(roles));
	}

}
