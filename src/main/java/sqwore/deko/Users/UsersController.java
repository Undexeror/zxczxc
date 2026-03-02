package sqwore.deko.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersRepository usersRepository;

    @Autowired
    public UsersController(UsersRepository usersRepository){
        this.usersRepository = usersRepository;
    }

    @PostMapping
    public Users createUser(@RequestBody Users user){
        return usersRepository.save(user);
    }

    @GetMapping
    public List<Users> getAllUsers(){
        return usersRepository.findAll();
    }


}
