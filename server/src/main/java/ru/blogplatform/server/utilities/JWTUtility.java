package ru.blogplatform.server.utilities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JWTUtility {
    private static final String SECRET_KEY = "q8vEwpOC~k5SGpWtQHv5{B2w1EBp#MbnBivSxftgbe8Njw%3KiUP8lUe~884cqL~{Mr9IYsQtql$SwARO|W~?0t8PLyL~}p$PQS";

    public static String generateJWT(long ID) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 864_000_000L);

        return Jwts.builder()
                .setSubject(Long.toString(ID))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static Long parseJWT(String JWT) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(JWT)
                    .getBody();

            return Long.parseLong(claims.getSubject());
        } catch (JwtException e) {
            return null;
        }
    }
}
