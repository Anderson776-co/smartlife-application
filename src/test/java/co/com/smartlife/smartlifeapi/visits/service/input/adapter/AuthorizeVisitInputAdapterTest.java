package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.VisitAuthorizationMapperInput;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.AuthorizeVisitDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.AuthorizeVisitUseCase;

class AuthorizeVisitInputAdapterTest {

	private AuthorizeVisitUseCase useCase;
	private VisitAuthorizationMapperInput mapper;
	private AuthorizeVisitInputAdapter adapter;

	@BeforeEach
	void setUp() {
		useCase = mock(AuthorizeVisitUseCase.class);
		mapper = mock(VisitAuthorizationMapperInput.class);
		adapter = new AuthorizeVisitInputAdapter(useCase, mapper);
	}

	@Test
	void shouldReturnTheResponseOfTheRegisteredAuthorization() {
		final AuthorizeVisitDtoRequest request = new AuthorizeVisitDtoRequest(null, null, null, null, null, null, null,
				null);
		final VisitAuthorizationDomain requested = mock(VisitAuthorizationDomain.class);
		final VisitAuthorizationDomain registered = mock(VisitAuthorizationDomain.class);
		final VisitAuthorizationDtoResponse response =
				new VisitAuthorizationDtoResponse(UUID.randomUUID(), "http://qr", true);
		when(mapper.toDomain(any(AuthorizeVisitDtoRequest.class))).thenReturn(requested);
		when(useCase.execute(requested)).thenReturn(registered);
		when(mapper.toDTO(registered)).thenReturn(response);

		assertSame(response, adapter.execute(request));
	}
}
