package org.wayster.com.emprestimos.Mapper;

import org.springframework.stereotype.Component;
import org.wayster.com.emprestimos.Dto.ClientesDto;
import org.wayster.com.emprestimos.Dto.EmprestimoDto;
import org.wayster.com.emprestimos.Entity.ClientesEntity;
import org.wayster.com.emprestimos.Entity.EmprestimoEntity;

import java.util.Optional;

@Component
public class MapperEmprestimo {

    public ClientesEntity toEntity(ClientesDto clientesDto) {
        return Optional.ofNullable(clientesDto)
                .map(dto -> ClientesEntity.builder()
                        .id(dto.getId())
                        .nome(dto.getNome())
                        .email(dto.getEmail())
                        .telefone(dto.getTelefone())
                        .cpf(dto.getCpf())
                        .endereco(dto.getEndereco())
                        .bairro(dto.getBairro())
                        .cidade(dto.getCidade())
                        .estado(dto.getEstado())
                        .numero(dto.getNumero())
                        .limitePagamento(dto.getLimitePagamento())
                        .foto(dto.getFoto())
                        .build())
                .orElse(null);
    }

    public ClientesDto toDto(ClientesEntity clientesEntity) {
        return Optional.ofNullable(clientesEntity)
                .map(entity -> ClientesDto.builder()
                        .id(entity.getId())
                        .nome(entity.getNome())
                        .email(entity.getEmail())
                        .telefone(entity.getTelefone())
                        .cpf(entity.getCpf())
                        .endereco(entity.getEndereco())
                        .bairro(entity.getBairro())
                        .cidade(entity.getCidade())
                        .estado(entity.getEstado())
                        .numero(entity.getNumero())
                        .limitePagamento(entity.getLimitePagamento())
                        .foto(entity.getFoto())
                        .dataCadastro(entity.getDataCadastro())
                        .build())
                .orElse(null);
    }


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
}