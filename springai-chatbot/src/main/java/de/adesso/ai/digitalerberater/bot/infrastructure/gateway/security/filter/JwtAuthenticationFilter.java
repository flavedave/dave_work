package de.adesso.ai.digitalerberater.bot.infrastructure.gateway.security.filter;

import java.io.IOException;
import java.util.Base64;
import java.util.Collection;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import de.adesso.ai.digitalerberater.bot.infrastructure.gateway.security.WebSecurityConfig;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtParser jwtParser;

    public JwtAuthenticationFilter() {
        jwtParser = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(WebSecurityConfig.SECRET_KEY)))
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        SecurityContext securityContext = SecurityContextHolder.getContext();

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);

        try {
            Jws<Claims> claims = jwtParser.parseSignedClaims(jwt);

            // ToDo: maybe UUID instead of email? (see JJ-Backend security)
            String subject = claims.getPayload().getSubject();
            Collection<?> roles = claims.getPayload().get("roles", Collection.class);

            securityContext.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                    (AuthenticatedPrincipal) () -> subject,
                    jwt,
                    roles.stream()
                            .map(r -> new SimpleGrantedAuthority(r.toString()))
                            .toList()));

        } catch (ExpiredJwtException e) {
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", "Token has expired");
            return;
        } catch (JwtException | IllegalArgumentException e) {
            writeErrorResponse(
                    response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", "Token could not be verified");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String error, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        String jsonPayload = """
        {
            "status": %d,
            "error": "%s",
            "message": "%s"
        }
        """.formatted(status, error, message);
        response.getWriter().write(jsonPayload);
    }
}
