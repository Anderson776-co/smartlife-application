package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.messagecatalog.strapi.dto;

import java.util.List;

public record StrapiMessageCatalogResponse(List<StrapiMessageCatalogData> data) {
}