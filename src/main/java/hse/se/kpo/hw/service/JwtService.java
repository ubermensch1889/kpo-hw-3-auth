package hse.se.kpo.hw.service;

import hse.se.kpo.hw.entity.AppUser;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.*;
import javax.crypto.SecretKey;

@Service
public class JwtService {
    private SecretKey signingKey() {
        String secretKey = "my0320character0ultra0secure0and0ultra0long0secret";
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(signingKey())
                .build().parseSignedClaims(token).getPayload();
    }

    public Boolean isValid(String token, UserDetails user) {
        return extractAllClaims(token).getSubject().equals(user.getUsername())
                && !extractAllClaims(token).getExpiration().before(new Date());
    }

    public String generateToken(AppUser user) {
        return Jwts.builder().subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .signWith(signingKey())
                .compact();
    }
}
