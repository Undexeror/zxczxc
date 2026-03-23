package sqwore.deko.MainController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {
    @GetMapping("/index")
    public String HomePage() {
        return "index";
    }

    @GetMapping("/register")
    public String registerPage(){
        return "register";
    }
}
