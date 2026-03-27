package sqwore.deko.Authentication;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sqwore.deko.DTO.AuthenticationRequest;
import sqwore.deko.DTO.AuthenticationResponse;
import sqwore.deko.DTO.UserRequests;
import sqwore.deko.Security.JwtService;
import sqwore.deko.Tokens.Token;
import sqwore.deko.Tokens.TokenRepository;
import sqwore.deko.Tokens.TokenType;
import sqwore.deko.Users.Users;
import sqwore.deko.Users.UsersRepository;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@Service
public class AuthenticationService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

    public AuthenticationService(UsersRepository usersRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, TokenRepository tokenRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.tokenRepository = tokenRepository;
    }

    public AuthenticationResponse register(UserRequests requests){
        Users user = new Users();
        user.setUsername(requests.getUsername());
        user.setPassword(passwordEncoder.encode(requests.getPassword()));
        usersRepository.save(user);
        String jwtToken = jwtService.generationToken(user);
        String refreshToken = jwtService.generationRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user,jwtToken,TokenType.ACCESS);
        saveUserToken(user,refreshToken,TokenType.REFRESH);
        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword())
        );
        Users user =usersRepository.findByUsername(request.getUsername()).orElseThrow();
        revokeAllUserTokens(user);
        String jwtToken = jwtService.generationToken(user);
        String refreshToken = jwtService.generationRefreshToken(user);
        saveUserToken(user,jwtToken,TokenType.ACCESS);
        saveUserToken(user,refreshToken,TokenType.REFRESH);
        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    private void saveUserToken(Users user, String jwtToken, TokenType type){
        var token = new Token(jwtToken,type,user);
        tokenRepository.save(token);
    }
    @Transactional
    public void logout(HttpServletRequest request){
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader== null || !authHeader.startsWith("Bearer ")){
            return;
        }
        final String jwt = authHeader.substring(7);
        Users user = tokenRepository.findByToken(jwt).get().getUser();
        revokeAllUserTokens(user);
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
            var user = this.usersRepository.findByUsername(username)
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

    private void revokeAllUserTokens(Users user){
        List<Token> tokens = tokenRepository.findAllValidTokenByUserId(user.getId());
        if(!tokens.isEmpty()){
            for (int i = 0; i < tokens.size(); i++) {
                Token t = tokens.get(i);
                t.setRevoked(true);
                t.setExpired(true);
            }
            tokenRepository.saveAll(tokens);
        }
    }
}
