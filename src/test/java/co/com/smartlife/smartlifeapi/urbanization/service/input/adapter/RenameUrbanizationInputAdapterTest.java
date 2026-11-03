package co.com.smartlife.smartlifeapi.urbanization.service.input.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import co.com.smartlife.smartlifeapi.urbanization.domain.UrbanizationRenameDomain;
import co.com.smartlife.smartlifeapi.urbanization.service.input.adapter.mapper.UrbanizationRenameMapperInput;
import co.com.smartlife.smartlifeapi.urbanization.service.input.dto.request.RenameUrbanizationDtoRequest;
import co.com.smartlife.smartlifeapi.urbanization.service.usecase.contract.RenameUrbanizationUseCase;

class RenameUrbanizationInputAdapterTest {

	private static final UUID ID = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001");

	@Test
	void shouldMapTheRequestToTheDomainAndRunTheUseCase() {
		final RenameUrbanizationUseCase useCase = mock(RenameUrbanizationUseCase.class);
		final RenameUrbanizationInputAdapter adapter = new RenameUrbanizationInputAdapter(useCase, Mappers.getMapper(UrbanizationRenameMapperInput.class));

		adapter.execute(new RenameUrbanizationDtoRequest(ID, "  Conjunto nuevo  "));

		final ArgumentCaptor<UrbanizationRenameDomain> captor = ArgumentCaptor.forClass(UrbanizationRenameDomain.class);
		verify(useCase).execute(captor.capture());
		assertEquals(ID, captor.getValue().urbanizationId());
		assertEquals("Conjunto nuevo", captor.getValue().name());
	}
}
