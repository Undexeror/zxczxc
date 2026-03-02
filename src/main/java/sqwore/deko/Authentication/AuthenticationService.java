package sqwore.deko.Authentication;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sqwore.deko.DTO.AuthenticationRequest;
import sqwore.deko.DTO.AuthenticationResponse;
import sqwore.deko.DTO.UserRequests;
import sqwore.deko.Security.JwtService;
import sqwore.deko.Users.Users;
import sqwore.deko.Users.UsersRepository;
import tools.jackson.databind.ObjectMapper;

@Service
public class AuthenticationService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UsersRepository usersRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(UserRequests requests){
        Users user = new Users();
        user.setUsername(requests.getUsername());
        user.setPassword(passwordEncoder.encode(requests.getPassword()));
        usersRepository.save(user);

        String jwtToken = jwtService.generationToken(user);
        String refreshToken = jwtService.generationRefreshToken(user);
        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword())
        );
        Users user =usersRepository.findbyUsername(request.getUsername()).orElseThrow();
        String jwtToken = jwtService.generationToken(user);
        String refreshToken = jwtService.generationRefreshToken(user);
        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION); // [4]
        final String refreshToken;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return; // [4]
        }

        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken); // [4]

        if (username != null) {
            var user = this.usersRepository.findbyUsername(username)
                    .orElseThrow(); // [5]

            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generationToken(user);

                AuthenticationResponse authResponse = new AuthenticationResponse(
                        accessToken,
                        refreshToken
                );
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
