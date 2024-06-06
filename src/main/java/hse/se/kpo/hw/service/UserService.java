package hse.se.kpo.hw.service;

import hse.se.kpo.hw.repository.UserRepository;
import hse.se.kpo.hw.entity.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.NoSuchElementException;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    enum UserRole implements GrantedAuthority {
        USER;

        @Override
        public String getAuthority() {
            return name();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        if (email == null) {
            throw new NoSuchElementException();
        }

        AppUser user = userRepository.findByEmail(email).orElseThrow();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(UserRole.USER)
        );
    }
}

