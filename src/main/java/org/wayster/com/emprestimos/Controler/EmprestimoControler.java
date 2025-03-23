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

import java.util.Map;

@RestController
@RequestMapping("/emprestimo")
@RequiredArgsConstructor
public class EmprestimoControler {

    private final EmprestimoService emprestimoService;



    @PostMapping
    public ResponseEntity<EmprestimoDto> salvarEmprestimo(@RequestBody @Valid EmprestimoDto emprestimoDto) {
        return emprestimoService.cadastrarEmprestimo(emprestimoDto)
                .map(emprestimoSalvo -> ResponseEntity.status(HttpStatus.CREATED).body(emprestimoSalvo))
                .orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }


    @GetMapping("/{cpf}")
    public ResponseEntity<ClientesDto> buscarClientePorCpf(@PathVariable String cpf) {
        return emprestimoService.buscarClientePorCpfComEmprestimos(cpf)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @PutMapping("/baixa/{emprestimoId}")
    public ResponseEntity<EmprestimoDto> baixarPagamento(@PathVariable Long emprestimoId) {
        return emprestimoService.realizarBaixaPagamento(emprestimoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/vencidos-hoje")
    public ResponseEntity<ResumoEmprestimosVencidos> buscarEmprestimosVencidosHoje() {
        ResumoEmprestimosVencidos resultado = emprestimoService.buscarEmprestimosVencidosHoje();
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/{emprestimoId}/pagar-parcialmente")
    public ResponseEntity<EmprestimoDto> pagarParcialmente(
            @PathVariable Long emprestimoId,
            @RequestBody Map<String, Double> pagamentoRequest) {

        double valorPago = pagamentoRequest.get("valorPago");

        return emprestimoService.pagarParcialmente(emprestimoId, valorPago)
                .map(dto -> ResponseEntity.ok().body(dto))
                .orElse(ResponseEntity.notFound().build());
    }

}
