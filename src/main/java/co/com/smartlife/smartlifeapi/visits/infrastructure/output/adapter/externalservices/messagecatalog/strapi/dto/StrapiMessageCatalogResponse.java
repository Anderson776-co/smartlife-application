package co.com.smartlife.smartlifeapi.visits.infrastructure.output.externalservices.messagecatalog.strapi.dto;

import java.util.List;

public record StrapiMessageCatalogResponse(List<StrapiMessageCatalogData> data) {
}