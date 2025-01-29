package org.wayster.com.emprestimos.Mapper;

import org.springframework.stereotype.Component;
import org.wayster.com.emprestimos.Dto.ClientesDto;
import org.wayster.com.emprestimos.Dto.EmprestimoDto;
import org.wayster.com.emprestimos.Entity.ClientesEntity;
import org.wayster.com.emprestimos.Entity.EmprestimoEntity;

import java.util.Optional;

@Component
public class MapperEmprestimo {

    /**
     * Converte um `EmprestimoDto` em uma entidade `EmprestimoEntity`.
     *
     * @param emprestimoDto O DTO do empréstimo.
     * @param clienteEntity O cliente relacionado ao empréstimo.
     * @return A entidade `EmprestimoEntity`.
     */
    public EmprestimoEntity toEntity(EmprestimoDto emprestimoDto, ClientesEntity clienteEntity) {
        return Optional.ofNullable(emprestimoDto)
                .map(dto -> EmprestimoEntity.builder()
                        .id(dto.getId())
                        .cliente(clienteEntity)
                        .valorEmprestimo(dto.getValorEmprestimo())
                        .taxaJuros(dto.getTaxaJuros())
                        .valorComJuros(dto.getValorComJuros())
                        .dataEmprestimo(dto.getDataEmprestimo())
                        .dataVencimento(dto.getDataVencimento())
                        .statusPagamento(dto.getStatusPagamento())
                        .observacao(dto.getObservacao())
                        .build()
                ).orElse(null);
    }

    /**
     * Converte uma entidade `EmprestimoEntity` em um `EmprestimoDto`.
     *
     * @param emprestimoEntity A entidade de empréstimo.
     * @return O DTO correspondente.
     */
    public EmprestimoDto toDto(EmprestimoEntity emprestimoEntity) {
        return Optional.ofNullable(emprestimoEntity)
                .map(entity -> EmprestimoDto.builder()
                        .id(entity.getId())
                        .clienteId(Optional.ofNullable(entity.getCliente())
                                .map(ClientesEntity::getId)
                                .orElse(null))
                        .valorEmprestimo(entity.getValorEmprestimo())
                        .taxaJuros(entity.getTaxaJuros())
                        .valorComJuros(entity.getValorComJuros())
                        .dataEmprestimo(entity.getDataEmprestimo())
                        .dataVencimento(entity.getDataVencimento())
                        .statusPagamento(entity.getStatusPagamento())
                        .observacao(entity.getObservacao())
                        .build()
                ).orElse(null);
    }

    /**
     * Converte um `ClientesDto` em uma entidade `ClientesEntity`.
     *
     * @param clientesDto O DTO do cliente.
     * @return A entidade `ClientesEntity`.
     */
    public ClientesEntity toEntity(ClientesDto clientesDto) {
        return Optional.ofNullable(clientesDto)
                .map(dto -> new ClientesEntity(
                        null, // O ID será gerado automaticamente
                        dto.getNome(),
                        dto.getTelefone(),
                        dto.getCpf(),
                        dto.getEndereco(),
                        dto.getBairro(),
                        dto.getCidade(),
                        dto.getEstado(),
                        dto.getNumero()
                )).orElse(null);
    }

    /**
     * Converte uma entidade `ClientesEntity` em um `ClientesDto`.
     *
     * @param clientesEntity A entidade do cliente.
     * @return O DTO correspondente.
     */
    public ClientesDto toDto(ClientesEntity clientesEntity) {
        return Optional.ofNullable(clientesEntity)
                .map(entity -> new ClientesDto(
                        entity.getNome(),
                        entity.getTelefone(),
                        entity.getCpf(),
                        entity.getEndereco(),
                        entity.getBairro(),
                        entity.getCidade(),
                        entity.getEstado(),
                        entity.getNumero(),
                        null
                )).orElse(null);
    }
}
