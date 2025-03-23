package loremipsum.dev.taskmanagement.config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import loremipsum.dev.taskmanagement.concretes.ApplicationUserDetailsService;
import loremipsum.dev.taskmanagement.concretes.JwtService;
import loremipsum.dev.taskmanagement.repositories.TokenRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ApplicationUserDetailsService appUserDetailService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            username = jwtService.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = appUserDetailService.loadUserByUsername(username);

                if (userDetails.getAuthorities().isEmpty()) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: No Roles Assigned");
                    return;
                }

                if (isTokenRevokedOrExpired(jwt)) {
                    log.warn("Attempt to use a revoked/expired token.");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is revoked or expired");
                    return;
                }

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException e) {
            log.warn("JWT Token is expired: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT Token validation failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
    private boolean isTokenRevokedOrExpired(String token) {
        return tokenRepository.findByToken(token)
                .map(t -> t.isExpired() || t.isRevoked())
                .orElse(true);
    }
}