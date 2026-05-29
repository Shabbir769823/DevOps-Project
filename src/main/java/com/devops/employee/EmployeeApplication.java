package com.devops.employee;

import com.devops.employee.model.Role;
import com.devops.employee.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.devops.employee.model.User;
import com.devops.employee.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class EmployeeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeApplication.class, args);
    }

    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

            if (!userRepository.existsByUsername("admin")) {
                User admin = new User("admin", passwordEncoder.encode("admin123"), "admin@devops.com", "System", "Admin");
                admin.addRole(userRole);
                admin.addRole(adminRole);
                userRepository.save(admin);
            }

            if (!userRepository.existsByUsername("user")) {
                User user = new User("user", passwordEncoder.encode("user123"), "user@devops.com", "Regular", "User");
                user.addRole(userRole);
                userRepository.save(user);
            }
        };
    }
}
