package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitReasonDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitReasonRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitReasonEntity;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.mapper.VisitReasonMapperUseCase;

class ConsultVisitReasonsUseCaseImplTest {

	private static final VisitReasonEntity FAMILY = new VisitReasonEntity(UUID.randomUUID(), "Visita familiar", false);
	private static final VisitReasonEntity TECHNICAL = new VisitReasonEntity(UUID.randomUUID(), "Servicio tecnico", true);

	private VisitReasonRepository visitReasonRepository;
	private ConsultVisitReasonsUseCaseImpl useCase;

	@BeforeEach
	void setUp() {
		visitReasonRepository = mock(VisitReasonRepository.class);
		useCase = new ConsultVisitReasonsUseCaseImpl(visitReasonRepository,
				Mappers.getMapper(VisitReasonMapperUseCase.class));
	}

	@Test
	void shouldReturnEveryVisitReasonMappedToTheDomain() {
		when(visitReasonRepository.findAll()).thenReturn(List.of(FAMILY, TECHNICAL));

		final List<VisitReasonDomain> result = useCase.list();

		assertEquals(List.of(new VisitReasonDomain(FAMILY.getId(), "Visita familiar", false),
				new VisitReasonDomain(TECHNICAL.getId(), "Servicio tecnico", true)), result);
		verify(visitReasonRepository).findAll();
	}

	@Test
	void shouldReturnAnEmptyListWhenThereAreNoVisitReasons() {
		when(visitReasonRepository.findAll()).thenReturn(List.of());

		assertTrue(useCase.list().isEmpty());
	}

}
