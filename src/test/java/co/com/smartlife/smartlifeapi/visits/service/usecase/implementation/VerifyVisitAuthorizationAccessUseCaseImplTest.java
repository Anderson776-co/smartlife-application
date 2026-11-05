package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationAccessDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.AuthorizatorRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitResidentRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizatorEntity;

class VerifyVisitAuthorizationAccessUseCaseImplTest {

	private static final UUID USER_ID = UUID.randomUUID();
	private static final UUID URBANIZATION_ID = UUID.randomUUID();
	private static final UUID PROPERTY_ID = UUID.randomUUID();

	private AuthorizatorRepository authorizatorRepository;
	private VisitResidentRepository visitResidentRepository;
	private VerifyVisitAuthorizationAccessUseCaseImpl useCase;

	@BeforeEach
	void setUp() {
		authorizatorRepository = mock(AuthorizatorRepository.class);
		visitResidentRepository = mock(VisitResidentRepository.class);
		useCase = new VerifyVisitAuthorizationAccessUseCaseImpl(authorizatorRepository, visitResidentRepository);
	}

	@Test
	void shouldAllowResidentOfTheProperty() {
		givenRegisteredAuthorizator(true, URBANIZATION_ID);
		when(visitResidentRepository.residesIn(USER_ID, PROPERTY_ID)).thenReturn(true);

		assertTrue(useCase.execute(requestWithRoles("RESIDENT")));
	}

	@Test
	void shouldDenyResidentWhoDoesNotLiveInTheProperty() {
		givenRegisteredAuthorizator(true, URBANIZATION_ID);

		assertFalse(useCase.execute(requestWithRoles("RESIDENT")));
	}

	@Test
	void shouldDenyAnOwnerWhoIsNotResident() {
		givenRegisteredAuthorizator(true, URBANIZATION_ID);
		when(visitResidentRepository.residesIn(USER_ID, PROPERTY_ID)).thenReturn(true);

		assertFalse(useCase.execute(requestWithRoles("OWNER")));
		verifyNoInteractions(authorizatorRepository, visitResidentRepository);
	}

	@Test
	void shouldDenyInactiveAuthorizator() {
		givenRegisteredAuthorizator(false, URBANIZATION_ID);

		assertFalse(useCase.execute(requestWithRoles("RESIDENT")));
		verifyNoInteractions(visitResidentRepository);
	}

	@Test
	void shouldDenyAuthorizatorFromAnotherUrbanization() {
		givenRegisteredAuthorizator(true, UUID.randomUUID());

		assertFalse(useCase.execute(requestWithRoles("RESIDENT")));
		verifyNoInteractions(visitResidentRepository);
	}

	@Test
	void shouldDenyWhenTheAuthorizatorIsNotRegistered() {
		when(authorizatorRepository.findById(USER_ID)).thenReturn(Optional.empty());

		assertFalse(useCase.execute(requestWithRoles("RESIDENT")));
		verifyNoInteractions(visitResidentRepository);
	}

	private void givenRegisteredAuthorizator(final boolean isActive, final UUID urbanizationId) {
		when(authorizatorRepository.findById(USER_ID))
				.thenReturn(Optional.of(new AuthorizatorEntity(USER_ID, "Usuario de prueba", isActive, urbanizationId)));
	}

	private static VisitAuthorizationAccessDomain requestWithRoles(final String... roles) {
		final AuthorizatorDomain authorizator = new AuthorizatorDomain(USER_ID, URBANIZATION_ID, Set.of(roles));
		return new VisitAuthorizationAccessDomain(authorizator, PROPERTY_ID);
	}

}
