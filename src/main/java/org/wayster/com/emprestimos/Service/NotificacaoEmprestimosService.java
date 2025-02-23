package org.wayster.com.emprestimos.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.wayster.com.emprestimos.Entity.EmprestimoEntity;
import org.wayster.com.emprestimos.Repository.EmprestimoRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoEmprestimosService {

    private final EmprestimoRepository emprestimoRepository;
    private final WhatsAppService whatsAppService;

    @Value("${whatsapp.agiota.phone}")
    private String numeroAgiota;

    /**
     * Executa diariamente às 08:00 e envia lembretes de vencimento para clientes e agiotas.
     */
    @Scheduled(cron = "0 0 8 * * *") // Executa todo dia às 08:00
    public void notificarEmprestimosVencendoHoje() {
        LocalDate hoje = LocalDate.now();
        List<EmprestimoEntity> emprestimosVencendo = emprestimoRepository.findByDataVencimento(hoje);

        for (EmprestimoEntity emprestimo : emprestimosVencendo) {
            String nomeCliente = emprestimo.getCliente().getNome();
            String telefoneCliente = emprestimo.getCliente().getTelefone();
            String valor = String.format("%.2f", emprestimo.getValorComJuros());
            String dataVencimento = emprestimo.getDataVencimento().toString();

            // Enviar template para o cliente
            whatsAppService.enviarTemplateVencendoCliente(
                    telefoneCliente,
                    nomeCliente,
                    valor,
                    dataVencimento
            );

            // Enviar template para o agiota
            whatsAppService.enviarTemplateVencendoAgiota(
                    numeroAgiota,
                    nomeCliente,
                    valor,
                    dataVencimento
            );
        }
    }







}
