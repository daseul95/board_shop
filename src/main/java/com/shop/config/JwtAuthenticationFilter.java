package com.shop.config;

import com.shop.config.jwt.JwtUtils;
import com.shop.service.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();
        // 로그인, 회원가입 등 인증 필요 없는 경로는 필터 스킵
        if (path.startsWith("/member/login") || path.startsWith("/member/signup")) {
            chain.doFilter(request, response);
            return;
        }
        try {
            String token = jwtTokenProvider.extractTokenFromHeader(httpRequest);
            System.out.println("JwtAuthenticationFilter - 토큰: " + token);

            if (token != null && jwtTokenProvider.validateJwtToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println("JwtAuthenticationFilter - 인증 성공: " + auth.getName());
            }
        } catch (Exception e) {
            // 토큰 검증 실패 시 SecurityContext 비우고 401 응답
            System.out.println("JwtAuthenticationFilter - 인증 실패: " + e.getMessage());
            SecurityContextHolder.clearContext();
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        chain.doFilter(request, response);
    }

}