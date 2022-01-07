package xyz.fivemillion.bulletinboardapi.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.user.User;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtTokenUtil {

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.secret}")
    private String secret;

    public String getEmailFromToken(String token) {
        try {
            return getClaimFromToken(token, Claims::getSubject);
        } catch (Exception e) {
            throw new NotFoundException(HttpStatus.UNAUTHORIZED, "user name not found");
        }
    }

    public String getDisplayNameFromToken(String token) {
        try {
            return (String) getAllClaimsFromToken(token).get("displayName");
        } catch (Exception e) {
            throw new NotFoundException(HttpStatus.UNAUTHORIZED, "display name not found");
        }
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secret))
                .parseClaimsJws(token)
                .getBody();
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public String generateJwtToken(User user) {
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(user.getEmail())
                .setHeader(createHeader())
                .addClaims(createClaims(user))
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(createExpireDateForOneMonth())
                .signWith(SignatureAlgorithm.HS512, createSigningKey())
                .compact();
    }

    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS512");

        return header;
    }

    private Map<String, Object> createClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("displayName", user.getDisplayName());

        return claims;
    }

    private Date createExpireDateForOneMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 30);
        return calendar.getTime();
    }

    private Key createSigningKey() {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
        return new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public Boolean verify(String token) {
        return !isExpired(token);
    }

    private Boolean isExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}
