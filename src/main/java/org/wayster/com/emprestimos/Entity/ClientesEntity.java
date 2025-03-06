package org.wayster.com.emprestimos.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "clientes")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String telefone;

    @CPF
    @Column(nullable = false, unique = true)
    private String cpf;

    private String endereco;
    private String bairro;
    private String cidade;
    private String estado;
    private String numero;

    @Column(name = "limite_pagamento")
    private BigDecimal limitePagamento;

    @Lob
    @Column(name = "foto")
    private byte[] foto;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
    }
}