package org.wayster.com.emprestimos.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientesDto {

    private String nome;
    private String telefone;
    private Long cpf;
    private String endereco;
    private String bairro;
    private String cidade;
    private String estado;
    private String numero;

    private List<EmprestimoDto> emprestimos;
}
