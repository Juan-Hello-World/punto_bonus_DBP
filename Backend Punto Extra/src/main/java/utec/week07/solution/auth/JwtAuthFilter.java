package utec.week07.solution.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import utec.week07.solution.users.domain.UserRoleGrantedAuthority;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private static String secret = "my-secret";
    private final AuthService authService;

    public JwtAuthFilter(AuthService authService) {
        this.authService = authService;
    }

    public static DecodedJWT validateToken(String token) throws Exception {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        try {
            var verifier = JWT.require(algorithm)
                    .withIssuer("my-app")
                    .withClaimPresence("sub")
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException exception) {
            throw new Exception("Invalid token", exception);
        }
    }

    public static String generateToken(UserDetails userDetails) throws Exception {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        var authorities = userDetails.getAuthorities();
        var authority = authorities.stream().toList().get(0);
        return JWT.create()
                          .withIssuer("my-app")
                          .withClaim("sub", userDetails.getUsername())
                          .withClaim("role", authority.getAuthority())
                          .sign(algorithm);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && StringUtils.startsWithIgnoreCase(authHeader, "Bearer ")) {
            String token = authHeader.substring(7);

            DecodedJWT decodedToken;
            try {
                decodedToken = JwtAuthFilter.validateToken(token);

                String username = decodedToken.getClaim("sub").asString();
                if (StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = authService.loadUserByUsername(username);
                    if (userDetails == null) {
                        throw new Exception("user not found");
                    }

                    var roles = new ArrayList<UserRoleGrantedAuthority>();
                    roles.add(new UserRoleGrantedAuthority(decodedToken.getClaim("role").asString()));
                    SecurityContext context = SecurityContextHolder.getContext();
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, roles);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authToken);
                }
            } catch (Exception e) {
                // ignore the error for now, it will go down the filter stack
                System.err.println("Error in JwtAuthFilter: " + e.getMessage());
            }
        }

        // follow the chain
        filterChain.doFilter(request, response);
    }
}
