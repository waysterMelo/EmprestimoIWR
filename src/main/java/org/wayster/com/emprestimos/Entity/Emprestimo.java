package org.wayster.com.emprestimos.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Clientes cliente;

    private Double valorEmprestimo;
    private Double taxaJuros; // 0.40 (40%)
    private Double valorComJuros;
    private LocalDate dataEmprestimo;
    private LocalDate dataVencimento;
    private String statusPagamento; // "PENDENTE", "PAGO", "ATRASADO"
}
