package utec.week07.solution.users.domain;

import java.util.List;
import java.util.regex.Pattern;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import utec.week07.solution.common.ValidationException;
import utec.week07.solution.users.infrastructure.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public User findById(long id) {
        return this.userRepository.findById(id).orElseThrow();
    }

    public User newUser(UserRegisterDTO dto) throws Exception {
        String pwd = dto.getPassword();
        if (!Pattern.matches(".*[A-Z].*", pwd) || !Pattern.matches(".*[0-9].*", pwd)) {
            throw new ValidationException("password: must contain at least one uppercase letter and one digit");
        }

        var user = modelMapper.map(dto, User.class);
        if (StringUtils.hasText(dto.getUsername())) {
            user.setUsername(dto.getUsername());
        } else if (StringUtils.hasText(dto.getEmail())) {
            user.setUsername(dto.getEmail());
        }

        user.setRole("USER");

        var found = this.userRepository.findByUsername(user.getUsername());
        if (found.isPresent()) {
            throw new ValidationException("Username already exists");
        }

        this.userRepository.save(user);
        return user;
    }

    public User loginUser(String username, String password) {
        var user = this.userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return null;
        } else if (user.get().getPassword().equals(password)) {
            return user.get();
        }

        return null;
    }

    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    public void deleteAll() {
        this.userRepository.deleteAll();
    }
}
