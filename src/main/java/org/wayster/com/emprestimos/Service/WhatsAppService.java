package org.wayster.com.emprestimos.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.wayster.com.emprestimos.Entity.EmprestimoEntity;
import org.wayster.com.emprestimos.Repository.EmprestimoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class WhatsAppService {

    @Value("${whatsapp.api.url}")
    private String whatsappApiUrl;

    @Value("${whatsapp.api.token}")
    private String whatsappToken;

    @Value("${whatsapp.api.phone-number-id}")
    private String phoneNumberId;

    @Value("${whatsapp.cloudapi.version}")
    private String apiVersion;

    private final RestTemplate restTemplate = new RestTemplate();
    private final EmprestimoRepository emprestimoRepository;

    public WhatsAppService(EmprestimoRepository emprestimoRepository) {
        this.emprestimoRepository = emprestimoRepository;
    }

    /**
     * Envia uma mensagem via WhatsApp.
     *
     * @param numeroDestino Número do cliente no formato internacional (ex: +5511999999999)
     * @param mensagem      Conteúdo da mensagem personalizada.
     */
    public void enviarMensagemWhatsApp(String numeroDestino, String mensagem) {
        String url = String.format("%s/%s/%s/messages", whatsappApiUrl, apiVersion, phoneNumberId);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(whatsappToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(whatsappToken);

        Map<String, Object> payload = Map.of(
                "messaging_product", "whatsapp",
                "to", numeroDestino,
                "type", "template",
                "template", Map.of(
                        "name", "pagamento_pendente",
                        "language", Map.of("code", "pt_BR")
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.println("Resposta da API: " + response.getBody());
        } catch (HttpClientErrorException e) {
            System.err.println("Erro na requisição: " + e.getStatusCode());
            System.err.println("Detalhes: " + e.getResponseBodyAsString());
        }
    }

    /**
     * Busca os empréstimos vencendo hoje e envia notificações para os clientes via WhatsApp.
     * Este método será executado automaticamente todos os dias às 08:00 da manhã.
     */
    @Scheduled(cron = "0 0 8 * * ?") // Agendado para rodar às 08:00 da manhã todos os dias
    public void enviarNotificacoesDeVencimento() {
        LocalDate hoje = LocalDate.now();

        // Busca empréstimos vencendo hoje
        List<EmprestimoEntity> emprestimosVencendoHoje = emprestimoRepository.findByDataVencimento(hoje);

        if (emprestimosVencendoHoje.isEmpty()) {
            System.out.println("✅ Nenhum empréstimo vencendo hoje.");
            return;
        }

        // Enviar mensagem para cada cliente com empréstimo vencendo
        emprestimosVencendoHoje.forEach(emprestimo -> {
            String numeroCliente = emprestimo.getCliente().getTelefone();
            Double valorDevido = emprestimo.getValorComJuros();
            String mensagem = String.format(
                    "📢 Olá, %s!\nSeu empréstimo de R$ %.2f vence hoje (%s). " +
                            "Por favor, efetue o pagamento para evitar juros adicionais.",
                    emprestimo.getCliente().getNome(),
                    valorDevido,
                    hoje
            );

            enviarMensagemWhatsApp(numeroCliente, mensagem);
            System.out.println("📨 Mensagem enviada para: " + numeroCliente);
        });
    }

}
