package com.project.fitness_project.Security;


import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException, java.io.IOException {

        String token = jwtUtils.getJwtFromHeader(request);

        if(token != null && jwtUtils.validateJwtToken(token)) {

            String userId = jwtUtils.getUsernameFromToken(token);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userId, null, jwtUtils.getAuthorities(token));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request,response);
    }
}
