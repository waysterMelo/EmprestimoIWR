package org.wayster.com.emprestimos.Controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wayster.com.emprestimos.Dto.ClientesDto;
import org.wayster.com.emprestimos.Service.ClientesServices;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClientesServices clientesServices;


    @Autowired
    public ClienteController(ClientesServices clientesServices) {
        this.clientesServices = clientesServices;
    }

    @PostMapping
    public ResponseEntity<ClientesDto> salvarClientes(@RequestBody ClientesDto dto){
       ClientesDto clienteSalvo = clientesServices.cadastrarCliente(dto);
       return ResponseEntity.ok(clienteSalvo);
    }



}
