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
public class EmprestimoDto {

    private Long id;
    private Long clienteId; // Referência ao cliente
    private Double valorEmprestimo;
    private Double taxaJuros;
    private Double valorComJuros;
    private LocalDate dataEmprestimo;
    private LocalDate dataVencimento;
    private StatusPagamento statusPagamento;

    }