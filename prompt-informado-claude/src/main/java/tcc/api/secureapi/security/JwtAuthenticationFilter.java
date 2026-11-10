package tcc.api.secureapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Não é um @Component de propósito: é instanciado em SecurityConfig para rodar apenas dentro da
 * SecurityFilterChain (evita registro duplicado como filtro de servlet).
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIX = "Bearer ";
    private static final int MAX_TOKEN_LENGTH = 2048;

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(PREFIX)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = header.substring(PREFIX.length()).trim();
            if (!token.isEmpty() && token.length() <= MAX_TOKEN_LENGTH) {
                jwtService.extractUserId(token)
                        .flatMap(userDetailsService::loadUserById)
                        .filter(AuthenticatedUser::isEnabled)
                        .ifPresent(user -> {
                            var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        });
            }
        }
        // Token ausente/inválido: segue sem autenticação; rotas protegidas respondem 401 via EntryPoint.
        chain.doFilter(request, response);
    }
}
