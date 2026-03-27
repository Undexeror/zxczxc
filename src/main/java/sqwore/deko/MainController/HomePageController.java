package sqwore.deko.MainController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//@Controller
//public class HomePageController {
//    @GetMapping("/index")
//    public String HomePage() {
//        return "index";
//    }
//
//    @GetMapping("/register")
//    public String registerPage(){
//        return "register";
//    }
//}
@Controller
public class HomePageController {

    @GetMapping({"/", "/index", "/home"})
    public String showIndex() {
        return "index";           // → откроет templates/index.html
    }

    // Если есть home.html
    @GetMapping("/home")
    public String showHome() {
        return "home";            // → templates/home.html
    }

    // Добавь другие страницы, если нужно
}