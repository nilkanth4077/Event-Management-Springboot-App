package com.event_management;

import com.event_management.entities.User;
import com.event_management.repositories.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class EventManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventManagementApplication.class, args);
	}

	@Bean
	public CommandLineRunner createAdmin(UserRepo userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (!userRepository.existsByRole("ADMIN")) {
				User admin = new User();
				admin.setFirstName("Nilkanth");
				admin.setLastName("Patel");
				admin.setEmail("nilkanth0904@gmail.com");
				admin.setPassword(passwordEncoder.encode("Admin@123"));
				admin.setRole("ADMIN");

				userRepository.save(admin);
				System.out.println("Default admin created: admin@example.com / admin123");
			}
		};
	}
}
