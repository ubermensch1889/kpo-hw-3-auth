package hse.se.kpo.hw.service;

import hse.se.kpo.hw.dto.request.LoginRequest;
import hse.se.kpo.hw.dto.request.RegistrationRequest;
import hse.se.kpo.hw.dto.response.AuthResponse;
import hse.se.kpo.hw.dto.response.CheckAuthResponse;
import hse.se.kpo.hw.entity.AppUser;
import hse.se.kpo.hw.entity.Session;
import hse.se.kpo.hw.repository.SessionRepository;
import hse.se.kpo.hw.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final SessionRepository sessionRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private final String passwordRegex = "^(?=.*[0-9])"
            + "(?=.*[a-z])(?=.*[A-Z])"
            + "(?=.*[@#$%^&+=])"
            + "(?=\\S+$).{8,20}$";
    private final Pattern emailPat = Pattern.compile(emailRegex);
    private final Pattern passwordPat = Pattern.compile(passwordRegex);

    public AuthService(UserRepository userRepo, SessionRepository sessionRepo, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepo;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegistrationRequest request) {

        // Пользователь с такой почтой уже зарегистрирован.
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            // Возвращаем 409, так как такой пользователь уже существует.
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The user with this email is already registered.");
        }

        if (!emailPat.matcher(request.getEmail()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email.");
        }

        if (request.getNickname().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid nickname.");
        }

        // Условия для пароля:
        // Он содержит не менее 8 символов и не более 20 символов.
        // Он содержит хотя бы одну цифру.
        // Он содержит хотя бы один алфавит верхнего регистра.
        // Он содержит хотя бы один строчный алфавит.
        // Он содержит как минимум один специальный символ, включая !@#$%&*()-+=^ .
        // Он не содержит пробелов.
        if (!passwordPat.matcher(request.getPassword()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password.");
        }

        AppUser user = AppUser.builder()
                .created(Timestamp.valueOf(LocalDateTime.now()))
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepo.save(user);
    }

    public String authenticate(LoginRequest request) {
        Optional<AppUser> user = userRepo.findByEmail(request.getEmail());

        // Пользователя с такой почтой не существует.
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No such user.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect password.");
        }

        String jwt = jwtService.generateToken(user.get());

        // Создаем сессию на основе выданного токена. Сессия длится 10 минут.
        Session session = Session.builder().user(user.get()).token(jwt).expires(Timestamp.valueOf(LocalDateTime.now().plusMinutes(10))).build();

        sessionRepo.save(session);

        return jwt;
    }

    public CheckAuthResponse checkAuthorized(String token) {
        Optional<Session> session = sessionRepo.findByToken(token);

        if (session.isEmpty()) {
            return new CheckAuthResponse(0, false);
        }

        if (session.get().getExpires().before(Timestamp.valueOf(LocalDateTime.now()))) {
            // Удаляем сессию, если она истекла.
            sessionRepo.delete(session.get());
            return new CheckAuthResponse(0, false);
        }

        return new CheckAuthResponse(session.get().getUser().getId(), true);
    }
}
