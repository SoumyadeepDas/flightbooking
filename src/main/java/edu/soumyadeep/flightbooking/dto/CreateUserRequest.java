package edu.soumyadeep.flightbooking.dto;


import edu.soumyadeep.flightbooking.model.User;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateUserRequest {
    @NotBlank private String firstName;
    private String lastName;
    @Email private String email;
    private String phone;
    private User.Category category = User.Category.NONE;
}
