package org.wayster.com.emprestimos.Controler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wayster.com.emprestimos.Service.NotificacaoEmprestimosService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TesteController {

    private final NotificacaoEmprestimosService notificacaoService;

    @GetMapping("/notificacoes")
    public ResponseEntity<String> testarNotificacoes() {
        LocalDate dataParaTeste = LocalDate.now(); // Usar data atual ou específica
        int notificacoesEnviadas = notificacaoService.testarNotificacoes(dataParaTeste);
        return ResponseEntity.ok("Teste executado com sucesso. " + notificacoesEnviadas + " notificações enviadas.");
    }

}
