package org.wayster.com.emprestimos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")  // Garante que as propriedades sejam carregadas
public class EmprestimosApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmprestimosApplication.class, args);
    }

}
