package org.wayster.com.emprestimos.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ResumoEmprestimosVencidos {

    private List<EmprestimoDto> emprestimosVencidos;
    private double somaValoresEmprestados;
    private double somaValoresAReceber;
    private double lucro;

}
