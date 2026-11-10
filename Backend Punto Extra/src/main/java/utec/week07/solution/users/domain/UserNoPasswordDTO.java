package utec.week07.solution.users.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserNoPasswordDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
}
