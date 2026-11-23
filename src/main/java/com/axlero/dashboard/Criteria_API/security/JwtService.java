package com.axlero.dashboard.Criteria_API.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";
    private static final String CLAIM_TYPE = "type";

    private final SecretKey signingKey;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.access-expiration}") long accessExpirationMs,
                      @Value("${jwt.refresh-expiration}") long refreshExpirationMs) {
        // Base64-encoded secret, at least 256 bits (32 bytes) or jjwt throws WeakKeyException at startup
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateAccessToken(CustomUserDetails user) {
        return buildToken(user, TYPE_ACCESS, accessExpirationMs);
    }

    public String generateRefreshToken(CustomUserDetails user) {
        return buildToken(user, TYPE_REFRESH, refreshExpirationMs);
    }

    private String buildToken(CustomUserDetails user, String type, long ttlMs) {
        Date now = new Date();
        return Jwts.builder()
                .id(UUID.randomUUID().toString()) // jti: makes every token unique
                .subject(String.valueOf(user.getId())) // immutable user id (login identifier is the email)
                .claim(CLAIM_TYPE, type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttlMs))
                .signWith(signingKey)
                .compact();
    }

    /** Verifies signature + expiry. Throws JwtException (ExpiredJwtException, SignatureException, ...). */
    public Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    }

    /** The subject is the user id. A non-numeric subject throws NumberFormatException (an IllegalArgumentException). */
    public Long getUserId(Claims claims) {
        return Long.valueOf(claims.getSubject());
    }

    public boolean isType(Claims claims, String type) {
        return type.equals(claims.get(CLAIM_TYPE, String.class));
    }

    public long getAccessExpirationMs() {
        return accessExpirationMs;
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }
}
