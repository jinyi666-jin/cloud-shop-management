package com.cloudshop.config;

import com.cloudshop.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);
        String uri = request.getRequestURI();
        String method = request.getMethod();

        log.info("JwtAuthFilter: method={}, uri={}", method, uri);
        
        if (StringUtils.hasText(token)) {
            log.info("JwtAuthFilter: token found, length={}", token.length());
            try {
                boolean valid = jwtUtil.validateToken(token);
                log.info("JwtAuthFilter: token valid={}", valid);
                if (valid) {
                    String username = jwtUtil.getUsernameFromToken(token);
                    log.info("JwtAuthFilter: setting auth for user={}", username);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("JwtAuthFilter: authentication set successfully");
                } else {
                    log.warn("JwtAuthFilter: validateToken returned false");
                }
            } catch (Exception e) {
                log.error("JwtAuthFilter: validation exception: {}", e.getMessage());
                e.printStackTrace();
            }
        } else {
            log.info("JwtAuthFilter: no token found in request");
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}