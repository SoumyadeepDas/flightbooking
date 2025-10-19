package edu.soumyadeep.flightbooking.controller;

import edu.soumyadeep.flightbooking.dto.CreateUserRequest;
import edu.soumyadeep.flightbooking.exception.UserNotFoundException;
import edu.soumyadeep.flightbooking.model.User;
import edu.soumyadeep.flightbooking.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static edu.soumyadeep.flightbooking.constant.EndpointConstant.*;

@RestController
@RequestMapping(USER_PATH)
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody final CreateUserRequest req) {
        User user = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .userCategory(req.getCategory())
                .build();
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }


    @GetMapping(FIND_USER_BY_USERID)
    public ResponseEntity<User> getUser(@PathVariable final Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return ResponseEntity.ok(user);
    }
}
