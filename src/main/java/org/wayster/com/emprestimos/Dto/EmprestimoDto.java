package org.wayster.com.emprestimos.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
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
    @NotNull(message = "O clienteId não pode ser nulo.")
    private Long clienteId;

    @NotNull(message = "O valor do empréstimo não pode ser nulo.")
    @Positive(message = "O valor do empréstimo deve ser maior que zero.")
    private Double valorEmprestimo;

    @NotNull(message = "A taxa de juros não pode ser nula.")
    private Double taxaJuros;
    private Double valorComJuros;

    @FutureOrPresent(message = "A data do empréstimo não pode estar no passado.")
    private LocalDate dataEmprestimo;

    @Future(message = "A data de vencimento deve ser uma data futura.")
    private LocalDate dataVencimento;

    private String observacao;

    private StatusPagamento statusPagamento;

    }