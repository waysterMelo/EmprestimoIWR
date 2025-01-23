package org.wayster.com.emprestimos.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wayster.com.emprestimos.Dto.ClientesDto;
import org.wayster.com.emprestimos.Entity.ClientesEntity;
import org.wayster.com.emprestimos.Mapper.ClientesMapper;
import org.wayster.com.emprestimos.Repository.ClientesRepository;

import java.util.Optional;

@Service
public class ClientesServices {

    private final ClientesRepository clientesRepository;
    private final ClientesMapper clientesMapper;

    @Autowired
    public ClientesServices(ClientesRepository clientesRepository, ClientesMapper clientesMapper) {
        this.clientesRepository = clientesRepository;
        this.clientesMapper = clientesMapper;
    }

    public ClientesDto cadastrarCliente(ClientesDto clientesDto) {
        ClientesEntity clienteEntity = clientesMapper.toEntity(clientesDto);
        ClientesEntity clienteSalvo = clientesRepository.save(clienteEntity);
        return clientesMapper.toDto(clienteSalvo);
    }
}
