package sqwore.deko.Security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sqwore.deko.Users.UsersRepository;

@Service
public class CustomUsersDetailsService implements UserDetailsService {
    private final UsersRepository usersRepository;

    public CustomUsersDetailsService(UsersRepository usersRepository){
        this.usersRepository = usersRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usersRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("Пользователь с именем "+username+" не найден"));
    }
}
