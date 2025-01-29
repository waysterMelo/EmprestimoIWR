package org.wayster.com.emprestimos.Controler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wayster.com.emprestimos.Service.WhatsAppService;

@RestController
@RequestMapping("/whatsapp")
public class WhatsAppController {

    private final WhatsAppService whatsAppService;

    public WhatsAppController(WhatsAppService whatsAppService) {
        this.whatsAppService = whatsAppService;
    }

    /**
     * Envia notificações para clientes com empréstimos vencendo hoje.
     *
     * @return Mensagem informando o resultado.
     */
    @GetMapping("/notificar-vencimentos")
    public ResponseEntity<String> notificarVencimentos() {
        whatsAppService.enviarNotificacoesDeVencimento();
        return ResponseEntity.ok("📨 Notificações de vencimentos enviadas com sucesso!");
    }



}
