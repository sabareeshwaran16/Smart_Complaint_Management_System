package com.hostelcare;

import com.hostelcare.entity.Category;
import com.hostelcare.entity.User;
import com.hostelcare.enums.Role;
import com.hostelcare.repository.CategoryRepository;
import com.hostelcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds default users and categories on first startup if they don't exist.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedCategories();
    }

    private void seedUsers() {
        List<Object[]> defaults = List.of(
            new Object[]{"Admin User",    "admin@hostel.com",   "admin123",   Role.ADMIN},
            new Object[]{"Warden User",   "warden@hostel.com",  "warden123",  Role.WARDEN},
            new Object[]{"Student User",  "student@hostel.com", "student123", Role.STUDENT}
        );

        for (Object[] u : defaults) {
            String email = (String) u[1];
            if (!userRepository.existsByEmail(email)) {
                userRepository.save(User.builder()
                    .name((String) u[0])
                    .email(email)
                    .password(passwordEncoder.encode((String) u[2]))
                    .role((Role) u[3])
                    .build());
                log.info("Seeded user: {}", email);
            }
        }
    }

    private void seedCategories() {
        List<String> names = List.of(
            "Plumbing", "Electrical", "Cleanliness", "Furniture", "Internet", "Security", "Other"
        );
        for (String name : names) {
            if (!categoryRepository.existsByName(name)) {
                categoryRepository.save(Category.builder().name(name).build());
                log.info("Seeded category: {}", name);
            }
        }
    }
}
