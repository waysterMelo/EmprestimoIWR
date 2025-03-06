package org.wayster.com.emprestimos.Dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientesDto {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "^\\(\\d{2}\\)\\s?\\d{4,5}-\\d{4}$", message = "Telefone inválido")
    private String telefone;

    @NotBlank(message = "CPF é obrigatório")
    @CPF(message = "CPF inválido")
    private String cpf;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    private String endereco;
    private String bairro;
    private String cidade;
    private String estado;
    private String numero;

    @NotNull(message = "Limite de pagamento é obrigatório")
    @Positive(message = "Limite de pagamento deve ser positivo")
    private BigDecimal limitePagamento;

    @Column(columnDefinition = "LONGBLOB")
    private byte[] foto;

    private LocalDateTime dataCadastro;

    private List<EmprestimoDto> emprestimos;
}