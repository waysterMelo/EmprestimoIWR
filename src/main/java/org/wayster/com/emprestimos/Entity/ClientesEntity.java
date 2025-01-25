package org.wayster.com.emprestimos.Entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "clientes")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String telefone;
    private Long cpf;
    private String endereco;
    private String bairro;
    private String cidade;
    private String estado;
    private String numero;
}
