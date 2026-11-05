package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitReasonDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.VisitReasonInputMapper;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitReasonDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.ConsultVisitReasonsUseCase;

class ConsultVisitReasonsInputAdapterTest {

	private static final UUID FAMILY_ID = UUID.randomUUID();
	private static final UUID TECHNICAL_ID = UUID.randomUUID();

	private ConsultVisitReasonsUseCase useCase;
	private ConsultVisitReasonsInputAdapter adapter;

	@BeforeEach
	void setUp() {
		useCase = mock(ConsultVisitReasonsUseCase.class);
		adapter = new ConsultVisitReasonsInputAdapter(useCase, Mappers.getMapper(VisitReasonInputMapper.class));
	}

	@Test
	void shouldMapTheDomainReasonsToTheResponse() {
		when(useCase.list()).thenReturn(List.of(new VisitReasonDomain(FAMILY_ID, "Visita familiar", false),
				new VisitReasonDomain(TECHNICAL_ID, "Servicio tecnico", true)));

		final List<VisitReasonDtoResponse> result = adapter.execute();

		assertEquals(List.of(new VisitReasonDtoResponse(FAMILY_ID, "Visita familiar", false),
				new VisitReasonDtoResponse(TECHNICAL_ID, "Servicio tecnico", true)), result);
	}

	@Test
	void shouldReturnAnEmptyListWhenTheUseCaseFindsNoReasons() {
		when(useCase.list()).thenReturn(List.of());

		assertTrue(adapter.execute().isEmpty());
	}

}
