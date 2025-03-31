package org.wayster.com.emprestimos.Controler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wayster.com.emprestimos.Dto.LoanDashboardDTO;
import org.wayster.com.emprestimos.Dto.ResumoFinanceiroMensalDTO;
import org.wayster.com.emprestimos.Service.DashboardService;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class LoanDashboardController {


    private final DashboardService dashboardService;


    @GetMapping("/resumo-mensal")
    public ResponseEntity<List<LoanDashboardDTO>> getLoanOverview() {
        return ResponseEntity.ok(dashboardService.getResumoMensal());
    }

    @GetMapping("/resumo-financeiro-mensal")
    public ResponseEntity<List<ResumoFinanceiroMensalDTO>> getResumoFinanceiroMensal() {
        List<ResumoFinanceiroMensalDTO> resumo = dashboardService.getResumoFinanceiroMensal();
        return ResponseEntity.ok(resumo);
    }



}
