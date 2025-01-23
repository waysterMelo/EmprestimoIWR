package org.wayster.com.emprestimos.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wayster.com.emprestimos.Entity.Emprestimo;
import org.wayster.com.emprestimos.Enums.StatusPagamento;
import org.wayster.com.emprestimos.Repository.EmprestimoRepository;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;

    @Autowired
    public EmprestimoService(EmprestimoRepository emprestimoRepository) {
        this.emprestimoRepository = emprestimoRepository;
    }

    public Emprestimo cadastrarEmprestimo (Emprestimo emprestimo){
        // Calcula o valor final com juros
        Double valorComJuros = emprestimo.getValorEmprestimo() * (1 + emprestimo.getTaxaJuros());
        emprestimo.setValorComJuros(valorComJuros);

        // Define o status inicial como PENDENTE
        emprestimo.setStatusPagamento(StatusPagamento.PENDENTE);

        // Define a data de realização do empréstimo (se ainda não estiver setada)
            LocalDate dataEmprestimo = Optional.ofNullable(emprestimo.getDataEmprestimo()).orElse(LocalDate.now());
            emprestimo.setDataEmprestimo(dataEmprestimo);

            return emprestimoRepository.save(emprestimo);
    }


}
