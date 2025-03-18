package org.wayster.com.emprestimos.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wayster.com.emprestimos.Dto.ClientesDto;
import org.wayster.com.emprestimos.Dto.EmprestimoDto;
import org.wayster.com.emprestimos.Dto.ResumoEmprestimosVencidos;
import org.wayster.com.emprestimos.EmprestimoUtils.EmprestimoUtils;
import org.wayster.com.emprestimos.Entity.EmprestimoEntity;
import org.wayster.com.emprestimos.Enums.StatusPagamento;
import org.wayster.com.emprestimos.Mapper.MapperEmprestimo;
import org.wayster.com.emprestimos.Repository.ClientesRepository;
import org.wayster.com.emprestimos.Repository.EmprestimoRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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
        List<EmprestimoEntity> emprestimosVencidos = emprestimoRepository.findByDataVencimento(hoje);

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

}
