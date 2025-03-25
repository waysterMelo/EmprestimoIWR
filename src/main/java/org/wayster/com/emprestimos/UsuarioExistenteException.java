package org.wayster.com.emprestimos;

public class UsuarioExistenteException extends RuntimeException{

        public UsuarioExistenteException(String username) {
            super("Já existe um usuário registrado com o nome: " + username);
        }
    }
