package org.wayster.com.emprestimos.Controler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wayster.com.emprestimos.Dto.EmprestimoDto;
import org.wayster.com.emprestimos.Service.EmprestimoService;

@RestController
@RequestMapping("/realizar-emprestimo")
@RequiredArgsConstructor
public class EmprestimoControler {

    private final EmprestimoService emprestimoService;


    /**
     * Endpoint para salvar um novo empréstimo.
     * Este método recebe um objeto DTO contendo os dados do empréstimo e o processa usando o serviço.
     *
     * @param emprestimoDto Dados do empréstimo a serem salvos.
     * @return ResponseEntity com o DTO do empréstimo salvo e o status HTTP.
     */
    @PostMapping
    public ResponseEntity<EmprestimoDto> salvarEmprestimo(@RequestBody @Valid EmprestimoDto emprestimoDto) {
        return emprestimoService.cadastrarEmprestimo(emprestimoDto)
                .map(emprestimoSalvo -> ResponseEntity.status(HttpStatus.CREATED).body(emprestimoSalvo))
                .orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

}
