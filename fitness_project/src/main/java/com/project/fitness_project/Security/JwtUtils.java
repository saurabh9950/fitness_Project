package com.project.fitness_project.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@Component
public class JwtUtils {

    private String jwtSecret = "YS1zdHJpbmctc2VjcmV0LWF0LWxlYXN0LTI1Ni1iaXRzLWxvbmc=";
    private int jwtExpirationMs = 172800000; // 2 days

    public String getJwtFromHeader(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    public String generateToken(String userId , String role){

        return Jwts.builder()
                .setSubject(userId)
                // store role as simple string claim; authorities will be rebuilt when validating
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    public boolean validateJwtToken(String jwtToken){
        try{
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(jwtToken);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private SecretKey key(){
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    public String getUsernameFromToken(String jwt){
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }

    public Claims getAllClaims(String jwt){
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public List<SimpleGrantedAuthority> getAuthorities(String jwt) {
        Claims claims = getAllClaims(jwt);
        Object roleObj = claims.get("role");
        if (roleObj == null) {
            return List.of();
        }
        // support single role string or list of strings
        if (roleObj instanceof String roleStr) {
            return List.of(new SimpleGrantedAuthority("ROLE_" + roleStr));
        }
        if (roleObj instanceof List<?> roleList) {
            return roleList.stream()
                    .filter(o -> o instanceof String)
                    .map(o -> new SimpleGrantedAuthority("ROLE_" + o))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
