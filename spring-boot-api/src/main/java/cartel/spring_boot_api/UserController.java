package cartel.spring_boot_api;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        User defaultUser = new User();
        defaultUser.setName("default");
        defaultUser.setEmail("default@example.com");
        userRepository.save(defaultUser);
        return userRepository.findAll();
    }

    @GetMapping("/helloWorld")
    public String helloWorld() {
        return "Hello World !";
    }
}