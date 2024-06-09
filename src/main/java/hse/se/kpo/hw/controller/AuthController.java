package hse.se.kpo.hw.controller;

import hse.se.kpo.hw.dto.request.LoginRequest;
import hse.se.kpo.hw.dto.request.RegistrationRequest;
import hse.se.kpo.hw.dto.response.AuthResponse;
import hse.se.kpo.hw.dto.response.CheckAuthResponse;
import hse.se.kpo.hw.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest request) {
        try {
            authService.register(request);
        }
        catch (ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getMessage(), ex.getStatusCode());
        }
        return ResponseEntity.ok("The user has been successfully registered.");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(new AuthResponse(authService.authenticate(request), "Success."));
        }
        catch (ResponseStatusException ex) {
            return new ResponseEntity<>(new AuthResponse("", ex.getMessage()), ex.getStatusCode());
        }
    }

    @GetMapping("/check-auth")
    public ResponseEntity<CheckAuthResponse> checkAuthorized(@RequestParam String token) {
        return ResponseEntity.ok(authService.checkAuthorized(token));
    }
}
