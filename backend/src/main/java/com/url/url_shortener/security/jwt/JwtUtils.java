package com.url.url_shortener.security.jwt;

import com.url.url_shortener.services.UserDetailsImpl;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    // Authorization -> Bearer <Token>
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateToken(UserDetailsImpl userDetails) {
        String userName = userDetails.getUsername();
        String roles = userDetails.getAuthorities().stream()
                                  .map(authorities -> authorities.getAuthority())
                                  .collect(Collectors.joining(","));
        return Jwts.builder()
                   .subject(userName)
                   .claim("roles", roles)
                   .issuedAt(new Date())
                   .expiration(new Date((new Date().getTime() + jwtExpirationMs)))
                   .signWith(key())
                   .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                   .verifyWith((SecretKey) key())
                   .build()
                   .parseSignedClaims(token)
                   .getPayload()
                   .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            // log the reason for invalid token
            System.out.println("Invalid JWT: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println("JWT is null or empty.");
            return false;
        }
    }

}
