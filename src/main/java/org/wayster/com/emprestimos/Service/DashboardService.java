package org.wayster.com.emprestimos.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wayster.com.emprestimos.Dto.LoanDashboardDTO;
import org.wayster.com.emprestimos.Repository.EmprestimoRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EmprestimoRepository emprestimoRepository;

    public List<LoanDashboardDTO> getResumoMensal() {
        return emprestimoRepository.buscarResumoMensal().stream()
                .map(obj -> new LoanDashboardDTO(
                        ((Number) obj[0]).intValue(),         // mês
                        (Double) obj[1],                  // totalEmprestado
                        (Double) obj[2],                  // retornoEsperado
                        ((Number) obj[3]).longValue(),        // clientes
                        ((Number) obj[4]).doubleValue()   // mediaJuros
                ))
                .toList();
    }

}
