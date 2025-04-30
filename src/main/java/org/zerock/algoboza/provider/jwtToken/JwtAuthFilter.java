package org.zerock.algoboza.provider.jwtToken;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zerock.algoboza.domain.auth.service.CustomUserDetailsService;

import java.io.IOException;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        log.info(uri);
        try {
            // SecurityConfig도 수정바람
            List<String> excludedPaths = List.of("/", "/login", "/signup", "/refresh-token", "/email/verify");
            if (excludedPaths.contains(uri) || uri.startsWith("/api/collection/log/")) {
                filterChain.doFilter(request, response);
                return;
            }

            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
                return;
            }

            String token = authHeader.substring(7);

            String userEmail = tokenProvider.getUserEmail(token);

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication); // 쉽게 꺼낼 수 있도록 context에 저장
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException expiredJwtException) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        } catch (JwtException jwtException) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        } catch (IllegalArgumentException illegalArgumentException) {
            log.error(illegalArgumentException.getMessage());
        }

    }
}
