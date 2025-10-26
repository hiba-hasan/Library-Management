package com.library.LibraryManagementSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.library.LibraryManagementSystem.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import com.library.LibraryManagementSystem.model.Role;
import com.library.LibraryManagementSystem.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.EnableAsync; // <-- ADD THIS IMPORT

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class LibraryManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryManagementSystemApplication.class, args);
	}
		// --- ADD THIS BEAN ---
		@Bean
		public CommandLineRunner initialAdminSetup(
				@Autowired MemberRepository memberRepository,
				@Autowired PasswordEncoder passwordEncoder
    ) {
			return args -> {
				// Check if an admin user already exists
				if (memberRepository.findByEmail("admin@library.com").isEmpty()) {
					// If not, create one
					Member admin = new Member();
					admin.setName("Admin User");
					admin.setEmail("admin@library.com");
					admin.setPassword(passwordEncoder.encode("adminpass")); // Set a secure password
					admin.setRole(Role.ROLE_ADMIN);

					memberRepository.save(admin);
					System.out.println(">>> Created default ADMIN user with email: admin@library.com <<<");
				}
			};
		}
		// --- END OF NEW BEAN ---


}
