package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.PropertyVisitDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.AuthorizatorMapperInput;
import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.PropertyVisitInputMapper;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.ListAuthorizatorPropertiesDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.PropertyVisitDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.ConsultAuthorizatorPropertiesUseCase;

class ConsultAuthorizatorPropertiesInputAdapterTest {

	private static final UUID USER_ID = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000002");
	private static final UUID URBANIZATION_ID = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001");
	private static final UUID PROPERTY_ID = UUID.fromString("cccccccc-0000-0000-0000-000000000002");
	private static final CurrentUser USER = new CurrentUser(USER_ID, "maria@example.com", Set.of("RESIDENT"),
			URBANIZATION_ID);

	private ConsultAuthorizatorPropertiesUseCase useCase;
	private ConsultAuthorizatorPropertiesInputAdapter adapter;

	@BeforeEach
	void setUp() {
		useCase = mock(ConsultAuthorizatorPropertiesUseCase.class);
		adapter = new ConsultAuthorizatorPropertiesInputAdapter(useCase, Mappers.getMapper(AuthorizatorMapperInput.class),
				Mappers.getMapper(PropertyVisitInputMapper.class));
	}

	@Test
	void shouldAskTheUseCaseForTheSessionUserAndMapTheirPropertiesToTheResponse() {
		final AuthorizatorDomain authorizator = new AuthorizatorDomain(USER_ID, URBANIZATION_ID, Set.of("RESIDENT"));
		when(useCase.list(authorizator)).thenReturn(Optional.of(new PropertyVisitDomain(PROPERTY_ID, "Bloque B, Vivienda 202")));

		final List<PropertyVisitDtoResponse> result = adapter.execute(new ListAuthorizatorPropertiesDtoRequest(USER));

		assertEquals(List.of(new PropertyVisitDtoResponse(PROPERTY_ID, "Bloque B, Vivienda 202")), result);
		verify(useCase).list(authorizator);
	}

	@Test
	void shouldReturnAnEmptyListWhenTheUseCaseFindsNoProperty() {
		when(useCase.list(new AuthorizatorDomain(USER_ID, URBANIZATION_ID, Set.of("RESIDENT")))).thenReturn(Optional.empty());

		assertTrue(adapter.execute(new ListAuthorizatorPropertiesDtoRequest(USER)).isEmpty());
	}

	@Test
	void aRequestWithoutUserShouldCarryAnAnonymousUserWithoutRoles() {
		final ListAuthorizatorPropertiesDtoRequest request = new ListAuthorizatorPropertiesDtoRequest(null);

		assertTrue(UUIDHelper.getUUIDHelper().isDefaultUUID(request.currentUser().userId()));
		assertTrue(request.currentUser().roles().isEmpty());
	}

}
