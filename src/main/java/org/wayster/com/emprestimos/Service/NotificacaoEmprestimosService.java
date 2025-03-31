package org.wayster.com.emprestimos.Service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.wayster.com.emprestimos.Entity.EmprestimoEntity;
import org.wayster.com.emprestimos.Enums.StatusPagamento;
import org.wayster.com.emprestimos.Repository.EmprestimoRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoEmprestimosService {

    private final EmprestimoRepository emprestimoRepository;
    private final WhatsAppService whatsAppService;

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoEmprestimosService.class);
    private static final DateTimeFormatter FORMATTER_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Value("${whatsapp.agiota.phone}")
    private String numeroAgiota;


    @Scheduled(cron = "0 0 8 * * *") // Executa todo dia às 08:00
    public void notificarEmprestimosVencendoHoje() {
        logger.info("🔍 Iniciando verificação de empréstimos vencendo hoje");

        LocalDate hoje = LocalDate.now();
        logger.info("📅 Data de verificação: {}", hoje.format(FORMATTER_BR));

        // Busca somente empréstimos pendentes que vencem hoje
        List<EmprestimoEntity> emprestimosVencendo = emprestimoRepository.findByDataVencimentoAndStatusPagamento(
                hoje, StatusPagamento.PENDENTE);

        logger.info("📊 Encontrados {} empréstimos vencendo hoje", emprestimosVencendo.size());

        int sucessoCliente = 0;
        int sucessoAgiota = 0;
        int falhas = 0;

        for (EmprestimoEntity emprestimo : emprestimosVencendo) {
            try {
                String nomeCliente = emprestimo.getCliente().getNome();
                String telefoneCliente = emprestimo.getCliente().getTelefone();
                // Formata o valor para o formato correto (R$ 123.45)
                String valor = String.format("%.2f", emprestimo.getValorComJuros());
                // Formata a data no padrão brasileiro
                String dataVencimento = emprestimo.getDataVencimento().format(FORMATTER_BR);

                logger.info("📝 Processando notificação para o cliente: {} - Valor: R$ {}",
                        nomeCliente, valor);

                // Enviar template para o cliente
                try {
                    whatsAppService.enviarTemplateVencendoCliente(
                            telefoneCliente,
                            nomeCliente,
                            valor,
                            dataVencimento
                    );
                    sucessoCliente++;
                    logger.info("✅ Notificação enviada com sucesso para o cliente: {}", nomeCliente);
                } catch (Exception e) {
                    falhas++;
                    logger.error("❌ Erro ao enviar notificação para o cliente {}: {}",
                            nomeCliente, e.getMessage());
                }

                // Enviar template para o agiota
                try {
                    whatsAppService.enviarTemplateVencendoAgiota(
                            numeroAgiota,
                            nomeCliente,
                            valor,
                            dataVencimento
                    );
                    sucessoAgiota++;
                    logger.info("✅ Notificação enviada com sucesso para o agiota sobre o cliente: {}", nomeCliente);
                } catch (Exception e) {
                    falhas++;
                    logger.error("❌ Erro ao enviar notificação para o agiota sobre o cliente {}: {}",
                            nomeCliente, e.getMessage());
                }
            } catch (Exception e) {
                falhas++;
                logger.error("❌ Erro ao processar empréstimo ID {}: {}",
                        emprestimo.getId(), e.getMessage());
            }
        }

        logger.info("📊 Resumo das notificações: {} para clientes, {} para agiota, {} falhas",
                sucessoCliente, sucessoAgiota, falhas);
        logger.info("✅ Processo de notificação de vencimentos concluído");
    }


    public int testarNotificacoes(LocalDate dataVencimento) {
        logger.info("🧪 Iniciando teste de notificações para a data: {}",
                dataVencimento.format(FORMATTER_BR));

        List<EmprestimoEntity> emprestimosVencendo = emprestimoRepository.findByDataVencimentoAndStatusPagamento(
                dataVencimento, StatusPagamento.PENDENTE);

        logger.info("🧪 Encontrados {} empréstimos para teste", emprestimosVencendo.size());

        for (EmprestimoEntity emprestimo : emprestimosVencendo) {
            String nomeCliente = emprestimo.getCliente().getNome();
            String telefoneCliente = emprestimo.getCliente().getTelefone();
            String valor = String.format("%.2f", emprestimo.getValorComJuros());
            String dataVencimentoFormatada = emprestimo.getDataVencimento().format(FORMATTER_BR);

            // Envio somente para o agiota durante os testes para evitar mensagens para clientes reais
            whatsAppService.enviarTemplateVencendoAgiota(
                    numeroAgiota,
                    nomeCliente,
                    valor,
                    dataVencimentoFormatada
            );

            logger.info("🧪 Notificação de teste enviada para o agiota sobre o cliente: {}", nomeCliente);
        }

        logger.info("🧪 Teste de notificações concluído: {} mensagens enviadas", emprestimosVencendo.size());
        return emprestimosVencendo.size();
    }
}