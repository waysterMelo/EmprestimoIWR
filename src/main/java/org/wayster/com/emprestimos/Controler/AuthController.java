package org.wayster.com.emprestimos.Controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wayster.com.emprestimos.Dto.AuthRequestDTO;
import org.wayster.com.emprestimos.Dto.AuthResponseDTO;
import org.wayster.com.emprestimos.Service.UsuarioService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
        origins = {
                "http://emprestimos-iwr-react.s3-website-sa-east-1.amazonaws.com",
                "http://emprestimos-iwr-react.s3.website-sa-east-1.amazonaws.com"
        }, 
        allowCredentials = "true",
        allowedHeaders = "*",
        methods = {
                RequestMethod.GET, 
                RequestMethod.POST, 
                RequestMethod.PUT, 
                RequestMethod.DELETE, 
                RequestMethod.OPTIONS
        }
)
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Endpoint para registrar um novo usuário.
     * Exemplo: POST /api/auth/register
     * Body: { "username": "joao", "password": "123" }
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequestDTO request) {
        usuarioService.register(request);
        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }

    /**
     * Endpoint para autenticar o usuário e retornar o token JWT.
     * Exemplo: POST /api/auth/login
     * Body: { "username": "joao", "password": "123" }
     * Resposta: { "token": "xxx.yyy.zzz", "username": "joao" }
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        AuthResponseDTO response = usuarioService.login(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint para verificar se o token é válido
     */
    @GetMapping("/verify")
    public ResponseEntity<String> verifyToken() {
        return ResponseEntity.ok("Token válido");
    }
}