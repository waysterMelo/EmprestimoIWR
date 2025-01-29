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

    /**
     * Cadastra um novo empréstimo e envia notificação via WhatsApp.
     *
     * @param emprestimoDto Dados do empréstimo a ser cadastrado.
     * @return O DTO do empréstimo cadastrado.
     */
    public Optional<EmprestimoDto> cadastrarEmprestimo(EmprestimoDto emprestimoDto) {
        return clientesRepository.findById(emprestimoDto.getClienteId())
                .map(cliente -> {
                    // Valida se o cliente tem um número de telefone válido
                    String telefoneCliente = Optional.ofNullable(cliente.getTelefone())
                            .map(tel -> tel.replaceAll("[^0-9]", "")) // Remove caracteres inválidos
                            .orElseThrow(() -> new IllegalArgumentException("O cliente não possui um número de telefone válido!"));

                    // Calcula o valor com juros
                    double valorComJuros = EmprestimoUtils.calcularValorComJuros(emprestimoDto.getValorEmprestimo(), emprestimoDto.getTaxaJuros());
                    emprestimoDto.setValorComJuros(valorComJuros);

                    // Define a data do empréstimo como a data atual, caso não esteja preenchida
                    emprestimoDto.setDataEmprestimo(Optional.ofNullable(emprestimoDto.getDataEmprestimo()).orElse(LocalDate.now()));

                    // Define a data de vencimento para 30 dias após a data do empréstimo, caso não esteja preenchida
                    emprestimoDto.setDataVencimento(Optional.ofNullable(emprestimoDto.getDataVencimento())
                            .orElse(emprestimoDto.getDataEmprestimo().plusDays(30)));

                    // Define o status do pagamento como PENDENTE por padrão
                    emprestimoDto.setStatusPagamento(Optional.ofNullable(emprestimoDto.getStatusPagamento()).orElse(StatusPagamento.PENDENTE));

                    // Mapeia o DTO para a entidade do empréstimo
                    EmprestimoEntity emprestimoEntity = emprestimosMapper.toEntity(emprestimoDto, cliente);

                    // Salva o empréstimo no banco de dados
                    EmprestimoEntity emprestimoSalvo = emprestimoRepository.save(emprestimoEntity);

                    // Formata mensagens para envio via WhatsApp
                    String mensagemCliente = String.format(
                            "Olá %s, seu empréstimo de R$%.2f foi aprovado. Data de vencimento: %s.",
                            cliente.getNome(), emprestimoSalvo.getValorEmprestimo(), emprestimoSalvo.getDataVencimento());

                    String mensagemAgiota = String.format(
                            "Novo empréstimo cadastrado! Cliente: %s, Valor: R$%.2f, Vence em: %s.",
                            cliente.getNome(), emprestimoSalvo.getValorEmprestimo(), emprestimoSalvo.getDataVencimento());

                    // LOG: Verificando o número antes de enviar
                    System.out.println("Enviando mensagem para o cliente: " + telefoneCliente);
                    System.out.println("Enviando mensagem para o agiota: 5531998956974");

                    // Enviar mensagens para o cliente e o agiota
                    whatsAppService.enviarMensagemWhatsApp(telefoneCliente, mensagemCliente);
                    whatsAppService.enviarMensagemWhatsApp("+5531998956974", mensagemAgiota); // Número do agiota

                    // Retorna o empréstimo cadastrado como DTO
                    return emprestimosMapper.toDto(emprestimoSalvo);
                });
    }


    /**
     * Busca um cliente pelo CPF e retorna seus dados.
     *
     * @param cpf CPF do cliente.
     * @return Dados do cliente e seus empréstimos, se encontrados.
     */
    public Optional<ClientesDto> buscarClientePorCpfComEmprestimos(Long cpf){
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

    /**
     * Realiza a baixa do pagamento de um empréstimo.
     *
     * @param emprestimoId ID do empréstimo.
     * @return Dados atualizados do empréstimo.
     */
    public Optional<EmprestimoDto> realizarBaixaPagamento(Long emprestimoId){
        return emprestimoRepository.findById(emprestimoId)
                .map(emprestimo -> {
                    // Atualiza o status do pagamento
                    emprestimo.setStatusPagamento(StatusPagamento.PAGO);
                    EmprestimoEntity emprestimoAtualizado = emprestimoRepository.save(emprestimo);
                    return mapperEmprestimo.toDto(emprestimoAtualizado);
                });
    }


    /**
     * Retorna todos os empréstimos vencidos na data atual e calcula a soma dos valores emprestados
     * e a soma dos valores a receber com juros.
     *
     * @return Objeto contendo a lista de empréstimos vencidos e as somas calculadas.
     */
    public ResumoEmprestimosVencidos buscarEmprestimosVencidosHoje(){
        LocalDate hoje = LocalDate.now(); // Data atual
        List<EmprestimoEntity> emprestimosVencidos = emprestimoRepository.findByDataVencimento(hoje);

        // Mapeia para DTO
        List<EmprestimoDto> emprestimosDto = emprestimosVencidos.stream().map(mapperEmprestimo::toDto).toList();

        // Calcula as somas
        double valoresEmprestados = emprestimosVencidos.stream()
                .mapToDouble(EmprestimoEntity::getValorEmprestimo).sum();

        double valoresAReceber = emprestimosVencidos.stream()
                .mapToDouble(EmprestimoEntity::getValorComJuros).sum();

        double lucro = valoresAReceber - valoresEmprestados;

        return new ResumoEmprestimosVencidos(emprestimosDto, valoresEmprestados, valoresAReceber, lucro);

    }

    /**
     * Realiza um pagamento parcial de um empréstimo.
     *
     * @param emprestimoId O ID do empréstimo.
     * @param valorPago O valor pago pelo cliente.
     * @return O DTO do empréstimo atualizado.
     */
    public Optional<EmprestimoDto> pagarParcialmente(Long emprestimoId, Double valorPago){
        return emprestimoRepository.findById(emprestimoId)
                .map(emprestimo -> {
                    // Verifica se o pagamento não ultrapassa o valor devido
                    if (valorPago <=0 || valorPago > emprestimo.getValorComJuros()){
                        throw new IllegalArgumentException("O valor pago é inválido ou excede o valor devido.");
                    }
                    // Atualiza o valor devido
                    double novoValorDevido = emprestimo.getValorComJuros() - valorPago;
                    emprestimo.setValorComJuros(novoValorDevido);

                    // Atualiza a observação indicando o pagamento parcial
                    String observacaoAtual = Optional.ofNullable(emprestimo.getObservacao()).orElse("");
                    String novaObservacao = String.format("Pagamento parcial de R$%.2f realizado em %s. Valor restante: R$%.2f.", valorPago,
                            LocalDate.now(), novoValorDevido);
                    emprestimo.setObservacao(observacaoAtual + (observacaoAtual.isEmpty() ? "" : " ") + novaObservacao);

                    // Atualiza o status para PAGO se o valor devido for zerado
                    if (novoValorDevido == 0) {
                        emprestimo.setStatusPagamento(StatusPagamento.PAGO);
                    }

                    // Salva as alterações no banco de dados
                    emprestimoRepository.save(emprestimo);

                    // Retorna o DTO atualizado
                    return mapperEmprestimo.toDto(emprestimo);

                });

    }


}
