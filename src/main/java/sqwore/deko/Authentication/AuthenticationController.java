package sqwore.deko.Authentication;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sqwore.deko.DTO.AuthenticationRequest;
import sqwore.deko.DTO.AuthenticationResponse;
import sqwore.deko.DTO.UserRequests;
import sqwore.deko.Users.Users;
import sqwore.deko.Users.UsersRepository;

import java.io.IOException;

//@RestController
//@RequestMapping("/profile")
//public class AuthenticationController {
//    private final AuthenticationService authenticationService;
//
//    public AuthenticationController(AuthenticationService authenticationService) {
//        this.authenticationService = authenticationService;
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity<AuthenticationResponse> register(@RequestBody UserRequests request){
//        return ResponseEntity.ok(authenticationService.register(request));
//    }
//
//    @PostMapping("/authenticate")
//    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
//        return ResponseEntity.ok(authenticationService.authenticate(request));
//    }
//
//    @PostMapping("/refresh-token")
//    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        authenticationService.refreshToken(request,response);
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(HttpServletRequest request) {
//        authenticationService.logout(request);
//        return ResponseEntity.ok().body("Logout successful");
//    }
//}
@Controller
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UsersRepository usersRepository;
    private final AuthenticationManager authenticationManager;

    public AuthenticationController(AuthenticationService authenticationService,AuthenticationManager authenticationManager, UsersRepository usersRepository) {
        this.authenticationService = authenticationService;
        this.usersRepository = usersRepository;
        this.authenticationManager = authenticationManager;
    }


    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }


    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(Users user,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        try {
            authenticationService.register(user);
            redirectAttributes.addAttribute("id",user.getId());
            return "redirect:/profile/{id}";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage() != null ? e.getMessage() : "Ошибка регистрации");
            return "redirect:/register";
        }
    }


    @GetMapping("/profile/{id}")
    public String showProfile(Authentication authentication,Model model) {
        Object principal = authentication.getPrincipal();
        System.out.println(principal);
        String username = null;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
            System.out.println("Regular user: " + username);
        }

        Users user = usersRepository.findByUsername(username);


        model.addAttribute("username",user.getUsername());
        model.addAttribute("count",user.getCount());

        return "profile/{id}";
    }
}
