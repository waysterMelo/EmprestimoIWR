package org.wayster.com.emprestimos.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoPesquisaDTO {

    private List<EmprestimoComClienteDTO> emprestimos;
    private int totalEmprestimos;
    private double valorTotalEmprestado;
    private double valorTotalComJuros;
    private double lucroTotal;
}
