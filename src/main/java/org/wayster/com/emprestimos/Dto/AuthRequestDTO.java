package org.wayster.com.emprestimos.Dto;

import lombok.Data;

@Data
public class AuthRequestDTO {

    private String username;
    private String password;
    private String roles;

}
