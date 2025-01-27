package org.wayster.com.emprestimos.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteComEmprestimosDto {


    private ClientesDto cliente;
    private List<EmprestimoDto> emprestimos;
}
