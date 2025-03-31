package org.wayster.com.emprestimos.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WhatsAppService {

    @Value("${whatsapp.api.url}")
    private String whatsappApiUrl;

    @Value("${whatsapp.api.version}")
    private String apiVersion;

    @Value("${whatsapp.api.phone-number-id}")
    private String phoneNumberId;

    @Value("${whatsapp.api.token}")
    private String whatsappToken;

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    public void enviarTemplateVencendoCliente(String numeroCliente, String nomeCliente, String valor, String dataVencimento) {

        // Formata o número adequadamente
        String numeroFormatado = formatarNumeroInternacional(numeroCliente);

        // Monta a URL da API
        String url = String.format("%s/%s/%s/messages", whatsappApiUrl, apiVersion, phoneNumberId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(whatsappToken);

        // Monta o corpo do template
        Map<String, Object> templateBody = Map.of(
                "name", "emprestimo_vencendo_cliente",  // exatamente o nome que você cadastrou
                "language", Map.of("code", "pt_BR"),
                "components", List.of(
                        Map.of(
                                "type", "body",
                                "parameters", List.of(
                                        Map.of("type", "text", "text", nomeCliente),
                                        Map.of("type", "text", "text", valor),
                                        Map.of("type", "text", "text", dataVencimento)
                                )
                        )
                )
        );

        // Monta o payload principal
        Map<String, Object> payload = Map.of(
                "messaging_product", "whatsapp",
                "to", numeroFormatado,
                "type", "template",
                "template", templateBody
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("✅ Template de vencimento (cliente) enviado para {}", numeroFormatado);
            } else {
                logger.error("❌ Erro enviando template (cliente). Status: {} - {}",
                        response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            logger.error("❌ Exceção ao enviar template (cliente): {}", e.getMessage());
        }
    }

    public void enviarTemplateVencendoAgiota(String numeroAgiota, String nomeCliente, String valor, String dataVencimento) {
        // Formata o número adequadamente
        String numeroFormatado = formatarNumeroInternacional(numeroAgiota);

        // Monta a URL da API
        String url = String.format("%s/%s/%s/messages", whatsappApiUrl, apiVersion, phoneNumberId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(whatsappToken);

        // Usando a mesma estrutura do método 'enviarTemplateEmprestimo' que funciona
        Map<String, Object> templateBody = Map.of(
                "name", "emprestimo_vencendo_agiota",
                "language", Map.of("code", "pt_BR"),
                "components", List.of(
                        Map.of(
                                "type", "body",
                                "parameters", List.of(
                                        Map.of("type", "text", "text", nomeCliente),
                                        Map.of("type", "text", "text", valor),
                                        Map.of("type", "text", "text", dataVencimento)
                                )
                        )
                )
        );

        // Payload final
        Map<String, Object> payload = Map.of(
                "messaging_product", "whatsapp",
                "to", numeroFormatado,
                "type", "template",
                "template", templateBody
        );

        // Log detalhado para depuração
        try {
            ObjectMapper mapper = new ObjectMapper();
            logger.info("📤 Enviando template 'emprestimo_vencendo_agiota' com payload: {}",
                    mapper.writeValueAsString(payload));
        } catch (Exception e) {
            logger.error("Erro ao converter payload para JSON", e);
        }

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("✅ Template de vencimento (agiota) enviado com sucesso para {}", numeroFormatado);
            } else {
                logger.error("❌ Erro ao enviar template (agiota). Status: {} - Resposta: {}",
                        response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            logger.error("❌ Exceção ao enviar template (agiota): {}", e.getMessage());
        }
    }

    public void enviarTemplateEmprestimo(String telefone, String valorCreditado, String valorPego, String valorDevido, String dataVencimento) {
        String numeroFormatado = formatarNumeroInternacional(telefone);
        String url = String.format("%s/%s/%s/messages", whatsappApiUrl, apiVersion, phoneNumberId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(whatsappToken);

        // Parâmetros ajustados para usar "text" com números formatados
        Map<String, Object> templateBody = Map.of(
                "name", "emprestimo_creditado",
                "language", Map.of("code", "pt_BR"),
                "components", List.of(
                        Map.of(
                                "type", "body",
                                "parameters", List.of(
                                        // Use "text" e formate os números com ponto decimal
                                        Map.of("type", "text", "text", valorCreditado.replace(",", ".")),
                                        Map.of("type", "text", "text", valorPego.replace(",", ".")),
                                        Map.of("type", "text", "text", valorDevido.replace(",", ".")),
                                        // Data no formato correto
                                        Map.of(
                                                "type", "date_time",
                                                "date_time", Map.of("fallback_value", dataVencimento)
                                        )
                                )
                        )
                )
        );

        Map<String, Object> payload = Map.of(
                "messaging_product", "whatsapp",
                "to", numeroFormatado,
                "type", "template",
                "template", templateBody
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("✅ Template de empréstimo creditado enviado para {}", numeroFormatado);
            } else {
                logger.error("❌ Erro ao enviar template. Status: {} - Resposta: {}",
                        response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            logger.error("❌ Exceção ao enviar mensagem: {}", e.getMessage());
        }

    }

    private String formatarNumeroInternacional(String numero) {
        if (!numero.startsWith("+")) {
            // Aqui assumimos que todos os números são do Brasil; se houver casos internacionais,
            // você pode aprimorar a lógica de acordo com sua demanda corporativa.
            numero = "55" + numero;
        }
        return numero;
    }
}
