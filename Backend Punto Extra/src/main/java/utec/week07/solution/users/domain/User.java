package utec.week07.solution.users.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "app_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String role;

    @Override
    public Collection<UserRoleGrantedAuthority> getAuthorities() {
        return List.of(new UserRoleGrantedAuthority(role));
    }
}
