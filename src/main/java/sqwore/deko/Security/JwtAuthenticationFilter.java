package sqwore.deko.Security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sqwore.deko.Tokens.TokenRepository;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final CustomUsersDetailsService usersDetailsService;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    public JwtAuthenticationFilter(CustomUsersDetailsService usersDetailsService, JwtService jwtService,TokenRepository tokenRepository) {
        this.usersDetailsService = usersDetailsService;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        if (request.getServletPath().contains("/")){
            filterChain.doFilter(request,response);
            return;
        }

        final String authHeader = request.getHeader("Autorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        jwt = authHeader.substring(7);
        var isTokenValidInDb = tokenRepository.findByToken(jwt)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
        username = jwtService.extractUsername(jwt);

        if (username!= null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = this.usersDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, userDetails) && isTokenValidInDb) {
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
