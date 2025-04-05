package org.wayster.com.emprestimos.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wayster.com.emprestimos.Dto.*;
import org.wayster.com.emprestimos.EmprestimoUtils.EmprestimoUtils;
import org.wayster.com.emprestimos.Entity.EmprestimoEntity;
import org.wayster.com.emprestimos.Enums.StatusPagamento;
import org.wayster.com.emprestimos.Mapper.MapperEmprestimo;
import org.wayster.com.emprestimos.Repository.ClientesRepository;
import org.wayster.com.emprestimos.Repository.EmprestimoRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final ClientesRepository clientesRepository;
    private final MapperEmprestimo emprestimosMapper;
    private final MapperEmprestimo mapperEmprestimo;
    private final WhatsAppService whatsAppService;


    public Optional<EmprestimoDto> cadastrarEmprestimo(EmprestimoDto emprestimoDto) {
        return clientesRepository.findById(emprestimoDto.getClienteId())
                .map(cliente -> {
                    // Calcula o valor com juros
                    double valorComJuros = EmprestimoUtils.calcularValorComJuros(
                            emprestimoDto.getValorEmprestimo(),
                            emprestimoDto.getTaxaJuros()
                    );

                    // Preenche campos do DTO
                    emprestimoDto.setValorComJuros(valorComJuros);
                    emprestimoDto.setDataEmprestimo(LocalDate.now());

                    // Se não veio data de vencimento do front, use 30 dias à frente como fallback:
                    if (emprestimoDto.getDataVencimento() == null) {
                        emprestimoDto.setDataVencimento(emprestimoDto.getDataEmprestimo().plusDays(30));
                    }

                    emprestimoDto.setStatusPagamento(StatusPagamento.PENDENTE);

                    // Converte DTO para Entity e salva no banco
                    EmprestimoEntity emprestimoEntity = emprestimosMapper.toEntity(emprestimoDto, cliente);
                    EmprestimoEntity emprestimoSalvo = emprestimoRepository.save(emprestimoEntity);

                    emprestimoSalvo.setValorDevidoApenasMostrar(emprestimoSalvo.getValorComJuros());

                    // Salva novamente o empréstimo com o campo atualizado
                    emprestimoRepository.save(emprestimoSalvo);

                    DateTimeFormatter formatterBR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                    // Envia via template do WhatsApp (sem valor pago)
                    whatsAppService.enviarTemplateEmprestimo(
                            cliente.getTelefone(),
                            String.format("%.2f", emprestimoSalvo.getValorEmprestimo()),  // valor_creditado
                            String.format("%.2f", emprestimoSalvo.getValorEmprestimo()), // valor pego
                            String.format("%.2f", emprestimoSalvo.getValorComJuros()),    // valor_com_juros
                            emprestimoSalvo.getDataVencimento().format(formatterBR)               // data_vencimento
                    );


                    // Retorna DTO
                    return emprestimosMapper.toDto(emprestimoSalvo);
                })
                .or(() -> {
                    throw new IllegalArgumentException("❌ Cliente não encontrado para o ID fornecido.");
                });
    }


    public Optional<ClientesDto> buscarClientePorCpfComEmprestimos(String cpf){
        return clientesRepository.findByCpf(cpf)
                .map(cliente -> {
                    ClientesDto clientedto = emprestimosMapper.toDto(cliente);
                    List<EmprestimoDto> emprestimos = emprestimoRepository.findByClienteId(cliente.getId())
                            .stream()
                            .map(mapperEmprestimo::toDto)
                            .toList();
                    clientedto.setEmprestimos(emprestimos);
                    return clientedto;
                });
    }


    public Optional<EmprestimoDto> realizarBaixaPagamento(Long emprestimoId){
        return emprestimoRepository.findById(emprestimoId)
                .map(emprestimo -> {
                    emprestimo.setStatusPagamento(StatusPagamento.PAGO);
                    EmprestimoEntity emprestimoAtualizado = emprestimoRepository.save(emprestimo);
                    return mapperEmprestimo.toDto(emprestimoAtualizado);
                });
    }


    public ResumoEmprestimosVencidos buscarEmprestimosVencidosHoje(){
        LocalDate hoje = LocalDate.now();
        List<EmprestimoEntity> emprestimosVencidos = emprestimoRepository.findByDataVencimento((hoje));

        // Mapeia para DTO
        List<EmprestimoDto> emprestimosDto = emprestimosVencidos
                .stream()
                .map(mapperEmprestimo::toDto)
                .toList();

        // Calcula somas
        double valoresEmprestados = emprestimosVencidos
                .stream()
                .mapToDouble(EmprestimoEntity::getValorEmprestimo)
                .sum();

        double valoresAReceber = emprestimosVencidos
                .stream()
                .mapToDouble(EmprestimoEntity::getValorComJuros)
                .sum();

        double lucro = valoresAReceber - valoresEmprestados;

        return new ResumoEmprestimosVencidos(emprestimosDto, valoresEmprestados, valoresAReceber, lucro);
    }


    public Optional<EmprestimoDto> pagarParcialmente(Long emprestimoId, Double valorPago){
        return emprestimoRepository.findById(emprestimoId)
                .map(emprestimo -> {
                    // Verifica se o pagamento é válido
                    if (valorPago <= 0 || valorPago > emprestimo.getValorComJuros()) {
                        throw new IllegalArgumentException("O valor pago é inválido ou excede o valor devido.");
                    }
                    // Subtrai do valor com juros
                    double novoValorDevido = emprestimo.getValorComJuros() - valorPago;
                    emprestimo.setValorComJuros(novoValorDevido);

                    // Atualiza observação
                    String observacaoAtual = Optional.ofNullable(emprestimo.getObservacao()).orElse("");
                    String novaObservacao = String.format(
                            "Pagamento parcial de R$%.2f realizado em %s. Valor restante: R$%.2f.",
                            valorPago, LocalDate.now(), novoValorDevido
                    );
                    emprestimo.setObservacao(
                            observacaoAtual + (observacaoAtual.isEmpty() ? "" : " ") + novaObservacao
                    );

                    // Se quitar tudo, marca como PAGO
                    if (novoValorDevido == 0) {
                        emprestimo.setStatusPagamento(StatusPagamento.PAGO);
                    }

                    // Salva alterações
                    emprestimoRepository.save(emprestimo);

                    // Retorna DTO atualizado
                    return mapperEmprestimo.toDto(emprestimo);
                });
    }


    public ResultadoPesquisaDTO pesquisarEmprestimos(PesquisaEmprestimoDTO filtro) {
        List<EmprestimoEntity> resultados;

        // Verificação dos critérios para definir qual query utilizar
        if (filtro.getCpf() != null && !filtro.getCpf().isBlank()) {

            if (filtro.getDataVencimento() != null) {
                // CPF + Data de vencimento
                if (filtro.getStatusPagamento() != null) {
                    // CPF + Data de vencimento + Status
                    resultados = emprestimoRepository.findByClienteCpfAndDataVencimentoAndStatusPagamento(
                            filtro.getCpf(),
                            filtro.getDataVencimento(),
                            filtro.getStatusPagamento()
                    );
                } else {
                    // CPF + Data de vencimento (sem status)
                    resultados = emprestimoRepository.findByClienteCpfAndDataVencimento(
                            filtro.getCpf(),
                            filtro.getDataVencimento()
                    );
                }
            } else if (filtro.getStatusPagamento() != null) {
                // CPF + Status (sem data)
                resultados = emprestimoRepository.findByClienteCpfAndStatusPagamento(
                        filtro.getCpf(),
                        filtro.getStatusPagamento()
                );
            } else {
                // Apenas CPF
                resultados = emprestimoRepository.findByClienteCpfSemFormatacao(filtro.getCpf());
            }
        } else if (filtro.getDataVencimento() != null) {
            // Sem CPF, mas com data de vencimento
            if (filtro.getStatusPagamento() != null) {
                // Data + Status
                resultados = emprestimoRepository.findByDataVencimentoAndStatusPagamentoOrderByValorEmprestimoDesc((
                        filtro.getDataVencimento()),
                        filtro.getStatusPagamento()
                );
            } else {
                // Apenas data
                resultados = emprestimoRepository.findByDataVencimentoOrderByValorEmprestimoDesc((
                        filtro.getDataVencimento()
                ));
            }
        } else if (filtro.getStatusPagamento() != null) {
            // Apenas status
            resultados = emprestimoRepository.findByStatusPagamentoOrderByDataVencimentoAsc((
                    filtro.getStatusPagamento()
            ));
        } else {
            // Nenhum critério fornecido - retorna lista vazia
            resultados = Collections.emptyList();
        }

        // Mapeamento para o DTO com dados do cliente
        List<EmprestimoComClienteDTO> emprestimosDtos = resultados.stream()
                .map(this::mapToEmprestimoComClienteDTO)
                .collect(Collectors.toList());

        // Cálculo de estatísticas
        double valorTotalEmprestado = resultados.stream()
                .mapToDouble(EmprestimoEntity::getValorEmprestimo)
                .sum();

        double valorTotalComJuros = resultados.stream()
                .mapToDouble(EmprestimoEntity::getValorComJuros)
                .sum();

        double lucroTotal = valorTotalComJuros - valorTotalEmprestado;

        // Montagem do DTO de resultado
        return ResultadoPesquisaDTO.builder()
                .emprestimos(emprestimosDtos)
                .totalEmprestimos(emprestimosDtos.size())
                .valorTotalEmprestado(valorTotalEmprestado)
                .valorTotalComJuros(valorTotalComJuros)
                .lucroTotal(lucroTotal)
                .build();
    }


    private EmprestimoComClienteDTO mapToEmprestimoComClienteDTO(EmprestimoEntity entity) {
        // Usa o mapper existente para obter o DTO do empréstimo
        EmprestimoDto emprestimoDto = mapperEmprestimo.toDto(entity);

        // Cria o DTO com dados do cliente
        return EmprestimoComClienteDTO.builder()
                .id(emprestimoDto.getId())
                .valorEmprestimo(emprestimoDto.getValorEmprestimo())
                .taxaJuros(emprestimoDto.getTaxaJuros())
                .valorComJuros(emprestimoDto.getValorComJuros())
                .dataEmprestimo(emprestimoDto.getDataEmprestimo())
                .dataVencimento(emprestimoDto.getDataVencimento())
                .observacao(emprestimoDto.getObservacao())
                .statusPagamento(emprestimoDto.getStatusPagamento())
                .clienteId(entity.getCliente().getId())
                .clienteNome(entity.getCliente().getNome())
                .clienteCpf(entity.getCliente().getCpf())
                .clienteTelefone(entity.getCliente().getTelefone())
                .build();
    }



}
