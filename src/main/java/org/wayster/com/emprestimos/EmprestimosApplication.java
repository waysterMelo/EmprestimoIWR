package org.wayster.com.emprestimos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableWebMvc
public class EmprestimosApplication implements WebMvcConfigurer {


        @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://emprestimos-iwr-react.s3.website-sa-east-1.amazonaws.com",
                    "http://emprestimos-iwr-react.s3-website-sa-east-1.amazonaws.com"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    public static void main(String[] args) {
        SpringApplication.run(EmprestimosApplication.class, args);
    }

}
