package co.com.smartlife.smartlifeapi.dwellings.service.input.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import co.com.smartlife.smartlifeapi.dwellings.domain.DwellingOwnerChangeDomain;
import co.com.smartlife.smartlifeapi.dwellings.service.input.adapter.mapper.DwellingOwnerMapperInput;
import co.com.smartlife.smartlifeapi.dwellings.service.input.dto.request.ChangeDwellingOwnerDtoRequest;
import co.com.smartlife.smartlifeapi.dwellings.service.usecase.contract.ChangeDwellingOwnerUseCase;

class ChangeDwellingOwnerInputAdapterTest {

	private static final UUID ID = UUID.fromString("cccccccc-0000-0000-0000-000000000001");
	private static final UUID OWNER = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000002");

	@Test
	void shouldMapTheRequestToTheDomainAndRunTheUseCase() {
		final ChangeDwellingOwnerUseCase useCase = mock(ChangeDwellingOwnerUseCase.class);
		final ChangeDwellingOwnerInputAdapter adapter = new ChangeDwellingOwnerInputAdapter(useCase, Mappers.getMapper(DwellingOwnerMapperInput.class));

		adapter.execute(new ChangeDwellingOwnerDtoRequest(ID, OWNER));

		final ArgumentCaptor<DwellingOwnerChangeDomain> captor = ArgumentCaptor.forClass(DwellingOwnerChangeDomain.class);
		verify(useCase).execute(captor.capture());
		assertEquals(ID, captor.getValue().dwellingId());
		assertEquals(OWNER, captor.getValue().ownerId());
	}
}
