package org.wayster.com.emprestimos.Controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wayster.com.emprestimos.Dto.ClienteComEmprestimosDto;
import org.wayster.com.emprestimos.Dto.ClientesDto;
import org.wayster.com.emprestimos.Mapper.ClientesMapper;
import org.wayster.com.emprestimos.Service.ClientesServices;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClientesServices clientesServices;
    private final ClientesMapper clientesMapper;


    @Autowired
    public ClienteController(ClientesServices clientesServices, ClientesMapper clientesMapper) {
        this.clientesServices = clientesServices;
        this.clientesMapper = clientesMapper;
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> cadastrarCliente(
            @RequestPart("nome") String nome,
            @RequestPart("email") String email,
            @RequestPart("telefone") String telefone,
            @RequestPart("cpf") String cpf,
            @RequestPart("limitePagamento") String limitePagamento,
            @RequestPart(value = "foto", required = false) MultipartFile foto,
            @RequestPart("endereco") String endereco,
            @RequestPart("bairro") String bairro,
            @RequestPart("cidade") String cidade,
            @RequestPart("estado") String estado,
            @RequestPart("numero") String numero
    ) {
        try {
            ClientesDto clientesDto = ClientesDto.builder()
                    .nome(nome)
                    .email(email)
                    .telefone(telefone)
                    .cpf(cpf)
                    .limitePagamento(new BigDecimal(limitePagamento))
                    .foto(foto != null ? foto.getBytes() : null)
                    .endereco(endereco)
                    .bairro(bairro)
                    .cidade(cidade)
                    .estado(estado)
                    .numero(numero)
                    .build();

            var clienteCadastrado = clientesServices.cadastrarCliente(clientesDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteCadastrado);
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }




    @GetMapping("/buscar-por-cpf/{cpf}")
    public ResponseEntity<ClientesDto> buscarClientesPorCpf(@PathVariable String cpf){
        return clientesServices.buscarClientePorCpf(cpf)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @GetMapping("/buscar-com-emprestimos/{cpf}")
    public ResponseEntity<ClienteComEmprestimosDto> buscarClienteComEmprestimosPorCpf(@PathVariable String cpf) {
        return clientesServices.buscarClienteComEmprestimosPorCpf(cpf)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<ClientesDto> atualizarCliente(@PathVariable Long id, @RequestBody ClientesDto clienteDto){
            return clientesServices.atualizarCliente(id, clienteDto)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        }



    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarCliente(@PathVariable Long id){
        String resultado = clientesServices.deletarCliente(id);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/buscar-por-nome")
    public ResponseEntity<List<ClientesDto>> buscarClientesPorNome(@RequestParam String nome) {
        List<ClientesDto> clientes = clientesServices.buscarClientesPorNome(nome);
        return ResponseEntity.ok(clientes);
    }


    @GetMapping("/todos")
    public ResponseEntity<List<ClientesDto>> buscarTodosClientes() {
        List<ClientesDto> clientes = clientesServices.buscarTodosClientes();
        return ResponseEntity.ok(clientes);
    }


    @GetMapping(value = "/foto/{cpf}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> buscarFotoCliente(@PathVariable String cpf) {
        return clientesServices.buscarClientePorCpf(cpf)
                .filter(cliente -> cliente.getFoto() != null)
                .map(cliente -> ResponseEntity.ok().body(cliente.getFoto()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }




}
