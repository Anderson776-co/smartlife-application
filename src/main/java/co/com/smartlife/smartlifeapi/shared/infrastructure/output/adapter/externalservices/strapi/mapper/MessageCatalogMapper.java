package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.strapi.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.strapi.dto.StrapiMessageCatalogData;
import co.com.smartlife.smartlifeapi.shared.mapper.DtoMapper;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.dto.MessageCatalogDTO;

@Mapper(componentModel = "spring")
public interface MessageCatalogMapper extends DtoMapper<MessageCatalogDTO, StrapiMessageCatalogData>{

}
