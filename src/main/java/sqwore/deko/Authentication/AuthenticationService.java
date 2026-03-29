package sqwore.deko.Authentication;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sqwore.deko.DTO.AuthenticationRequest;
import sqwore.deko.DTO.AuthenticationResponse;
import sqwore.deko.DTO.UserRequests;
import sqwore.deko.Security.CustomUsersDetailsService;
import sqwore.deko.Users.Users;
import sqwore.deko.Users.UsersRepository;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@Service
public class AuthenticationService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUsersDetailsService customUsersDetailsService;

    public AuthenticationService(UsersRepository usersRepository,CustomUsersDetailsService customUsersDetailsService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.customUsersDetailsService = customUsersDetailsService;
    }
    @Transactional
    public boolean register(Users user){
        if (usersRepository.findByUsername(user.getUsername())!=null){
            return false;
        }
        Users user1 = new Users();
        user1.setUsername(user.getUsername());
        user1.setPassword(passwordEncoder.encode(user.getPassword()));
        usersRepository.save(user);
        return true;

    }
}
