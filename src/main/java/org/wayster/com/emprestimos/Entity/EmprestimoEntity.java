package org.wayster.com.emprestimos.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.wayster.com.emprestimos.Enums.StatusPagamento;

import java.time.LocalDate;

@Table(name = "emprestimos")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmprestimoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClientesEntity cliente;

    private Double valorEmprestimo;
    private Double taxaJuros;
    private Double valorComJuros;
    private Double valorDevidoApenasMostrar;
    private LocalDate dataEmprestimo;
    private LocalDate dataVencimento;
    @Column(length = 1000)
    private String observacao;

    @Enumerated(EnumType.STRING)
    private StatusPagamento statusPagamento; // "PENDENTE", "PAGO", "ATRASADO"
}