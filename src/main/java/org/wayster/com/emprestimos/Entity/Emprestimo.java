package org.wayster.com.emprestimos.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.wayster.com.emprestimos.Enums.StatusPagamento;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClientesEntity cliente;

    private Double valorEmprestimo;
    private Double taxaJuros; // 0.40 (40%)
    private Double valorComJuros;
    private LocalDate dataEmprestimo;
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    private StatusPagamento statusPagamento; // "PENDENTE", "PAGO", "ATRASADO"
}
