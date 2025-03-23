package org.wayster.com.emprestimos.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wayster.com.emprestimos.Dto.ClienteComEmprestimosDto;
import org.wayster.com.emprestimos.Dto.ClientesDto;
import org.wayster.com.emprestimos.Dto.EmprestimoDto;
import org.wayster.com.emprestimos.Entity.ClientesEntity;
import org.wayster.com.emprestimos.Mapper.ClientesMapper;
import org.wayster.com.emprestimos.Mapper.MapperEmprestimo;
import org.wayster.com.emprestimos.Repository.ClientesRepository;
import org.wayster.com.emprestimos.Repository.EmprestimoRepository;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientesServices {

    private final ClientesRepository clientesRepository;
    private final ClientesMapper clientesMapper;
    private final MapperEmprestimo mapperEmprestimo;
    private final EmprestimoRepository emprestimoRepository;



    @Transactional
    public ClientesDto cadastrarCliente(ClientesDto clientesDto) {
        // Usa o mapper para converter DTO para Entity
        ClientesEntity clienteEntity = mapperEmprestimo.toEntity(clientesDto);

        // Salva no banco de dados
        ClientesEntity clienteSalvo = clientesRepository.save(clienteEntity);

        // Usa o mapper para converter Entity de volta para DTO
        return mapperEmprestimo.toDto(clienteSalvo);
    }

    public Optional<ClientesDto> buscarClientePorCpf(String cpf) {
        return clientesRepository.findByCpf(cpf)
                .map(mapperEmprestimo::toDto);
    }


    public Optional<ClienteComEmprestimosDto> buscarClienteComEmprestimosPorCpf(String cpf) {
        return clientesRepository.findByCpf(cpf)
                .map(cliente -> {
                    // Converte o cliente para DTO
                    ClientesDto clienteDto = mapperEmprestimo.toDto(cliente);

                    // Busca os empréstimos associados e os converte para DTO
                    List<EmprestimoDto> emprestimos = emprestimoRepository.findByClienteId(cliente.getId())
                            .stream()
                            .map(mapperEmprestimo::toDto)
                            .toList();

                    // Retorna um DTO combinado com cliente e empréstimos
                    return new ClienteComEmprestimosDto(clienteDto, emprestimos);
                });
    }


    public Optional<ClientesDto> atualizarCliente(Long id, ClientesDto clienteDto) {
        return clientesRepository.findById(id)
                .map(clienteExistente -> {
                    // Atualiza os campos do cliente existente
                    clienteExistente.setNome(clienteDto.getNome());
                    clienteExistente.setEmail(clienteDto.getEmail());
                    clienteExistente.setTelefone(clienteDto.getTelefone());
                    clienteExistente.setCpf(clienteDto.getCpf());
                    clienteExistente.setEndereco(clienteDto.getEndereco());
                    clienteExistente.setBairro(clienteDto.getBairro());
                    clienteExistente.setCidade(clienteDto.getCidade());
                    clienteExistente.setEstado(clienteDto.getEstado());
                    clienteExistente.setNumero(clienteDto.getNumero());
                    // Salva as alterações no banco de dados
                    ClientesEntity clienteAtualizado = clientesRepository.save(clienteExistente);

                    // Converte a entidade atualizada para DTO e retorna
                    return mapperEmprestimo.toDto(clienteAtualizado);
                });
    }


     public String deletarCliente(Long id){
         // Verifica se o cliente existe
         ClientesEntity cliente  = clientesRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cliente com ID " + id + " não encontrado."));

         // Verifica se o cliente tem empréstimos associados
         boolean possuiEmprestimos = emprestimoRepository.existsByClienteId(id);
         if (possuiEmprestimos){
             return "O cliente com ID " + id + " não pode ser deletado porque possui empréstimos associados.";
         }

         // Exclui o cliente se não houver empréstimos
         clientesRepository.delete(cliente);
         return "Cliente com ID " + id + " deletado com sucesso.";
     }


    public List<ClientesDto> buscarTodosClientes(){
        return clientesRepository.findAll()
               .stream()
               .map(mapperEmprestimo::toDto)
               .toList();
    }


    public List<ClientesDto> buscarClientesPorNome(String nome) {
        return clientesRepository.findByNomeContaining(nome)
                .stream()
                .map(mapperEmprestimo::toDto)
                .toList();
    }

    


}
