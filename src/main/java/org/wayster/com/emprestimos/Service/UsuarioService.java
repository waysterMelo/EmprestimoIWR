package org.wayster.com.emprestimos.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.wayster.com.emprestimos.Dto.AuthRequestDTO;
import org.wayster.com.emprestimos.Dto.AuthResponseDTO;
import org.wayster.com.emprestimos.Entity.UsuarioEntity;
import org.wayster.com.emprestimos.Repository.UserRepository;
import org.wayster.com.emprestimos.UsuarioExistenteException;

import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {


    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    // 1) Cadastra usuário
    public void register(AuthRequestDTO request) {
        // Verifica se já existe
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsuarioExistenteException(request.getUsername());
        }

        UsuarioEntity novo = UsuarioEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(request.getRoles())
                .build();

        usuarioRepository.save(novo);
    }


    // 2) Faz login e retorna o token
    public AuthResponseDTO login(AuthRequestDTO request) {
        // Tenta autenticar com Spring Security
        var authToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        authenticationManager.authenticate(authToken);

        // Se chegar aqui, autenticação foi bem-sucedida
        UsuarioEntity usuario = usuarioRepository.findByUsername(request.getUsername()).orElseThrow(() -> new UsuarioExistenteException("Usuário não encontrado"));
        String tokenJWT = jwtService.generateToken(usuario);

        return new AuthResponseDTO(usuario.getUsername(), tokenJWT);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioEntity usuario = usuarioRepository.findByUsername(username).orElseThrow(() -> new UsuarioExistenteException("Usuário não encontrado"));

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRoles()))
        );
    }
}
