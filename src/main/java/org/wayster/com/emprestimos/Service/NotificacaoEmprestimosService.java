package org.wayster.com.emprestimos.Service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.wayster.com.emprestimos.Entity.EmprestimoEntity;
import org.wayster.com.emprestimos.Repository.EmprestimoRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificacaoEmprestimosService {

    private final EmprestimoRepository emprestimoRepository;
    private final WhatsAppService whatsAppService;

    public NotificacaoEmprestimosService(EmprestimoRepository emprestimoRepository, WhatsAppService whatsAppService) {
        this.emprestimoRepository = emprestimoRepository;
        this.whatsAppService = whatsAppService;
    }

    /**
     * Executa diariamente às 08:00 e envia lembretes de vencimento para clientes e agiotas.
     */
    @Scheduled(cron = "0 0 8 * * *") // Executa todo dia às 08:00
    public void notificarEmprestimosVencendoHoje() {
        LocalDate hoje = LocalDate.now();
        List<EmprestimoEntity> emprestimosVencendo = emprestimoRepository.findByDataVencimento(hoje);

        for (EmprestimoEntity emprestimo : emprestimosVencendo) {
            String mensagemCliente = String.format(
                    "Olá %s, lembrete: Seu empréstimo de R$%.2f vence hoje (%s). Evite juros!",
                    emprestimo.getCliente().getNome(), emprestimo.getValorComJuros(), emprestimo.getDataVencimento());

            String mensagemAgiota = String.format(
                    "Atenção: O cliente %s tem um pagamento de R$%.2f vencendo hoje (%s).",
                    emprestimo.getCliente().getNome(), emprestimo.getValorComJuros(), emprestimo.getDataVencimento());

            // Enviar mensagens
            whatsAppService.enviarMensagemWhatsApp(emprestimo.getCliente().getTelefone(), mensagemCliente);
            whatsAppService.enviarMensagemWhatsApp("+5531998956974", mensagemAgiota);
        }
    }
}
