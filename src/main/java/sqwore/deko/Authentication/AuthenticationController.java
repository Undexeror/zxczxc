package sqwore.deko.Authentication;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    public AuthenticationController(AuthenticationService authenticationService, UsersRepository usersRepository) {
        this.authenticationService = authenticationService;
        this.usersRepository = usersRepository;
    }

    // Показ формы входа
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";                 // templates/login.html
    }

    // Обработка входа (POST)
    @PostMapping("/login")
    public String processLogin(@ModelAttribute AuthenticationRequest request,
                               HttpServletResponse response,
                               HttpServletRequest httpRequest,
                               RedirectAttributes redirectAttributes) {
        try {
            var authResp = authenticationService.authenticate(request);

            Users user = usersRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            System.out.println("Вход успешен");

            // передаём id через redirect attributes (flash)
            redirectAttributes.addAttribute("id", user.getId());   // ← вот так

            return "redirect:/profile/{id}";   // Spring сам подставит значение
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Неверный логин или пароль");
            return "redirect:/login";
        }
    }

    // Показ формы регистрации
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";              // templates/register.html (уже есть в проекте)
    }

    // Обработка регистрации (POST)
    @PostMapping("/register")
    public String processRegister(@ModelAttribute UserRequests request,
                                  HttpServletResponse response,
                                  RedirectAttributes redirectAttributes) {
        try {
            var authResp = authenticationService.register(request);
            Users user = usersRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            // redirectAttributes.addFlashAttribute("accessToken", authResp.getAccessToken());

            return "redirect:/profile/{id}";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage() != null ? e.getMessage() : "Ошибка регистрации");
            return "redirect:/register";
        }
    }

    // Профиль (пример — адаптируй под свой Security)
    @GetMapping("/profile/{id}")
    public String showProfile(@PathVariable Long id, Model model, Authentication authentication) {

//        if (authentication == null || !authentication.isAuthenticated()) {
//            return "redirect:/login?expired=true";
//        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof Users)) {
            return "redirect:/access-denied";
        }

        Users current = (Users) principal;
        if (!current.getId().equals(id)) {
            return "redirect:/access-denied";   // или 403
        }

        model.addAttribute("username", current.getUsername());
        model.addAttribute("userId", id);
        model.addAttribute("count",current.getCount());

        return "profile";
    }
}
