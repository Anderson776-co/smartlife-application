package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.notificationcatalog.strapi.dto;

import java.util.List;

public record StrapiNotificationCatalogResponse(List<StrapiNotificationCatalogData> data) {
}