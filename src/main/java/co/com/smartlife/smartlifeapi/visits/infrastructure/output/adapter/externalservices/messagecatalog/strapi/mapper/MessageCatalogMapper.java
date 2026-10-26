package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.messagecatalog.strapi.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.mapper.DtoMapper;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.messagecatalog.strapi.dto.StrapiMessageCatalogData;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.MessageCatalogDTO;

@Mapper(componentModel = "spring")
public interface MessageCatalogMapper extends DtoMapper<MessageCatalogDTO, StrapiMessageCatalogData>{

}
