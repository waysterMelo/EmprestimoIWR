package org.wayster.com.emprestimos.Controler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wayster.com.emprestimos.Dto.ClientesDto;
import org.wayster.com.emprestimos.Dto.EmprestimoDto;
import org.wayster.com.emprestimos.Dto.ResumoEmprestimosVencidos;
import org.wayster.com.emprestimos.Service.EmprestimoService;

import java.util.List;

@RestController
@RequestMapping("/emprestimo")
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


    /**
     * Busca um cliente pelo CPF e seus empréstimos.
     *
     * @param cpf CPF do cliente.
     * @return Dados do cliente e seus empréstimos.
     */
    @GetMapping("/{cpf}")
    public ResponseEntity<ClientesDto> buscarClientePorCpf(@PathVariable Long cpf) {
        return emprestimoService.buscarClientePorCpfComEmprestimos(cpf)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Realiza a baixa do pagamento de um empréstimo.
     *
     * @param emprestimoId ID do empréstimo.
     * @return Dados do empréstimo atualizado.
     */
    @PostMapping("/{emprestimoId}/baixar")
    public ResponseEntity<EmprestimoDto> baixarPagamento(@PathVariable Long emprestimoId) {
        return emprestimoService.realizarBaixaPagamento(emprestimoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Endpoint para buscar os empréstimos vencidos hoje.
     *
     * @return ResponseEntity contendo os empréstimos e as somas calculadas.
     */
    @GetMapping("/vencidos-hoje")
    public ResponseEntity<ResumoEmprestimosVencidos> buscarEmprestimosVencidosHoje() {
        ResumoEmprestimosVencidos resultado = emprestimoService.buscarEmprestimosVencidosHoje();
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/{emprestimoId}/pagar-parcialmente")
    public ResponseEntity<EmprestimoDto> pagarParcialmente(
            @PathVariable Long emprestimoId,
            @RequestParam double valorPago) {

        return emprestimoService.pagarParcialmente(emprestimoId, valorPago)
                .map(dto -> ResponseEntity.ok().body(dto))
                .orElse(ResponseEntity.notFound().build());
    }

}
