package org.wayster.com.emprestimos.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wayster.com.emprestimos.Enums.StatusPagamento;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmprestimoComClienteDTO {

    // Dados do empréstimo
    private Long id;
    private Double valorEmprestimo;
    private Double taxaJuros;
    private Double valorComJuros;
    private LocalDate dataEmprestimo;
    private LocalDate dataVencimento;
    private String observacao;
    private StatusPagamento statusPagamento;

    // Dados do cliente
    private Long clienteId;
    private String clienteNome;
    private String clienteCpf;
    private String clienteTelefone;


}
