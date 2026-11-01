package co.com.smartlife.smartlifeapi.shared.service.input.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.shared.domain.catalog.CatalogEventDomain;
import co.com.smartlife.smartlifeapi.shared.domain.catalog.CatalogInvalidationDomain;
import co.com.smartlife.smartlifeapi.shared.domain.catalog.CatalogTypeDomain;
import co.com.smartlife.smartlifeapi.shared.service.input.dto.request.CatalogWebhookDtoRequest;

/** DTO del webhook de Strapi a dominio. */
@Mapper(componentModel = "spring")
public interface CatalogWebhookMapperInput {

	@Mapping(target = "event", source = "event")
	@Mapping(target = "catalog", source = "model")
	@Mapping(target = "code", source = "entry.code")
	@Mapping(target = "locale", source = "entry.locale")
	CatalogInvalidationDomain toDomain(CatalogWebhookDtoRequest request);

	default CatalogEventDomain toEvent(final String event) {
		return switch (event) {
		case "entry.publish" -> CatalogEventDomain.PUBLISH;
		case "entry.unpublish" -> CatalogEventDomain.UNPUBLISH;
		case "entry.delete" -> CatalogEventDomain.DELETE;
		default -> CatalogEventDomain.OTHER;
		};
	}

	default CatalogTypeDomain toCatalog(final String model) {
		return switch (model) {
		case "message-catalog" -> CatalogTypeDomain.MESSAGE;
		case "notification-catalog" -> CatalogTypeDomain.NOTIFICATION;
		default -> CatalogTypeDomain.UNKNOWN;
		};
	}
}
