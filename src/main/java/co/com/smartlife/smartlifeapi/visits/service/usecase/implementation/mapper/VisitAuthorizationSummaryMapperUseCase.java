package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.mapper;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationSummaryDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitorEntity;

/** Entity -> dominio de CONSULTA. Es independiente de {@link VisitAuthorizationMapperUseCase}, que es el del registro. */
@Mapper(componentModel = "spring", imports = TextHelper.class)
public interface VisitAuthorizationSummaryMapperUseCase {

	@Mapping(target = "propertyLocation", source = "property.location", defaultValue = "")
	@Mapping(target = "visitReason", source = "visitReason.name", defaultValue = "")
	@Mapping(target = "documentAttached", expression = "java(!TextHelper.isEmptyWithTrim(authorization.getFileReference()))")
	@Mapping(target = "visitorDocumentNumber", source = "visitor.documentNumber")
	@Mapping(target = "visitorFullName", source = "visitor", qualifiedByName = "visitorFullName")
	@Mapping(target = "visitorEmail", source = "visitor.email")
	@Mapping(target = "vehiclePlate", source = "visitor.vehiclePlate", defaultValue = "")
	VisitAuthorizationSummaryDomain toDomain(VisitAuthorizationEntity authorization);

	@Named("visitorFullName")
	default String visitorFullName(final VisitorEntity visitor) {
		if (visitor == null) {
			return TextHelper.getDefault();
		}
		return Stream.of(visitor.getFirstName(), visitor.getMiddleName(), visitor.getLastName(),
				visitor.getSecondLastName()).filter(part -> !TextHelper.isEmptyWithTrim(part))
				.collect(Collectors.joining(" "));
	}
}
