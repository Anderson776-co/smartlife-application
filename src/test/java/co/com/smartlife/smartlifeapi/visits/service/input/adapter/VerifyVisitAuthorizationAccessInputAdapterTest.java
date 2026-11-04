package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationAccessDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.VisitAuthorizationAccessInputMapper;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.VisitAuthorizationAccessDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.VerifyVisitAuthorizationAccessUseCase;

class VerifyVisitAuthorizationAccessInputAdapterTest {

	private static final UUID USER_ID = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000003");
	private static final UUID URBANIZATION_ID = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000003");
	private static final UUID PROPERTY_ID = UUID.fromString("cccccccc-0000-0000-0000-000000000003");
	private static final Set<String> ROLES = Set.of("RESIDENT");
	private static final CurrentUser USER = new CurrentUser(USER_ID, "maria@example.com", ROLES, URBANIZATION_ID);

	private VerifyVisitAuthorizationAccessUseCase useCase;
	private VisitAuthorizationAccessInputMapper mapper;
	private VerifyVisitAuthorizationAccessInputAdapter adapter;

	@BeforeEach
	void setUp() {
		useCase = mock(VerifyVisitAuthorizationAccessUseCase.class);
		mapper = mock(VisitAuthorizationAccessInputMapper.class);
		adapter = new VerifyVisitAuthorizationAccessInputAdapter(useCase, mapper);
	}

	private static VisitAuthorizationAccessDomain accessDomain() {
		return new VisitAuthorizationAccessDomain(new AuthorizatorDomain(USER_ID, URBANIZATION_ID, ROLES), PROPERTY_ID);
	}

	@Test
	void shouldReturnTrueWhenTheUseCaseGrantsAccessToTheMappedRequest() {
		final VisitAuthorizationAccessDtoRequest request = new VisitAuthorizationAccessDtoRequest(USER, PROPERTY_ID);
		final VisitAuthorizationAccessDomain domain = accessDomain();
		when(mapper.toDomain(request)).thenReturn(domain);
		when(useCase.execute(domain)).thenReturn(Boolean.TRUE);

		assertTrue(adapter.execute(request));
	}

	@Test
	void shouldReturnFalseWhenTheUseCaseDeniesAccessToTheMappedRequest() {
		final VisitAuthorizationAccessDtoRequest request = new VisitAuthorizationAccessDtoRequest(USER, PROPERTY_ID);
		final VisitAuthorizationAccessDomain domain = accessDomain();
		when(mapper.toDomain(request)).thenReturn(domain);
		when(useCase.execute(domain)).thenReturn(Boolean.FALSE);

		assertFalse(adapter.execute(request));
	}

	@Test
	void shouldMapTheRequestOnceAndHandTheMappedDomainToTheUseCaseUnchanged() {
		final VisitAuthorizationAccessDtoRequest request = new VisitAuthorizationAccessDtoRequest(USER, PROPERTY_ID);
		final VisitAuthorizationAccessDomain domain = accessDomain();
		when(mapper.toDomain(request)).thenReturn(domain);
		when(useCase.execute(domain)).thenReturn(Boolean.TRUE);

		assertEquals(Boolean.TRUE, adapter.execute(request));

		verify(mapper).toDomain(request);
		verify(useCase).execute(domain);
		verifyNoMoreInteractions(mapper, useCase);
	}
}
