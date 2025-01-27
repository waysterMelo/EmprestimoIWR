package org.wayster.com.emprestimos.Service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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


    public ClientesDto cadastrarCliente(ClientesDto clientesDto) {
        // Verifica se o CPF já existe
        boolean cpfExistente = clientesRepository.existsByCpf((clientesDto.getCpf()));
        if (cpfExistente) {
            throw new IllegalArgumentException("CPF já cadastrado: " + clientesDto.getCpf());
        }

        ClientesEntity clienteEntity = clientesMapper.toEntity(clientesDto);
        ClientesEntity clienteSalvo = clientesRepository.save(clienteEntity);
        return clientesMapper.toDto(clienteSalvo);
    }

    /**
     * Busca um cliente pelo CPF.
     *
     * @param cpf CPF do cliente.
     * @return DTO do cliente, se encontrado.
     */
    public Optional<ClientesDto> buscarClientePorCpf(Long cpf) {
        return clientesRepository.findByCpf(cpf)
                .map(mapperEmprestimo::toDto); // Converte a entidade para DTO usando o mapper
    }

    /**
     * Busca um cliente pelo CPF e retorna os empréstimos associados, se houver.
     *
     * @param cpf CPF do cliente.
     * @return DTO contendo os dados do cliente e os empréstimos associados.
     */
    public Optional<ClienteComEmprestimosDto> buscarClienteComEmprestimosPorCpf(Long cpf) {
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

    /**
     * Atualiza os dados de um cliente existente.
     *
     * @param id          ID do cliente a ser atualizado.
     * @param clienteDto  Dados atualizados do cliente.
     * @return Um Optional contendo o DTO do cliente atualizado, ou vazio se o cliente não for encontrado.
     */
    public Optional<ClientesDto> atualizarCliente(Long id, ClientesDto clienteDto) {
        return clientesRepository.findById(id)
                .map(clienteExistente -> {
                    // Atualiza os campos do cliente existente
                    clienteExistente.setNome(clienteDto.getNome());
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

    /**
     * Deleta um cliente caso ele não possua empréstimos associados.
     *
     * @param id ID do cliente a ser deletado.
     * @return Mensagem indicando sucesso ou erro.
     */
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

    /**
     * Busca todos os clientes cadastrados no sistema.
     *
     * @return Lista de todos os ClientesDto.
     */
    public List<ClientesDto> buscarTodosClientes(){
        return clientesRepository.findAll()
               .stream()
               .map(mapperEmprestimo::toDto)
               .toList();
    }

    /**
     * Busca clientes pelo nome com base em palavras-chave.
     *
     * @param nome Palavra-chave parcial ou completa.
     * @return Lista de ClientesDto correspondentes.
     */
    public List<ClientesDto> buscarClientesPorNome(String nome) {
        return clientesRepository.findByNomeContaining(nome)
                .stream()
                .map(mapperEmprestimo::toDto)
                .toList();
    }

}
