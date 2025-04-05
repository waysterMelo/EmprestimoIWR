package org.wayster.com.emprestimos.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wayster.com.emprestimos.Enums.StatusPagamento;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PesquisaEmprestimoDTO {

    private LocalDate dataVencimento;
    private String cpf;
    private StatusPagamento statusPagamento;


}
