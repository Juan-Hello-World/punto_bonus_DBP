package utec.week07.solution.users.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import utec.week07.solution.common.NewIdDTO;
import utec.week07.solution.users.domain.User;
import utec.week07.solution.users.domain.UserNoPasswordDTO;
import utec.week07.solution.users.domain.UserRegisterDTO;
import utec.week07.solution.users.domain.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<NewIdDTO> register(@Valid @RequestBody UserRegisterDTO dto) throws Exception {
        var newObj = userService.newUser(dto);
        return ResponseEntity.created(null).body(new NewIdDTO(newObj.getId()));
    }

    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserNoPasswordDTO> getUser() {
        var currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(modelMapper.map(userService.findById(currentUser.getId()), UserNoPasswordDTO.class));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserNoPasswordDTO> getUser(@PathVariable long id) {
         return ResponseEntity.ok(modelMapper.map(userService.findById(id), UserNoPasswordDTO.class));
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserNoPasswordDTO>> getUsers() {
        return ResponseEntity.ok(userService.findAll().stream().map(u -> modelMapper.map(u, UserNoPasswordDTO.class))
                                            .toList());
    }
}
