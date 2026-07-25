package iibs.gestionportefeuille.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMs;

    public String genererToken(UserDetails userDetails) {
        Date maintenant = new Date();
        Date expiration = new Date(maintenant.getTime() + expirationMs);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(maintenant)
                .expiration(expiration)
                .signWith(cle())
                .compact();
    }

    public String extraireEmail(String token) {
        return extraire(token, Claims::getSubject);
    }

    public boolean estValide(String token, UserDetails userDetails) {
        String email = extraireEmail(token);
        return email.equals(userDetails.getUsername()) && !estExpire(token);
    }

    private boolean estExpire(String token) {
        return extraire(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extraire(String token, Function<Claims, T> resolveur) {
        Claims claims = Jwts.parser()
                .verifyWith(cle())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolveur.apply(claims);
    }

    private SecretKey cle() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}