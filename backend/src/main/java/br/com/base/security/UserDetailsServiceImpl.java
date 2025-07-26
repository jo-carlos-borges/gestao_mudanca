package br.com.base.security;

import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import br.com.base.domain.User;
import br.com.base.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final HttpServletRequest request;
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) {
	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new NoSuchElementException("User not found"));

	    // 1. Criamos a instância de CustomUserDetails. 
	    // Ele contém a lógica para buscar as "authorities".
	    CustomUserDetails customUserDetails = new CustomUserDetails(user);

	    // 2. Usamos o customUserDetails como principal e passamos sua coleção 
	    // de authorities diretamente para o construtor do Token.
	    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
	            customUserDetails,
	            null,
	            customUserDetails.getAuthorities()); // Correção aqui

	    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	    SecurityContextHolder.getContext().setAuthentication(authentication);

	    return customUserDetails;
	}
	
}