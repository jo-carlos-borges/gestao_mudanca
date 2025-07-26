package br.com.base.filter;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

	    String method = request.getMethod();
	    String uri = request.getRequestURI();
	    String ip = request.getRemoteAddr();
	    String username = "anonymousUser"; // Valor padrão

	    // Lógica corrigida para obter o nome de usuário
	    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	    if (principal instanceof UserDetails userDetails) {
	        username = userDetails.getUsername();
	    } else if (principal instanceof String principalString) {
	        username = principalString;
	    }

	    long start = System.currentTimeMillis();

	    log.info("[{}] {} - IP: {} - User: {}", method, uri, ip, username);

	    try {
	        filterChain.doFilter(request, response);

	        long duration = System.currentTimeMillis() - start;
	        int status = response.getStatus();

	        log.info("[{}] {} - {} - {}ms", method, uri, status, duration);

	    } catch (Exception e) {
	        log.error("[{}] {} - Erro: {}", method, uri, e.getMessage(), e);
	        throw e;
	    }
	}
	
}