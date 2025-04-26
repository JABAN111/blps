package org.example.blps_lab1.adapters.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.example.blps_lab1.core.ports.auth.UserService;
import org.example.blps_lab1.adapters.rest.advicer.ExceptionWrapper;
import org.example.blps_lab1.core.exception.security.JwtTokenExpiredException;
import org.example.blps_lab1.core.ports.security.JwtService;


@Component
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {
        try {

            var authHeader = request.getHeader(HEADER_NAME);
            if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }
            var jwt = authHeader.substring(BEARER_PREFIX.length());
            var username = jwtService.extractUserName(jwt);
            if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService
                        .getUserDetailsService()
                        .loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authToken);
                    SecurityContextHolder.setContext(context);
                }
            }
            filterChain.doFilter(request, response);
        } catch (JwtTokenExpiredException | UsernameNotFoundException ex) {
            log.error("JWT token expired");
            response.setStatus(HttpStatus.FORBIDDEN.value());
            var wr = new ExceptionWrapper(new Exception("Invalid account"));
            response.getWriter().write(wr.toString());
            response.getWriter().flush();
        }
    }
}
