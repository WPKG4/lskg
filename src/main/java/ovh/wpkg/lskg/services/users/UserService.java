package ovh.wpkg.lskg.services.users;

import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import ovh.wpkg.lskg.db.entities.User;
import ovh.wpkg.lskg.db.repository.UserRepository;
import ovh.wpkg.lskg.utils.HashUtils;

import java.util.List;
import java.util.Optional;

@Singleton
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    @Transactional
    public User registerUser(String email, String password) {
        if (password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        return userRepository.save(new User(email, HashUtils.generateSHA256(password)));
    }


    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}