package olnow.phmobile.restapi;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
class UserService implements UserDetailsService {


    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        if("username".equals(login)) {
            return new User(login, "", new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found with login: " + login);
        }
    }
}
