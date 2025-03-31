package org.wayster.com.emprestimos.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wayster.com.emprestimos.Dto.LoanDashboardDTO;
import org.wayster.com.emprestimos.Dto.ResumoFinanceiroMensalDTO;
import org.wayster.com.emprestimos.Repository.EmprestimoRepository;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<ResumoFinanceiroMensalDTO> getResumoFinanceiroMensal() {
        List<Object[]> resultados = emprestimoRepository.buscarResumoFinanceiroMensal();

        return resultados.stream()
                .map(obj -> new ResumoFinanceiroMensalDTO(
                        ((Number) obj[0]).intValue(),    // mes
                        ((Number) obj[1]).doubleValue(), // totalEmprestado (COALESCE garante não ser null)
                        ((Number) obj[2]).doubleValue(), // retornoEsperado (COALESCE garante não ser null)
                        ((Number) obj[3]).doubleValue()  // lucro (COALESCE garante não ser null)
                ))
                .collect(Collectors.toList()); // Usar Collectors.toList()
    }


}
