package tcc.api.secureapi.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey key;
    private final JwtProperties props;

    public JwtService(JwtProperties props) {
        byte[] bytes;
        try {
            bytes = Decoders.BASE64.decode(props.secret());
        } catch (RuntimeException e) {
            throw new IllegalStateException("JWT_SECRET deve estar codificado em Base64");
        }
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET deve ter pelo menos 256 bits (32 bytes)");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.props = props;
    }

    public String generateToken(AuthenticatedUser user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(props.issuer())
                .subject(user.getId().toString())          // somente o id; papéis NÃO vão no token
                .id(UUID.randomUUID().toString())           // jti (base para revogação futura)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(props.expirationMinutes(), ChronoUnit.MINUTES)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public long expirationSeconds() {
        return props.expirationMinutes() * 60;
    }

    /** Valida assinatura (rejeita alg=none), expiração e emissor. Qualquer falha resulta em Optional vazio. */
    public Optional<UUID> extractUserId(String token) {
        try {
            String subject = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(props.issuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return Optional.of(UUID.fromString(subject));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
