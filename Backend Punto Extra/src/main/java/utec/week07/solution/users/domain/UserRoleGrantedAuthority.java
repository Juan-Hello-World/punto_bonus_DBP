package utec.week07.solution.users.domain;

import org.springframework.security.core.GrantedAuthority;

public class UserRoleGrantedAuthority implements GrantedAuthority {
    private final String roleName;

    public UserRoleGrantedAuthority(String roleName) {
        this.roleName =roleName;
    }

    @Override
    public String getAuthority() {
        return roleName;
    }
}
