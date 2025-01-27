package org.wayster.com.emprestimos.Controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wayster.com.emprestimos.Dto.ClienteComEmprestimosDto;
import org.wayster.com.emprestimos.Dto.ClientesDto;
import org.wayster.com.emprestimos.Service.ClientesServices;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClientesServices clientesServices;


    @Autowired
    public ClienteController(ClientesServices clientesServices) {
        this.clientesServices = clientesServices;
    }

    @PostMapping
    public ResponseEntity<?> cadastrarCliente(@RequestBody ClientesDto clientesDto) {
        try {
            var clienteCadastrado = clientesServices.cadastrarCliente(clientesDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteCadastrado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint para buscar um cliente pelo CPF.
     *
     * @param cpf CPF do cliente.
     * @return ResponseEntity com o DTO do cliente ou status NOT_FOUND.
     */
    @GetMapping("/buscar-por-cpf/{cpf}")
    public ResponseEntity<ClientesDto> buscarClientesPorCpf(@PathVariable Long cpf){
        return clientesServices.buscarClientePorCpf(cpf)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }



    /**
     * Endpoint para buscar cliente pelo CPF e incluir empréstimos associados.
     *
     * @param cpf CPF do cliente.
     * @return ResponseEntity com os dados do cliente e empréstimos ou status NOT_FOUND.
     */
    @GetMapping("/buscar-com-emprestimos/{cpf}")
    public ResponseEntity<ClienteComEmprestimosDto> buscarClienteComEmprestimosPorCpf(@PathVariable Long cpf) {
        return clientesServices.buscarClienteComEmprestimosPorCpf(cpf)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Endpoint para atualizar os dados de um cliente.
     *
     * @param id           ID do cliente a ser atualizado.
     * @param clienteDto   Dados atualizados do cliente.
     * @return ResponseEntity com o cliente atualizado ou status NOT_FOUND se o cliente não for encontrado.
     */
        @PutMapping("/{id}")
        public ResponseEntity<ClientesDto> atualizarCliente(@PathVariable Long id, @RequestBody ClientesDto clienteDto){
            return clientesServices.atualizarCliente(id, clienteDto)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        }


    /**
     * Endpoint para deletar um cliente.
     * Verifica se o cliente tem empréstimos antes de excluí-lo.
     *
     * @param id ID do cliente a ser deletado.
     * @return ResponseEntity com mensagem de sucesso ou erro.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarCliente(@PathVariable Long id){
        String resultado = clientesServices.deletarCliente(id);
        return ResponseEntity.ok(resultado);
    }
    /**
     * Busca clientes pelo nome.
     * @param nome Palavra-chave parcial ou completa.
     * @return Lista de clientes correspondentes.
     */
    @GetMapping("/buscar-por-nome")
    public ResponseEntity<List<ClientesDto>> buscarClientesPorNome(@RequestParam String nome) {
        List<ClientesDto> clientes = clientesServices.buscarClientesPorNome(nome);
        return ResponseEntity.ok(clientes);
    }

    /**
     * Busca todos os clientes cadastrados.
     * @return Lista de todos os clientes.
     */
    @GetMapping("/todos")
    public ResponseEntity<List<ClientesDto>> buscarTodosClientes() {
        List<ClientesDto> clientes = clientesServices.buscarTodosClientes();
        return ResponseEntity.ok(clientes);
    }


}
