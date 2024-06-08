package hse.se.kpo.hw.service;

import hse.se.kpo.hw.dto.response.UserInfoResponse;
import hse.se.kpo.hw.repository.SessionRepository;
import hse.se.kpo.hw.repository.UserRepository;
import hse.se.kpo.hw.entity.AppUser;
import hse.se.kpo.hw.entity.Session;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final SessionRepository sessionRepository;

    public UserService(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public String getNickname(int id) {
        Optional<AppUser> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such user.");
        }

        return user.get().getNickname();
    }

    public UserInfoResponse getUserInfo(String token) {
        Optional<Session> session = sessionRepository.findByToken(token);

        if (session.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token.");
        }

        AppUser user = session.get().getUser();

        return new UserInfoResponse(user.getNickname(), user.getEmail());
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

