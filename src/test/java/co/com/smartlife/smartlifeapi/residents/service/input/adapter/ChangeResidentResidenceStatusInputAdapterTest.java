package co.com.smartlife.smartlifeapi.residents.service.input.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import co.com.smartlife.smartlifeapi.residents.domain.ResidentResidenceStatusChangeDomain;
import co.com.smartlife.smartlifeapi.residents.service.input.adapter.mapper.ResidentResidenceStatusMapperInput;
import co.com.smartlife.smartlifeapi.residents.service.input.dto.request.ChangeResidentResidenceStatusDtoRequest;
import co.com.smartlife.smartlifeapi.residents.service.usecase.contract.ChangeResidentResidenceStatusUseCase;

class ChangeResidentResidenceStatusInputAdapterTest {

	private static final UUID ID = UUID.fromString("99999999-0000-0000-0000-000000000001");

	@Test
	void shouldMapTheRequestToTheDomainAndRunTheUseCase() {
		final ChangeResidentResidenceStatusUseCase useCase = mock(ChangeResidentResidenceStatusUseCase.class);
		final ChangeResidentResidenceStatusInputAdapter adapter = new ChangeResidentResidenceStatusInputAdapter(useCase, Mappers.getMapper(ResidentResidenceStatusMapperInput.class));

		adapter.execute(new ChangeResidentResidenceStatusDtoRequest(ID, true));

		final ArgumentCaptor<ResidentResidenceStatusChangeDomain> captor = ArgumentCaptor.forClass(ResidentResidenceStatusChangeDomain.class);
		verify(useCase).execute(captor.capture());
		assertEquals(ID, captor.getValue().residentResidenceId());
		assertEquals(Boolean.TRUE, captor.getValue().active());
	}
}
