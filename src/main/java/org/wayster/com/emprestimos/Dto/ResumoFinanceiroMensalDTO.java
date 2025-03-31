package org.wayster.com.emprestimos.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumoFinanceiroMensalDTO {


    private Integer mes; // Número do mês (1-12)
    private Double totalEmprestado;
    private Double retornoEsperado;
    private Double lucro; // Calculado como retornoEsperado - totalEmprestado

}
