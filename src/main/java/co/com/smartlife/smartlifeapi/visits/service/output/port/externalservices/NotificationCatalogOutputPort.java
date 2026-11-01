package co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices;

import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.NotificationCatalogDTO;

public interface NotificationCatalogOutputPort {

	NotificationCatalogDTO findByCodeAndLocale(String code, String locale);
}