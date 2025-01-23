package org.wayster.com.emprestimos.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.wayster.com.emprestimos.Dto.ClientesDto;
import org.wayster.com.emprestimos.Entity.ClientesEntity;

@Mapper(componentModel = "spring")
public interface ClientesMapper {


    ClientesMapper INSTANCE = Mappers.getMapper(ClientesMapper.class);

    // Mapeia de Entity para DTO
        ClientesDto toDto(ClientesEntity clientesEntity);
    // Mapeia de DTO para Entity
        ClientesEntity toEntity(ClientesDto clientesDto);

}
