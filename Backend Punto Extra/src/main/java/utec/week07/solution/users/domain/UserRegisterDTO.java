package utec.week07.solution.users.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
public class UserRegisterDTO {
    @Email(message = "must be a valid email")
    private String username;

    @Email(message = "must be a valid email")
    private String email;

    @NotNull(message = "is required")
    @NotEmpty(message = "is required")
    @Pattern(regexp = "^[A-Z].+", message = "must start with an uppercase letter")
    private String firstName;

    @NotNull(message = "is required")
    @NotEmpty(message = "is required")
    @Pattern(regexp = "^[A-Z].+", message = "must start with an uppercase letter")
    private String lastName;

    @NotNull(message = "is required")
    @NotEmpty(message = "is required")
    @Length(min = 8, message = "must be at least 8 characters")
    private String password;
}
