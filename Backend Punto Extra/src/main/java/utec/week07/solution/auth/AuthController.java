package utec.week07.solution.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import utec.week07.solution.common.ValidationException;
import utec.week07.solution.users.domain.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private UserService userService;
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthTokenDTO> login(@Valid @RequestBody AuthLoginDTO login) throws Exception {
        var user = userService.loginUser(login.getEmail(), login.getPassword());
        if (user == null) {
            throw new ValidationException("Username does not exist");
        }

        try {
            var token = JwtAuthFilter.generateToken(user);
            return ResponseEntity.ok(new AuthTokenDTO(token));
        } catch (Exception exception){
            return ResponseEntity.internalServerError().build();
        }
    }
}
