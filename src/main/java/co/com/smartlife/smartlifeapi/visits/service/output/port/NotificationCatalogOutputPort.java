package co.com.smartlife.smartlifeapi.visits.service.output.port;

import co.com.smartlife.smartlifeapi.visits.service.output.dto.NotificationCatalogDTO;

public interface NotificationCatalogOutputPort {

	NotificationCatalogDTO findByCodeAndLocale(String code, String locale);
}