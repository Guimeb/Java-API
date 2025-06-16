package br.com.sprint.sprint;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SprintApplication {

	public static void main(String[] args) {
		SpringApplication.run(SprintApplication.class, args);
	}

	@Bean
	ApplicationRunner runner(PasswordEncoder passwordEncoder) {
		return args -> System.out.println(passwordEncoder.encode("password"));
	}
}
